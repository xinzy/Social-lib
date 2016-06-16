package com.xinzy.social;

import java.io.InputStream;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.content.Context;

public class Social {
	
	public static final boolean DEBUG = true;
	
	public static final String VERSION = "1.0.0";

	private static final String CONFIG_PATH = "social/SocialConfig.xml";

	public static final boolean init(Context context) {

		try {
			InputStream is = context.getAssets().open(CONFIG_PATH);
			SAXParser parser = SAXParserFactory.newInstance().newSAXParser();
			parser.parse(is, new ConfigHanlder());

			return true;
		} catch (Exception e) {
			if (DEBUG) {
				e.printStackTrace();
			}
			return false;
		}
	}

	static class ConfigHanlder extends DefaultHandler {

		static final String ELEMENT_WEIBO = "Weibo";
		static final String ELEMENT_WECHAT = "Wechat";
		static final String ELEMENT_QQ = "QQ";

		static final String ATTR_KEY = "key";
		static final String ATTR_SECRET = "secret";
		static final String ATTR_REDIRECT_URL = "redirectUrl";
		static final String ATTR_APPID = "appid";
		static final String ATTR_APPKEY = "appkey";

		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes)
				throws SAXException {

			if (ELEMENT_WEIBO.equals(localName)) {
				// 解析微博配置
				if (attributes != null && attributes.getLength() > 0) {
					final int length = attributes.getLength();

					for (int i = 0; i < length; i++) {
						String name = attributes.getLocalName(i);
						
						if (ATTR_KEY.equals(name)) {
							// 微博 key
							String key = attributes.getValue(i);
							Platform.WEIBO_KEY = key;
						} else if (ATTR_SECRET.equals(name)) {
							// 微博secert
							String secret = attributes.getValue(i);
							Platform.WEIBO_SECRET = secret;
						} else if (ATTR_REDIRECT_URL.equals(name)) {
							//微博redirect url
							String url = attributes.getValue(i);
							Platform.WEIBO_REDIRECT_URL = url;
						}
					}
				}

			} else if (ELEMENT_WECHAT.equals(localName)) {
				// 解析微信
				if (attributes != null && attributes.getLength() > 0) {
					final int length = attributes.getLength();

					for (int i = 0; i < length; i++) {
						String name = attributes.getLocalName(i);
						
						if (ATTR_APPID.equals(name)) {
							// 微信 Appid
							String appid = attributes.getValue(i);
							Platform.WECHAT_APPID = appid;
						} else if (ATTR_SECRET.equals(name)) {
							// 微信 secert
							String secret = attributes.getValue(i);
							Platform.WECHAT_SECRET = secret;
						}
					}
				}
			} else if (ELEMENT_QQ.equals(localName)) {
				// 解析QQ 
				if (attributes != null && attributes.getLength() > 0) {
					final int length = attributes.getLength();

					for (int i = 0; i < length; i++) {
						String name = attributes.getLocalName(i);
						
						if (ATTR_APPID.equals(name)) {
							// QQ Appid
							String appid = attributes.getValue(i);
							Platform.QQ_APPID = appid;
						} else if (ATTR_APPKEY.equals(name)) {
							// QQ appkey
							String appkey = attributes.getValue(i);
							Platform.QQ_APPKEY = appkey;
						}
					}
				}
			}
		}
	}
}
