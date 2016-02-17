package com.gaodun.common.lib.social.oauth;

import org.json.JSONObject;

import com.gaodun.common.lib.social.Constant;
import com.gaodun.common.lib.social.Platform;
import com.gaodun.common.lib.social.SocialException;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.AsyncWeiboRunner;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.net.WeiboParameters;
import com.tencent.connect.UserInfo;
import com.tencent.connect.common.Constants;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

public class Oauth {
	
	static final boolean DEBUG = true;
	

	private Activity mActivity;
	private OauthCallback mCallback;

	private int mPlatform;
	private static Oauth mInstance;

	//-----------------------------------
	// Weibo
	//-----------------------------------
	private AuthInfo mSinaAuthInfo;
	private SsoHandler mSinaSsoHandler;
	private Oauth2AccessToken mSinaAccessToken;

	//-----------------------------------
	// QQ
	//-----------------------------------
	private Tencent mTencent;

	//-----------------------------------
	// Wechat
	//-----------------------------------
	private IWXAPI mApi;
	
	public static final Oauth getInstance(Activity activity) {
	
		if (mInstance == null) {
			mInstance = new Oauth(activity);
		}
		
		return mInstance;
	}

	private Oauth(Activity activity) {
		mActivity = activity;

		mSinaAuthInfo = new AuthInfo(activity, Platform.WEIBO_KEY, Platform.WEIBO_REDIRECT_URL, null);
		mSinaSsoHandler = new SsoHandler(activity, mSinaAuthInfo);
		
		mTencent = Tencent.createInstance(Platform.QQ_APPID, mActivity.getApplicationContext());
		
		mApi = WXAPIFactory.createWXAPI(mActivity, Platform.WECHAT_APPID, false);
	}

	public void onWeibo() {
		mPlatform = Platform.WEIBO;

		mSinaSsoHandler.authorize(new SinaOauthCallback());
	}

	public void onQQ() {
		mPlatform = Platform.QQ;
		
		if (! mTencent.isSessionValid()) {
			mTencent.login(mActivity, Platform.QQ_SCOPE, new QQOauthCallback(QQOauthCallback.TYPE_OAUTH));
		} else {
			if (DEBUG) {
				System.out.println("Tencent is not session valid");
			}
			if (mCallback != null) {
				mCallback.onError(Platform.QQ, new SocialException("QQ cannot be valid"));
			}
		}
	}
	
	public void onWechat() {
		mApi.registerApp(Platform.WECHAT_APPID);
		
		final SendAuth.Req req = new SendAuth.Req();
		req.scope = Platform.WECHAT_SCOPE;
		req.state = Platform.WECHAT_STATE;
		mApi.sendReq(req);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (mPlatform == Platform.WEIBO) {
			mSinaSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		} else if (mPlatform == Platform.QQ) {
			Tencent.onActivityResultData(requestCode, resultCode, data, null);
		}
	}

	public void setCallback(OauthCallback callback) {
		this.mCallback = callback;
	}
	
	OauthCallback getCallback() {
		return mCallback;
	}
	
	IWXAPI getWXApi() {
		return mApi;
	}
	
	class QQOauthCallback implements IUiListener {
		static final int TYPE_OAUTH = 1;
		static final int TYPE_INFO = 2;
		
		private int type;
		
		public QQOauthCallback(int type) {
			this.type = type;
		}

		@Override
		public void onCancel() {
			
			if (DEBUG) {
				System.out.println("QQ login cancelled.");
			}

			if (mCallback != null) {
				mCallback.onCancelled(Platform.QQ);
			}
		}

		@Override
		public void onComplete(Object object) {
			
			if (DEBUG) {
				System.out.println("QQ onComplete : " + object);
			}
			
			if (mCallback != null) {
				
				if (object == null) {
					if (type == TYPE_OAUTH) {
						mCallback.onFailure(Platform.QQ, "QQ oauth return null");
					} else {
						mCallback.onFailure(Platform.QQ, "QQ fetch user info return null");
					}
				} else if (object instanceof JSONObject) {
					
					JSONObject json = (JSONObject) object;
					if (type == TYPE_OAUTH) {
						String token = json.optString(Constants.PARAM_ACCESS_TOKEN);
						String expires = json.optString(Constants.PARAM_EXPIRES_IN);
						String openId = json.optString(Constants.PARAM_OPEN_ID);
						if (!TextUtils.isEmpty(token) && !TextUtils.isEmpty(expires) && !TextUtils.isEmpty(openId)) {
							mTencent.setAccessToken(token, expires);
							mTencent.setOpenId(openId);
							
							UserInfo userInfo = new UserInfo(mActivity.getApplicationContext(), mTencent.getQQToken());
							userInfo.getUserInfo(new QQOauthCallback(TYPE_INFO));
						}
					} else {
						U u = U.qq(json);
						
						if (u == null) {
							mCallback.onFailure(Platform.QQ, "fetch user info fail");
						} else {
							u.setOpenid(mTencent.getOpenId());
							u.setPlat(Platform.QQ);
							mCallback.onSuccess(Platform.QQ, u);
						}
					}
				} else {
					if (type == TYPE_OAUTH) { 
						mCallback.onFailure(Platform.QQ, "QQ oauth return data cannot be cast to JSONObject");
					} else {
						mCallback.onFailure(Platform.QQ, "QQ fetch user info return data cannot be cast to JSONObject");
					}
				}
			}
		}

		@Override
		public void onError(UiError error) {
			
			if (DEBUG) {
				System.out.println(error == null ? "error is null"
						: "error message = " + error.errorMessage + "; detail = " + error.errorDetail);
			}
			
			if (mCallback != null) {
				
				SocialException e = new SocialException(error.errorMessage);
				mCallback.onError(Platform.QQ, e);
			}
		}
	}

	class SinaOauthCallback implements WeiboAuthListener, RequestListener {

		@Override
		public void onCancel() {

			if (mCallback != null) {
				mCallback.onCancelled(Platform.WEIBO);
			}
		}

		@Override
		public void onComplete(Bundle bundle) {
			mSinaAccessToken = Oauth2AccessToken.parseAccessToken(bundle);
			
			if (mSinaAccessToken.isSessionValid()) {

				WeiboParameters param = new WeiboParameters(Platform.WEIBO_KEY);
				param.put("uid", Long.parseLong(mSinaAccessToken.getUid()));
				param.put(Constant.KEY_ACCESS_TOKEN, mSinaAccessToken.getToken());
				new AsyncWeiboRunner(mActivity.getApplicationContext()).requestAsync(Constant.WEIBO_API_USER, param,
						Constant.HTTPMETHOD_GET, this);
			} else {
				String msg = "";
				String code = bundle.getString("code");
				if (!TextUtils.isEmpty(code)) {
					msg = "Obtained code " + code;
				}

				if (mCallback != null) {
					mCallback.onFailure(Platform.WEIBO, msg);
				}
			}
		}

		@Override
		public void onComplete(String response) {
			
			if (DEBUG) {
				System.out.println("Weibo get user info: " + response);
			}
			
			U user = null;
			if (!TextUtils.isEmpty(response)) {
				user = U.weibo(response);
			}

			if (mCallback != null) {
				if (user == null) {
					mCallback.onFailure(Platform.WEIBO, "Get user info error");
				} else {
					user.setPlat(Platform.WEIBO);
					mCallback.onSuccess(Platform.WEIBO, user);
				}
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {

			if (mCallback != null) {
				SocialException ex = new SocialException(e != null ? e.getMessage() : "", e);
				mCallback.onError(Platform.WEIBO, ex);
			}
		}
	}
}
