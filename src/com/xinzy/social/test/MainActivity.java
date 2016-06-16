package com.xinzy.social.test;

import com.xinzy.social.Social;
import com.xinzy.social.share.Entry;
import com.xinzy.social.share.Share;
import com.xinzy.social.share.ShareCallback;
import com.xinzy.social.test.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

	private static final int[] IDS = { R.id.weibo, R.id.qq, R.id.wechat, R.id.share, };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		for (int id : IDS) {
			findViewById(id).setOnClickListener(this);
		}

		Social.init(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.weibo:
			startActivity(new Intent(this, WeiboActivity.class));
			break;

		case R.id.qq:
			startActivity(new Intent(this, QQActivity.class));
			break;

		case R.id.wechat:
			startActivity(new Intent(this, WechatActivity.class));
			break;

		case R.id.share:
			String path = Environment.getExternalStorageDirectory().getAbsolutePath();
			Entry entry = new Entry().setTitle("测试分享到各大平台").setContent("这里是分享的内容，啦啦啦啦啦啦啦啊啦啦啦啦啦啦")
					.setUrl("http://androidweekly.cn/").addImage(path + "/TEST/imgs/1.jpg").setImageUrl(
							"https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
			Share share = Share.getInstance(this);
			share.setCallback(new Callback());
			share.openShareActivity(entry);
			break;

		default:
			break;
		}
	}
	
	class Callback implements ShareCallback {

		@Override
		public void onSuccess(int platform) {
			System.out.println(platform + " Share success ");
		}

		@Override
		public void onFailure(int platform, String msg) {
			System.out.println(platform + " Share failure; msg = " + msg);
		}

		@Override
		public void onCancelled(int platform) {
			System.out.println(platform + " Share cancelled");
		}
	}
}
