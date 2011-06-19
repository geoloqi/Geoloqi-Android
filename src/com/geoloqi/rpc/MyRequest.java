package com.geoloqi.rpc;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import org.apache.http.Header;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;

public class MyRequest {
	static final int GET = 0;
	static final int POST = 1;

	public final HttpRequestBase request;

	MyRequest(int requestType, String url) {
		switch (requestType) {
		case GET:
			request = new HttpGet(url);
			break;
		case POST:
			request = new HttpPost(url);
			break;
		default:
			throw new IllegalArgumentException("Request type must be one of the static types.");
		}
	}

	MyRequest headers(Header... headers) {
		request.setHeaders(headers);
		return this;
	}

	MyRequest params(BasicNameValuePair... pairs) {
		BasicHttpParams params = new BasicHttpParams();
		for (int i = 0; i < pairs.length; i++) {
			params.setParameter(pairs[i].getName(), pairs[i].getValue());
		}
		request.setParams(params);
		return this;
	}

	MyRequest entityParams(BasicNameValuePair... pairs) {
		try {
			this.entity(new UrlEncodedFormEntity(Arrays.asList(pairs)));
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e.getMessage());
		}
		return this;
	}

	MyRequest entity(AbstractHttpEntity entity) {
		if (request instanceof HttpEntityEnclosingRequestBase) {
			((HttpEntityEnclosingRequestBase) request).setEntity(entity);
		} else {
			throw new IllegalArgumentException("Request must be PUT or POST to enclose an entity.");
		}
		return this;
	}
}
