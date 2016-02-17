package com.gdwx.tiku.kjcy.wxapi;

import com.gdwx.tiku.kjcy.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class MainActivity extends Activity implements OnClickListener {

	private static final int[] IDS = {
			R.id.weibo,
			R.id.qq,
			R.id.wechat,
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		for (int id : IDS) {
			findViewById(id).setOnClickListener(this);
		}
		
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

		default:
			break;
		}
	}
}
