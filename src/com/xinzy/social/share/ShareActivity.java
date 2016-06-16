package com.xinzy.social.share;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ShareActivity extends Activity implements OnClickListener, OnItemClickListener {
	
	public static final String PARAM_ENTRY = "entry";
	
	private static final int COLOR_TRANSLUCENCE = 0x60000000;
	
	private LinearLayout mContainer;
	private GridView mGridView;

	/**
	 * 底部全选删除按钮的显示动画
	 */
	private Animation mShowAnim;
	/**
	 * 底部全选删除按钮的隐藏动画
	 */
	private Animation mHideAnim;	

	private Share mShare;
	private Entry mEntry;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		
		try {
			mEntry = (Entry) getIntent().getSerializableExtra(PARAM_ENTRY);
		} catch (Exception e) {
		}
		if (mEntry == null) {
			finish();
			return ;
		}
		
		mContainer = new LinearLayout(this);
		mContainer.setOnClickListener(this);
		mContainer.setBackgroundColor(COLOR_TRANSLUCENCE);
		setContentView(mContainer);
		
		mGridView = new GridView(this);
		mGridView.setCacheColorHint(Color.TRANSPARENT);
		mGridView.setBackgroundColor(Color.WHITE);
		mGridView.setNumColumns(3);
		mGridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
		mGridView.setOnItemClickListener(this);
		
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM;
		mContainer.addView(mGridView, params);
		mGridView.setAdapter(new ShareAdapter());
		
		mShowAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, 
				Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, -0f);
		mShowAnim.setDuration(300);
		mHideAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f, 
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 1.0f);
		mHideAnim.setDuration(300);
		
		mGridView.startAnimation(mShowAnim);
		
		mShare = Share.getInstance(this);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		mShare.onActivityResult(requestCode, resultCode, data);
	}
	
	@Override
	public void onClick(View v) {
		close();
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		close();
		
		switch (position) {
		case 0:
			mShare.session(mEntry);
			break;
			
		case 1:
			mShare.timeline(mEntry);
			break;
			
		case 2:
			mShare.favor(mEntry);
			break;
			
		case 3:
			mShare.weibo(mEntry);
			break;
			
		case 4:
			mShare.qq(mEntry);
			break;
			
		case 5:
			mShare.qzone(mEntry);
			break;

		default:
			break;
		}
	}
	
	@Override
	public void onBackPressed() {
		close();
	}
	
	private void close() {
		mGridView.startAnimation(mHideAnim);
		mHideAnim.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mGridView.setVisibility(View.GONE);;
				finish();
			}
		});
	}
	
	@Override
	public void finish() {
		super.finish();
		overridePendingTransition(0, 0);
	}
	
	class ShareAdapter extends BaseAdapter {
		
		private final String[] logos = {
				"social/logo_wechat.png",
				"social/logo_wechatmoments.png",
				"social/logo_wechatfavorite.png",
				"social/logo_weibo.png",
				"social/logo_qq.png",
				"social/logo_qzone.png",
		};
		private final String[] names = {
				"微信好友",
				"朋友圈",
				"微信收藏",
				"微博",
				"QQ",
				"QQ空间",
		};
		
		public ShareAdapter() {
		}

		@Override
		public int getCount() {
			return 6;
		}

		@Override
		public Object getItem(int position) {
			return names[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			LinearLayout container = new LinearLayout(ShareActivity.this);
			container.setOrientation(LinearLayout.VERTICAL);
			container.setPadding(36, 36, 36, 36);
			container.setGravity(Gravity.CENTER_HORIZONTAL);
			
			ImageView img = new ImageView(ShareActivity.this);
			LinearLayout.LayoutParams imgParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT); 
			container.addView(img, imgParams);
			try {
				Bitmap bitmap = BitmapFactory.decodeStream(getAssets().open(logos[position]));
				img.setImageBitmap(bitmap);
			} catch (IOException e) {
			}
			
			TextView text = new TextView(ShareActivity.this);
			text.setTextColor(0xFF666666);
			text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
			text.setText(names[position]);
			text.setGravity(Gravity.CENTER);
			LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT); 
			textParams.topMargin = 12;
			container.addView(text, textParams);
			
			return container;
		}
	}
}
