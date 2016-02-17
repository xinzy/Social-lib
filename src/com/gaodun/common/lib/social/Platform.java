package com.gaodun.common.lib.social;

public class Platform {

	public static final int WEIBO = 0x1;
	public static final int QQ = 0x2;
	public static final int WECHAT = 0x3;

	public static final int SHARE_QQ = 0x10;
	public static final int SHARE_QZONE = 0x20;
	public static final int SHARE_WEIBO = 0x40;
	public static final int SHARE_WECHAT_SESSION = 0x80;
	public static final int SHARE_WECHAT_TIMELINE = 0x100;
	public static final int SHARE_WECHAT_FAVORITE = 0x200;

	// Sina 微博
	public static final String WEIBO_KEY = "1108795777";
	public static final String WEIBO_SECRET = "bb38a46a0bbd41c90a1b82c61c24073d";
	public static final String WEIBO_REDIRECT_URL = "http://www.gaodun.com/oauth/weibo/callback.php";

	// QQ
	public static final String QQ_APPID = "101055478";
	public static final String QQ_APPKEY = "a15192b69926e129f53c1f12942f833a";
	public static final String QQ_SCOPE = "get_user_info";

	// 微信
	public static final String WECHAT_APPID = "wx8baf1de2a92d60c4";
	public static final String WECHAT_SECRET = "d4624c36b6795d1d99dcf0547af5443d";
	public static final String WECHAT_SCOPE = "snsapi_userinfo";
	public static final String WECHAT_STATE = "wechat_state";
}
