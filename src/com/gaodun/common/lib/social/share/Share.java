package com.gaodun.common.lib.social.share;

import java.util.ArrayList;

import com.gaodun.common.lib.social.Platform;
import com.gaodun.common.lib.social.SocialUtil;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.utils.Utility;
import com.tencent.connect.share.QQShare;
import com.tencent.connect.share.QzoneShare;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.open.utils.ThreadManager;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;

public class Share {
	
	static final boolean DEBUG = true;

	private Activity mActivity;
	private ShareCallback mCallback;

	private int mPlatform;

	// -----------------------------------
	// QQ, QZone
	// -----------------------------------
	private Tencent mTencent;
	private BaseCallback mShareCallback;

	// -----------------------------------
	// Wechat
	// -----------------------------------
	private IWXAPI mApi;

	// -----------------------------------
	// Weibo
	// -----------------------------------
	private IWeiboShareAPI mWeiboShareAPI;

	private static Share mInstance;

	public static final Share getInstance(Activity activity) {

		if (mInstance == null) {
			mInstance = new Share(activity);
		}
		return mInstance;
	}

	private Share(Activity activity) {
		mActivity = activity;

		mTencent = Tencent.createInstance(Platform.QQ_APPID, mActivity.getApplicationContext());
		mApi = WXAPIFactory.createWXAPI(mActivity.getApplicationContext(), Platform.WECHAT_APPID, false);
		mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(mActivity.getApplicationContext(), Platform.WEIBO_KEY);
		mWeiboShareAPI.registerApp();
	}

	public void setCallback(ShareCallback callback) {
		this.mCallback = callback;
	}

	public ShareCallback getCallback() {
		return mCallback;
	}

	public int getPlatform() {
		return mPlatform;
	}
	
	public void handleWeiboResponse(Intent intent) {
		mWeiboShareAPI.handleWeiboResponse(intent, new WeiboResponse());
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {

		if (mPlatform == Platform.SHARE_QZONE) {
			Tencent.onActivityResultData(requestCode, resultCode, data, mShareCallback);
		}
	}

	/**
	 * 微博
	 */
	public void weibo(Entry entry) {
		mPlatform = Platform.SHARE_WEIBO;

		WeiboMultiMessage message = new WeiboMultiMessage();

		String text = entry.getContent();
		if (!TextUtils.isEmpty(text)) {
			text = text.substring(0, text.length() > 140 ? 140 : text.length());
		}
		TextObject textObject = new TextObject();
		textObject.text = text;
		message.textObject = textObject;

		byte[] bytes = null;
		final ArrayList<String> imgs = entry.getImagePaths();
		if (imgs != null && imgs.size() > 0) {
			String img = imgs.get(0);
			Bitmap bitmap = BitmapFactory.decodeFile(img);
			if (bitmap != null) {
				bytes = SocialUtil.bmpToByteArray(bitmap);
			}
		}

		if (bytes != null) {
			ImageObject imageObject = new ImageObject();
			imageObject.imageData = bytes;
			message.imageObject = imageObject;
		}

		String url = entry.getUrl();
		if (!TextUtils.isEmpty(url)) {
			WebpageObject webpageObject = new WebpageObject();
			webpageObject.identify = Utility.generateGUID();
			webpageObject.title = entry.getTitle();
			webpageObject.description = entry.getContent();
			webpageObject.actionUrl = url;
			webpageObject.defaultText = "";
			if (bytes != null) {
				webpageObject.thumbData = bytes;
			}

			message.mediaObject = webpageObject;
		}

		SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
		request.transaction = System.currentTimeMillis() + "";
		request.multiMessage = message;

//		AuthInfo authInfo = new AuthInfo(mActivity.getApplicationContext(), Platform.WEIBO_KEY,
//				Platform.WEIBO_REDIRECT_URL, Constant.WEIBO_SCOPE);
//		WeiboCallback callback = new WeiboCallback();
		mWeiboShareAPI.sendRequest(mActivity, request);
	}

	/**
	 * 微信好友
	 */
	public void session(Entry entry) {
		mPlatform = Platform.SHARE_WECHAT_SESSION;
		wechat(entry, SendMessageToWX.Req.WXSceneSession);
	}

	/**
	 * 朋友圈
	 */
	public void timeline(Entry entry) {
		mPlatform = Platform.SHARE_WECHAT_TIMELINE;
		wechat(entry, SendMessageToWX.Req.WXSceneTimeline);
	}

	/**
	 * 微信收藏
	 */
	public void favor(Entry entry) {
		mPlatform = Platform.SHARE_WECHAT_FAVORITE;
		wechat(entry, SendMessageToWX.Req.WXSceneFavorite);
	}

	private void wechat(Entry entry, int type) {
		WXWebpageObject webpage = new WXWebpageObject();
		webpage.webpageUrl = entry.getUrl();

		WXMediaMessage msg = new WXMediaMessage(webpage);
		msg.title = entry.getTitle();
		msg.description = entry.getContent();

		final ArrayList<String> imgs = entry.getImagePaths();
		if (imgs != null && imgs.size() > 0) {
			String img = imgs.get(0);
			Bitmap bitmap = BitmapFactory.decodeFile(img);
			if (bitmap != null) {
				msg.thumbData = SocialUtil.bmpToByteArray(bitmap);
			}
		}

		SendMessageToWX.Req req = new SendMessageToWX.Req();
		req.transaction = SocialUtil.buildTransaction("transaction" + type);
		req.message = msg;
		req.scene = type;
		mApi.sendReq(req);
	}

	/**
	 * QQ 好友
	 */
	public void qq(Entry entry) {

		if (entry == null || TextUtils.isEmpty(entry.getTitle()) || TextUtils.isEmpty(entry.getContent())
				|| TextUtils.isEmpty(entry.getUrl())) {

			if (mCallback != null) {
				mCallback.onFailure(Platform.SHARE_QQ, "invalid entry");
			}
		} else {

			mPlatform = Platform.SHARE_QQ;
			final Bundle params = new Bundle();

			params.putString(QQShare.SHARE_TO_QQ_TITLE, entry.getTitle());
			params.putString(QQShare.SHARE_TO_QQ_SUMMARY, entry.getContent());
			params.putString(QQShare.SHARE_TO_QQ_TARGET_URL, entry.getUrl());
			params.putString(QQShare.SHARE_TO_QQ_APP_NAME, entry.getAppname());
			params.putInt(QQShare.SHARE_TO_QQ_KEY_TYPE, QQShare.SHARE_TO_QQ_TYPE_DEFAULT);

			if (!TextUtils.isEmpty(entry.getImageUrl())) {
				params.putString(QQShare.SHARE_TO_QQ_IMAGE_URL, entry.getImageUrl());
			}

			mShareCallback = new BaseCallback(Platform.SHARE_QQ);
			ThreadManager.getMainHandler().post(new Runnable() {
				@Override
				public void run() {
					mTencent.shareToQQ(mActivity, params, mShareCallback);
				}
			});
		}
	}

	/**
	 * Qzone
	 */
	public void qzone(Entry entry) {

		if (entry == null || TextUtils.isEmpty(entry.getTitle()) || TextUtils.isEmpty(entry.getContent())
				|| TextUtils.isEmpty(entry.getUrl())) {

			if (mCallback != null) {
				mCallback.onFailure(Platform.SHARE_QZONE, "invalid entry");
			}
		} else {

			mPlatform = Platform.SHARE_QZONE;
			final Bundle params = new Bundle();

			params.putInt(QzoneShare.SHARE_TO_QZONE_KEY_TYPE, QzoneShare.SHARE_TO_QZONE_TYPE_IMAGE_TEXT);
			params.putString(QzoneShare.SHARE_TO_QQ_TITLE, entry.getTitle());
			params.putString(QzoneShare.SHARE_TO_QQ_SUMMARY, entry.getContent());
			params.putString(QzoneShare.SHARE_TO_QQ_TARGET_URL, entry.getUrl());

			ArrayList<String> path = entry.getImagePaths();
			if (path != null && path.size() > 0) {
				params.putStringArrayList(QzoneShare.SHARE_TO_QQ_IMAGE_URL, path);
			}

			mShareCallback = new BaseCallback(Platform.SHARE_QZONE);
			ThreadManager.getMainHandler().post(new Runnable() {
				@Override
				public void run() {
					mTencent.shareToQzone(mActivity, params, mShareCallback);
				}
			});
		}
	}

	// QQ 分享Callback
	class BaseCallback implements IUiListener {

		private int platform;

		public BaseCallback(int platform) {
			this.platform = platform;
		}

		@Override
		public void onCancel() {
			if (mCallback != null) {
				mCallback.onCancelled(platform);
			}
		}

		@Override
		public void onComplete(Object object) {
			if (mCallback != null) {
				mCallback.onSuccess(platform);
			}
		}

		@Override
		public void onError(UiError error) {
			if (mCallback != null) {
				mCallback.onFailure(platform, error != null ? error.errorMessage : "");
			}
		}
	}

	/**
	 * 微博分享返回
	 */
	class WeiboCallback implements WeiboAuthListener {

		@Override
		public void onCancel() {
			if (mCallback != null) {
				mCallback.onCancelled(Platform.SHARE_WEIBO);
			}
		}

		@Override
		public void onComplete(Bundle data) {
			if (mCallback != null) {
				mCallback.onSuccess(Platform.SHARE_WEIBO);
			}
		}

		@Override
		public void onWeiboException(WeiboException e) {
			if (mCallback != null) {
				mCallback.onFailure(Platform.SHARE_WEIBO, "" + e);
			}
		}
	}
	
	class WeiboResponse implements IWeiboHandler.Response {
		@Override
		public void onResponse(BaseResponse response) {
			if (DEBUG) {
				System.out.println("weibo hanlder onResponse");
			}
			
			if (mCallback != null) {
				switch (response.errCode) {
				case WBConstants.ErrorCode.ERR_OK:
					mCallback.onSuccess(Platform.SHARE_WEIBO);
					break;

				case WBConstants.ErrorCode.ERR_CANCEL:
					mCallback.onCancelled(Platform.SHARE_WEIBO);
					break;

				case WBConstants.ErrorCode.ERR_FAIL:
					mCallback.onFailure(Platform.SHARE_WEIBO, response.errMsg);
					break;
					
				default:
					break;
				}

			}
		}
	}
}
