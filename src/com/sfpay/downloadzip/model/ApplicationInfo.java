package com.sfpay.downloadzip.model;
/**
 * 应用（本地，WEB)实体
 * @author 837781
 *
 */
public class ApplicationInfo {
	public static final String TYPE_WEB = "web";
	public static final String TYPE_NATIVE = "native";
	
	private String id;
	private String name;
	private String type;
	private String url;
	private String version;
	private String icon;
	private String downloadUrl;
	private String signature;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}
	
}
