package com.example.partyplayeractivity;


public class ConfigureData {
	
	private String url;
	private boolean serviceAlive;
	private int workingMode;
	public static final int LOCAL_MODE = 0;
	public static final int G_MDOE = 1;
	public static final int COOPERATIVE_MODE = 2;
	
	
	public ConfigureData(String url){
		this.url = url;
		serviceAlive = false;
		workingMode = LOCAL_MODE;
	}
	
	
	public int getWorkingMode() {
		return workingMode;
	}

	public void setWorkingMode(int workingMode) {
		this.workingMode = workingMode;
	}

	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public boolean isServiceAlive() {
		return serviceAlive;
	}

	public void setServiceAlive(boolean serviceAlive) {
		this.serviceAlive = serviceAlive;
	}

}
