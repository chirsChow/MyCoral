package com.sfpay.downloadzip.model;

import java.io.Serializable;

public class OAuthToken implements Serializable{
	
	private static final long serialVersionUID = -4648680460632250344L;

	private String accessToken;

	private String refreshToken;

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}
