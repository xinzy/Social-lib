package com.gdwx.tiku.kjcy.wxapi;

import com.gaodun.common.lib.social.SocialException;
import com.gaodun.common.lib.social.oauth.Oauth;
import com.gaodun.common.lib.social.oauth.OauthCallback;
import com.gaodun.common.lib.social.oauth.U;
import com.gaodun.common.lib.social.share.Entry;
import com.gaodun.common.lib.social.share.Share;
import com.gaodun.common.lib.social.share.ShareCallback;
import com.gdwx.tiku.kjcy.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class WechatActivity extends Activity implements OnClickListener {
	private TextView mTextView;

	private static final int[] IDS = { R.id.login, R.id.favor, R.id.timeline, R.id.session, };

	private Oauth mOauth;
	private Share mShare;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wechat);
		setTitle("Wechat");

		mTextView = (TextView) findViewById(R.id.infoText);
		for (int id : IDS) {
			findViewById(id).setOnClickListener(this);
		}
		mOauth = Oauth.getInstance(this);
		mOauth.setCallback(new OauthBack());
		
		mShare = Share.getInstance(this);
		mShare.setCallback(new ShareBack());
	}

	@Override
	public void onClick(View v) {
		String path = Environment.getExternalStorageDirectory().getAbsolutePath();
		Entry entry = new Entry().setTitle("来自杨杨的分享").setContent("哈哈哈哈哈哈哈，这里是内容")
				.setUrl("http://androidweekly.cn/").addImage(path + "/TEST/imgs/1.jpg");
		
		switch (v.getId()) {
		case R.id.login:
			mOauth.onWechat();
			break;

		case R.id.session:
			mShare.session(entry);
			break;
			
		case R.id.timeline:
			mShare.timeline(entry);
			break;
			
		case R.id.favor:
			mShare.favor(entry);
			break;

		default:
			break;
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		mOauth.onActivityResult(requestCode, resultCode, data);
	}

	class ShareBack implements ShareCallback {

		@Override
		public void onSuccess(int platform) {
			mTextView.append("onSuccess " + platform);
		}

		@Override
		public void onFailure(int platform, String msg) {
			mTextView.append("onFailure " + platform + "; msg = " + msg);
		}

		@Override
		public void onCancelled(int platform) {
			mTextView.append("onCancelled " + platform);
		}
		
	}
	
	class OauthBack implements OauthCallback {

		@Override
		public void onSuccess(int platform, U user) {
			mTextView.setText("onSuccess: " + platform + " " + user);
		}

		@Override
		public void onFailure(int platform, String msg) {

			mTextView.setText("onFailure: " + platform + " " + msg);
		}

		@Override
		public void onCancelled(int platform) {

			mTextView.setText("onCancelled: " + platform);
		}

		@Override
		public void onError(int platform, SocialException ex) {

			L.e("onError: " + platform, ex);
		}
	}
}
