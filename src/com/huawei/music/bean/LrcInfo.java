package com.huawei.music.bean;

public class LrcInfo {
	private int begin;
	private String lrcLine;
	
	public int getBegin() {
		return begin;
	}
	public void setBegin(int begin) {
		this.begin = begin;
	}
	public String getLrcLine() {
		return lrcLine;
	}
	public void setLrcLine(String lrcLine) {
		this.lrcLine = lrcLine;
	}
	
	public LrcInfo(int begin, String lrcLine) {
		super();
		this.begin = begin;
		this.lrcLine = lrcLine;
	}

}
