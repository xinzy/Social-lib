package com.gaodun.common.lib.social;

public class SocialException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -501635650521020129L;

	public SocialException() {
		super();
	}

	public SocialException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

	public SocialException(String detailMessage) {
		super(detailMessage);
	}

	public SocialException(Throwable throwable) {
		super(throwable);
	}

}
