package com.gaodun.common.lib.social.oauth;

import org.json.JSONException;
import org.json.JSONObject;

import com.gaodun.common.lib.social.Constant;
import com.gaodun.common.lib.social.Platform;
import com.gaodun.common.lib.social.Social;
import com.gaodun.common.lib.social.SocialException;
import com.gaodun.common.lib.social.oauth.AsyncRequest.RequestCallback;
import com.gaodun.common.lib.social.share.Share;
import com.gaodun.common.lib.social.share.ShareCallback;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.modelmsg.SendAuth.Resp;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public abstract class WechatEntryActivity extends Activity implements IWXAPIEventHandler {

	
	protected IWXAPI mApi;
	
	private Share mShare;
	private OauthCallback mOauthCallback;
	private ShareCallback mShareCallback;

	private String openid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		mApi = Oauth.getInstance(this).getWXApi();
		if (mApi == null) {
			finish();
			return ;
		}
		mOauthCallback = Oauth.getInstance(this).getCallback();
		mShare = Share.getInstance(this);
		mShareCallback = mShare.getCallback();
		
		mApi.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		setIntent(intent);

		mApi.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
		
		if (Social.DEBUG) {
			System.out.println("Wechat Entry Activity onReq");
		}
		
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		
		if (Social.DEBUG) {
			System.out.println("Wechat Entry Activity onResp");
		}

		if (resp instanceof SendAuth.Resp) { // 微信Oauth 认证返回
			Resp authRes = (Resp) resp;

			if (mOauthCallback != null && resp.checkArgs()) {

				switch (authRes.errCode) {
				case BaseResp.ErrCode.ERR_OK:
					openid = authRes.openId;
					if (Social.DEBUG) {
						System.out.println("openid = " + openid);
					}
					loadAccessToken(authRes.code);
					break;

				case BaseResp.ErrCode.ERR_USER_CANCEL:
					mOauthCallback.onCancelled(Platform.WECHAT);
					break;

				case BaseResp.ErrCode.ERR_AUTH_DENIED:
					mOauthCallback.onFailure(Platform.WECHAT, "User denied");
					break;

				default:
					mOauthCallback.onError(Platform.WECHAT, new SocialException(authRes.errStr));
					break;
				}
			}
		} else if (resp instanceof SendMessageToWX.Resp) {
			SendMessageToWX.Resp msgRes = (SendMessageToWX.Resp) resp;
			
			if (mShareCallback != null && msgRes.checkArgs()) {
				switch (msgRes.errCode) {
				case BaseResp.ErrCode.ERR_OK:
					mShareCallback.onSuccess(mShare.getPlatform());
					break;

				case BaseResp.ErrCode.ERR_USER_CANCEL:
					mShareCallback.onCancelled(mShare.getPlatform());
					break;

				case BaseResp.ErrCode.ERR_AUTH_DENIED:
					mShareCallback.onFailure(mShare.getPlatform(), "User denied");
					break;

				default:
					mShareCallback.onFailure(mShare.getPlatform(), msgRes.errStr);;
					break;
				}
			}
		}
		
		finish();
	}

	private void loadAccessToken(String code) {
		String url = String.format(Constant.WECHAT_API_ACCESS_TOKEN, code);
		new AsyncRequest(url).doGet(new WechatCallback(WechatCallback.TYPE_ACCESS_TOKEN));
	}
	
	private void loadUserInfo(String accessToken) {
		String url = String.format(Constant.WECHAT_API_USERINFO, accessToken, openid);
		new AsyncRequest(url).doGet(new WechatCallback(WechatCallback.TYPE_USER_INFO));
	}
	
	private void error(SocialException ex) {
		if (mOauthCallback != null) {
			mOauthCallback.onError(Platform.WECHAT, ex);
		}
	}

	private class WechatCallback implements RequestCallback {

		static final int TYPE_ACCESS_TOKEN = 0x1;
		static final int TYPE_USER_INFO = 0x2;
		private int type;

		public WechatCallback(int type) {
			this.type = type;
		}

		@Override
		public void onSuccess(String result) {
			
			if (Social.DEBUG) {
				System.out.println("wechat api: " + result);
			}
			
			if (type == TYPE_ACCESS_TOKEN) {
				try {
					JSONObject json = new JSONObject(result);
					
					if (json.has("errcode")) {
						String errcode = json.optString("errcode");
						String errmsg = json.optString("errmsg");
						
						error(new SocialException("get access token error; error code = " + errcode + "; error msg = " + errmsg));
					} else {
						String accessToken = json.getString("access_token");
						loadUserInfo(accessToken);
					}
				} catch (JSONException e) {
					error(new SocialException("get access token error; return data: " + result));
				}
			} else if (type == TYPE_USER_INFO) {
				try {
					JSONObject json = new JSONObject(result);
					
					if (json.has("errcode")) {
						String errcode = json.optString("errcode");
						String errmsg = json.optString("errmsg");
						error(new SocialException("get user info error; error code = " + errcode + "; error msg = " + errmsg));
					} else {
						U u = U.wechat(json);
						
						if (u == null) {
							error(new SocialException("get user info error"));
						} else {
							if (mOauthCallback != null) {
								mOauthCallback.onSuccess(Platform.WECHAT, u);
							}
						}
					}
				} catch (JSONException e) {
					error(new SocialException("get access token error; return data: " + result));
				}
			}
		}

		@Override
		public void onError(Exception e) {
			String msg = "";
			
			if (type == TYPE_ACCESS_TOKEN) {
				msg = "get access token error";
			} else if (type == TYPE_USER_INFO) {
				msg = "get user info error";
			}
			
			error(new SocialException(msg, e));
		}
	}
}
