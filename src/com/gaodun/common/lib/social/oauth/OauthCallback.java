package com.gaodun.common.lib.social.oauth;

import com.gaodun.common.lib.social.SocialException;

public interface OauthCallback {

	void onSuccess(int platform, U user);
	
	void onFailure(int platform, String msg);
	
	void onCancelled(int platform);
	
	void onError(int platform, SocialException ex);
}
