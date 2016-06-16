package com.xinzy.social.share;

import java.io.Serializable;
import java.util.ArrayList;

public class Entry implements Serializable {

	private static final long serialVersionUID = -2206706913423077360L;
	private String title;
	private String content;
	private String url;
	private String appname;
	private ArrayList<String> imagePaths;
	private String imageUrl;
	
	public Entry setTitle(String title) {
		this.title = title;
		return this;
	}
	
	public Entry setContent(String content) {
		this.content = content;
		return this;
	}
	
	public Entry setUrl(String url) {
		this.url = url;
		return this;
	}
	
	public Entry setAppname(String appname) {
		this.appname = appname;
		return this;
	}
	
	public Entry setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
		return this;
	}
	
	public Entry addImage(String path) {
		if (imagePaths == null) {
			imagePaths = new ArrayList<>();
		}
		
		if (imagePaths.size() < 9) {
			imagePaths.add(path);
		}
		
		return this;
	}

	public String getTitle() {
		return title;
	}

	public String getContent() {
		return content;
	}

	public String getUrl() {
		return url;
	}

	public String getAppname() {
		return appname;
	}
	
	public String getImageUrl() {
		return imageUrl;
	}
	
	public ArrayList<String> getImagePaths() {
		return imagePaths;
	}
}
