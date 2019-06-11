package com.max.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@ConfigurationProperties(prefix = "proxyside")
@Component
public class ProxySideProperties {
	
	private String serverUrls = "http://localhost:75";
	private String mainUrl = "http://localhost:75";
	
	private String socketUrls = "ws://localhost:75/";
	private String mainSocket = "ws://localhost:75/";
	
	public String getServerUrls() {
		return serverUrls;
	}
	public void setServerUrls(String serverUrls) {
		this.serverUrls = serverUrls;
	}
	public String getMainUrl() {
		return mainUrl;
	}
	public void setMainUrl(String mainUrl) {
		this.mainUrl = mainUrl;
	}
	public String getSocketUrls() {
		return socketUrls;
	}
	public void setSocketUrls(String socketUrls) {
		this.socketUrls = socketUrls;
	}
	public String getMainSocket() {
		return mainSocket;
	}
	public void setMainSocket(String mainSocket) {
		this.mainSocket = mainSocket;
	}
	
}
