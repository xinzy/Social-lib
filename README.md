# Social-lib
社会化分享和第三方登录封装 针对国内常用的微博，微信，QQ进行分装

## 配置
1. 使用之前请在assert/social/SocialConfig.xml文件中配置相关信息。如下

	<?xml version="1.0" encoding="utf-8"?>
	<Social>
	    
	    <Weibo 
	        key="1108795777"
	        secret="bb38a46a0bbd41c90a1b82c61c24073d"
	        redirectUrl="http://www.gaodun.com/oauth/weibo/callback.php" />
	    
	    <Wechat
	        appid="wx8baf1de2a92d60c4"
	        secret="d4624c36b6795d1d99dcf0547af5443d" />
	    
	    <QQ
	        appid="101055478"
	        appkey="a15192b69926e129f53c1f12942f833a" />
	    
	</Social>

1. Android Manifest 

	<!-- 如果您打算使用一键分享功能，请将该Activity添加到配置文件中 -->
	<activity
	    android:name="com.gaodun.common.lib.social.share.ShareActivity"
	    android:theme="@android:style/Theme.Translucent.NoTitleBar"
	    android:screenOrientation="portrait" />

	<!-- 以下是第三方平台必须的Activity -->
	<activity
	    android:name="com.sina.weibo.sdk.component.WeiboSdkBrowser"
	    android:configChanges="keyboardHidden|orientation"
	    android:exported="false"
	    android:windowSoftInputMode="adjustResize" />
	<activity
	    android:name="com.tencent.connect.common.AssistActivity"
	    android:configChanges="orientation|keyboardHidden"
	    android:screenOrientation="behind"
	    android:theme="@android:style/Theme.Translucent.NoTitleBar" />
	<activity
	    android:name="com.tencent.tauth.AuthActivity"
	    android:launchMode="singleTask"
	    android:noHistory="true" >
	    <intent-filter>
	        <action android:name="android.intent.action.VIEW" />
	
	        <category android:name="android.intent.category.DEFAULT" />
	        <category android:name="android.intent.category.BROWSABLE" />
	
			<!-- 注意这个地方 tencent + QQ的appid -->
	        <data android:scheme="tencent101055478" />
	    </intent-filter>
	</activity>

1. 对于微信，需要在自己应用包下新建一个.wxapi.WXEntryActivity。 配置文件如下：

	<activity
		android:name="com.gdwx.tiku.kjcy.wxapi.WXEntryActivity"
		android:exported="true"
		android:launchMode="singleTask"
		android:screenOrientation="portrait"
		android:theme="@android:style/Theme.Translucent.NoTitleBar" />

其中Activity 可以继续 com.gaodun.common.lib.social.oauth.WechatEntryActivity， 不需要再写任何代码即可，因为WechatEntryActivity已经对其进行了封装

	package com.gdwx.tiku.kjcy.wxapi;
	
	import com.gaodun.common.lib.social.oauth.WechatEntryActivity;

	public class WXEntryActivity extends WechatEntryActivity {
	}

## 登录

在登录之前务必调用  Social.init(context); 初始化第三方帐号的配置信息

	Social.init(this);
	
	Oauth mOauth = Oauth.getInstance(this);
	mOauth.setCallback(new OauthBack());
	
#### 微博登录

	mOauth.onWeibo();
	
#### 微信登录

	mOauth.onWechat();

#### QQ登录

	mOauth.onQQ();

值得注意的是，如果是新浪登录和QQ登录时，务必重写Activity 的 onActivityResult 方法

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 直接调用Oauth 的 onActivityResult 方法即可。里面封装了对其的操作
		mOauth.onActivityResult(requestCode, resultCode, data);
	}

## 分享

在分享之前务必调用  Social.init(context); 初始化第三方帐号的配置信息

1. 使用一键分享

一键分享已经写好了界面，使用Activity承载，首先需要在Manifest文件中注册该Activity 信息

	<activity
	    android:name="com.gaodun.common.lib.social.share.ShareActivity"
	    android:theme="@android:style/Theme.Translucent.NoTitleBar"
	    android:screenOrientation="portrait" />

然后代码调用的时候只需如下代码即可完成分享

	Social.init(this);
	
	String path = Environment.getExternalStorageDirectory().getAbsolutePath();
	Entry entry = new Entry().setTitle("测试分享到各大平台").setContent("这里是分享的内容，啦啦啦啦啦啦啦啊啦啦啦啦啦啦")
			.setUrl("http://androidweekly.cn/").addImage(path + "/TEST/imgs/1.jpg")
			.setImageUrl("https://ss0.bdstatic.com/5aV1bjqh_Q23odCf/static/superman/img/logo/bd_logo1_31bdc765.png");
	Share share = Share.getInstance(this);
	share.setCallback(new Callback());		// 自定义回调
	share.openShareActivity(entry);


1. 自定义分享

	Social.init(this);
	
	Entry entry = new Entry().set...;
	Share share = Share.getInstance(this);
	share.setCallback(new Callback());	// 自定义回调
	share.weibo(entry);					//分享到微博
	share.session(entry);				//分享到微信好友
	share.timeline(entry);				//分享到微信朋友圈
	share.favor(entry);					//分享到微信收藏
	share.qq(entry);					//分享到QQ好友
	share.qzone(entry);					//分享到QQ空间
