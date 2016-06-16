package com.xinzy.social;

public class Constant {

	/** POST 请求方式 */
	public static final String HTTPMETHOD_POST = "POST";
	/** GET 请求方式 */
	public static final String HTTPMETHOD_GET = "GET";

	public static final String KEY_ACCESS_TOKEN = "access_token";

	// Weibo
	public static final String WEIBO_SCOPE = "email,direct_messages_read,direct_messages_write,"
			+ "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
			+ "follow_app_official_microblog,invitation_write";

	protected static final String WEIBO_API_SERVER = "https://api.weibo.com/2";
	public static final String WEIBO_API_USER = WEIBO_API_SERVER + "/users/show.json";

	// Wechat
	protected static final String WECHAT_API_SERVER = "https://api.weixin.qq.com/sns/";
	public static final String WECHAT_API_ACCESS_TOKEN = WECHAT_API_SERVER + "oauth2/access_token?appid="
			+ Platform.WECHAT_APPID + "&secret=" + Platform.WECHAT_SECRET + "&code=%1$s&grant_type=authorization_code";
	public static final String WECHAT_API_USERINFO = WECHAT_API_SERVER + "userinfo?access_token=%1$s&openid=%2$s";
}
