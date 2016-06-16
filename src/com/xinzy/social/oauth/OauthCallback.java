package com.xinzy.social.oauth;

import com.xinzy.social.SocialException;

public interface OauthCallback {

	void onSuccess(int platform, U user);
	
	void onFailure(int platform, String msg);
	
	void onCancelled(int platform);
	
	void onError(int platform, SocialException ex);
}
