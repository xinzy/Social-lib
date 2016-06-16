package com.xinzy.social.oauth;

import org.json.JSONException;
import org.json.JSONObject;

public class U {

	/** 用户UID（int64） Weibo */
	private int id;
	/** 字符串型的用户 UID Weibo */
	private String idstr;
	/** 用户昵称 */
	private String screenName;
	/** 友好显示名称 */
	private String name;
	/** 用户所在地 */
	private String location;
	/** 用户个人描述 */
	private String description;
	/** 用户头像地址，50×50像素 */
	private String avatar;
	/** 用户大头像地址 */
	private String avatarLarge;
	/** 用户高清大头像地址 */
	private String avatarHd;
	private String gender;
	
	/** 第三方登录后的openid*/
	private String openid;
	private int plat;
	/** 微信登录后的unionid 仅微信有效*/
	private String unionid;
	
	static U weibo(String res) {
		try {
			JSONObject json = new JSONObject(res);
			U u = new U();
			
			u.id = json.optInt("id");
			u.idstr = json.optString("idstr");
			u.screenName = json.optString("screen_name");
			u.name = json.optString("name");
			u.location = json.optString("location");
			u.description = json.optString("description");
			u.avatar = json.optString("profile_image_url");
			u.avatarLarge = json.optString("avatar_large");
			u.avatarHd = json.optString("avatar_hd");
		
			/** 性别，m：男、f：女、n：未知 */
			String gend = json.optString("gender");
			if ("m".equals(gend)) {
				u.gender = "男";
			} else if ("f".equals(gend)) {
				u.gender = "女";
			} else {
				u.gender = "未知";
			}
			u.openid = u.idstr;
			
			return u;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	static final U qq(JSONObject json) {
		if (json != null && json.optInt("ret", -1) == 0) {
			U u = new U();
			String name = json.optString("nickname");
			u.name = name;
			u.screenName = name;
			u.avatar = json.optString("figureurl_qq_2");
			u.location = json.optString("province") + " " + json.optString("city");
			u.gender = json.optString("gender");
			
			return u;
		}
		
		return null;
	}
	
	static final U wechat(JSONObject json) {
		
		if (json != null) {
			U u = new U();
			String name = json.optString("nickname");
			u.name = name;
			u.screenName = name;
			u.openid = json.optString("openid");
			u.unionid = json.optString("unionid");
			u.avatar = json.optString("headimgurl");
			u.location = json.optString("country") + " " + json.optString("province") + " " + json.optString("city");
			
			int sex = json.optInt("sex");
			if (sex == 1) {
				u.gender = "男";
			} else if (sex == 2) {
				u.gender = "女";
			} else {
				u.gender = "未知";
			}
			
			return u;
		}
		
		return null;
	}

	public int getId() {
		return id;
	}

	public String getIdstr() {
		return idstr;
	}

	public String getScreenName() {
		return screenName;
	}

	public String getName() {
		return name;
	}

	public String getLocation() {
		return location;
	}

	public String getDescription() {
		return description;
	}

	public String getGender() {
		return gender;
	}

	public String getAvatar() {
		return avatar;
	}

	public String getAvatarLarge() {
		return avatarLarge;
	}

	public String getAvatarHd() {
		return avatarHd;
	}

	public String getOpenid() {
		return openid;
	}

	public int getPlat() {
		return plat;
	}
	
	public String getUnionid() {
		return unionid;
	}

	void setOpenid(String openid) {
		this.openid = openid;
	}
	
	void setUnionid(String unionid) {
		this.unionid = unionid;
	}
	
	void setPlat(int plat) {
		this.plat = plat;
	}

	@Override
	public String toString() {
		return "U [screenName=" + screenName + ", name=" + name + ", location=" + location + ", description="
				+ description + ", avatar=" + avatar + ", gender=" + gender + ", openid=" + openid + ", plat=" + plat
				+ ", unionid=" + unionid + "]";
	}
}
