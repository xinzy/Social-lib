package com.gaodun.social;

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

public class QQActivity extends Activity implements OnClickListener {

	private TextView mTextView;
	
	private static final int[] IDS = {R.id.login, R.id.qq, R.id.qzone};

	private Oauth mOauth;
	private Share mShare;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_qq);
		setTitle("QQ");
		
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
		switch (v.getId()) {
		case R.id.login:
			mOauth.onQQ();
			break;
			
		case R.id.qq:
			Entry e = new Entry().setAppname("测试啦啦啦").setTitle("分享").setContent("内容来啦来啦").setUrl("https://www.baidu.com")
				.setImageUrl("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
			mShare.qq(e);
			break;
			
		case R.id.qzone:
			String root = Environment.getExternalStorageDirectory().getAbsolutePath();
			Entry entry = new Entry().setAppname("测试啦啦啦").setTitle("分享").setContent("内容来啦来啦").setUrl("https://www.baidu.com")
				.addImage(root + "/TEST/imgs/1.jpg").addImage(root + "/TEST/imgs/2.jpg").addImage(root + "/TEST/imgs/3.jpg");
			mShare.qzone(entry);
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
	
	class ShareBack implements ShareCallback {

		@Override
		public void onSuccess(int platform) {
			mTextView.append("Share onSuccess: " + platform);
		}

		@Override
		public void onFailure(int platform, String msg) {
			mTextView.append("Share onFailure: " + platform + "; msg = " + msg);
		}

		@Override
		public void onCancelled(int platform) {
			mTextView.append("Share onCancelled: " + platform);
		}
	}
	
}
