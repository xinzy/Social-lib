package com.xinzy.social.oauth;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;
import java.util.Set;

import com.xinzy.social.Constant;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;

class AsyncRequest extends Thread {

	private Handler mHandler;

	private String mUrl;

	private static final int DEFAULT_CONNECTION_TIMEOUT = 10000;
	private static final int DEFAULT_READ_TIMEOUT = 10000;

	private static final String DEFAULT_ENCODING = "UTF-8";
	private static final String DEFAULT_USERAGENT = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/46.0.2490.80 Safari/537.36";

	private HttpURLConnection mUrlConnection;
	private Map<String, String> param;
	private String method;

	private RequestCallback mCallback;

	public AsyncRequest(String url) {
		mUrl = url;
		mHandler = new RequestHandler();
	}

	public AsyncRequest setParam(Map<String, String> param) {
		this.param = param;
		return this;
	}

	@Override
	public void run() {
		try {

			String response = request();
			Message msg = mHandler.obtainMessage();
			msg.what = RequestHandler.SUCCESS;
			msg.obj = response;
			msg.sendToTarget();
		} catch (Exception e) {

			Message msg = mHandler.obtainMessage();
			msg.what = RequestHandler.ERROR;
			msg.obj = e;
			msg.sendToTarget();
		}
	}

	public void doGet(RequestCallback callback) {
		this.mCallback = callback;
		method = Constant.HTTPMETHOD_GET;
		start();
	}

	public void doPost(RequestCallback callback) {
		this.mCallback = callback;
		method = Constant.HTTPMETHOD_POST;
		start();
	}

	private String request() throws Exception {

		if (Constant.HTTPMETHOD_GET.equals(method) && param != null && param.size() > 0) {
			mUrl = splitParam(mUrl, param);
		}

		URL url = new URL(mUrl);

		mUrlConnection = (HttpURLConnection) url.openConnection();
		mUrlConnection.setRequestMethod(method);
		mUrlConnection.setDefaultUseCaches(true);
		mUrlConnection.setConnectTimeout(DEFAULT_CONNECTION_TIMEOUT);
		mUrlConnection.setReadTimeout(DEFAULT_READ_TIMEOUT);

		mUrlConnection.setRequestProperty("Content-type", "text/html");
		mUrlConnection.setRequestProperty("Accept-Charset", DEFAULT_ENCODING);
		mUrlConnection.setRequestProperty("Charset", DEFAULT_ENCODING);
		mUrlConnection.setRequestProperty("User-Agent", DEFAULT_USERAGENT);

		if (Constant.HTTPMETHOD_POST.equals(method)) {

			mUrlConnection.setDefaultUseCaches(false);
			mUrlConnection.setDoOutput(true);

			if (param != null && param.size() > 0) {
				String request = splitParam(param);
				OutputStream os = mUrlConnection.getOutputStream();
				os.write(request.getBytes());
			}
		} else {
			mUrlConnection.connect();
		}

		int responseCode = mUrlConnection.getResponseCode();
		if (responseCode == HttpURLConnection.HTTP_OK) {
			InputStream is = mUrlConnection.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line).append("\n");
			}

			return sb.toString();
		} else {
			throw new Exception("send request error, http response code = " + responseCode);
		}
	}

	private String splitParam(String url, Map<String, String> param) throws Exception {

		StringBuffer sb = new StringBuffer(url);

		if (!TextUtils.isEmpty(url)) {
			if (url.contains("?")) {
				sb.append("&");
			} else {
				sb.append("?");
			}
		}
		return sb.append(splitParam(param)).toString();
	}

	private String splitParam(Map<String, String> param) throws Exception {
		StringBuffer sb = new StringBuffer();
		if (param == null) {

			return "";
		}
		Set<String> keys = param.keySet();
		for (String key : keys) {

			if (TextUtils.isEmpty(key)) {
				continue;
			}
			String val = param.get(key);
			val = (val == null ? "" : URLEncoder.encode(val.trim(), DEFAULT_ENCODING));
			sb.append(key).append("=").append(val).append("&");
		}
		String str = sb.toString();
		if (str.endsWith("&")) {
			return str.substring(0, str.length() - 1);
		} else {
			return str;
		}
	}

	@SuppressLint("HandlerLeak")
	class RequestHandler extends Handler {

		public static final int SUCCESS = 0;
		public static final int ERROR = 1;

		public RequestHandler() {
			super(Looper.getMainLooper());
		}

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case SUCCESS:
				String response = (String) msg.obj;
				if (mCallback != null) {
					mCallback.onSuccess(response);
				}
				break;

			case ERROR:
				Exception e = (Exception) msg.obj;
				if (mCallback != null) {
					mCallback.onError(e);
				}
				break;
			default:
				break;
			}
			
		}
	}

	public static interface RequestCallback {
		void onSuccess(String response);

		void onError(Exception e);
	}
}
