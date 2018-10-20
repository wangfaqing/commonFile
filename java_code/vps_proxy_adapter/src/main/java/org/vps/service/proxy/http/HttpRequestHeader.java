package org.vps.service.proxy.http;

import java.util.ArrayList;
import java.util.List;

import org.vps.app.util.keyValue;


public class HttpRequestHeader {

	private int headerLen;
	private String methon;
	private String uri;
	private String version;
	private List<keyValue<String, String>> headers = null;
	
	public HttpRequestHeader() {
		setHeaderLen(0);
		setMethon("");
		setUri("");
		setVersion("");
		setHeaders(new ArrayList<keyValue<String,String>>());
	}

	public int getHeaderLen() {
		return headerLen;
	}

	public void setHeaderLen(int headerLen) {
		this.headerLen = headerLen;
	}

	public String getMethon() {
		return methon;
	}

	public void setMethon(String methon) {
		this.methon = methon;
	}

	public String getUri() {
		return uri;
	}

	public void setUri(String uri) {
		this.uri = uri;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public List<keyValue<String, String>> getHeaders() {
		return headers;
	}

	private void setHeaders(List<keyValue<String, String>> headers) {
		this.headers = headers;
	}
}
