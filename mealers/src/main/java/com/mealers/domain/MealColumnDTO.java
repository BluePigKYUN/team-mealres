package com.mealers.domain;

import java.util.List;

import com.mealers.util.MyMultipartFile;

public class MealColumnDTO {
	private long num;
	private String subject;
	private String content;
	private String reg_date;
	private int hitCount;
	private String userNum; 
	
	private long filenum;
	private String fileName;
	
	private List<MyMultipartFile> listFile;
	
	public List<MyMultipartFile> getListFile() {
		return listFile;
	}

	public void setListFile(List<MyMultipartFile> listFile) {
		this.listFile = listFile;
	}

	private int likeCount;

	public long getNum() {
		return num;
	}

	public void setNum(long num) {
		this.num = num;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getReg_date() {
		return reg_date;
	}

	public void setReg_date(String reg_date) {
		this.reg_date = reg_date;
	}

	public int getHitCount() {
		return hitCount;
	}

	public void setHitCount(int hitCount) {
		this.hitCount = hitCount;
	}

	public String getUserNum() {
		return userNum;
	}

	public void setUserNum(String userNum) {
		this.userNum = userNum;
	}

	public long getFilenum() {
		return filenum;
	}

	public void setFilenum(long filenum) {
		this.filenum = filenum;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getLikeCount() {
		return likeCount;
	}

	public void setLikeCount(int likeCount) {
		this.likeCount = likeCount;
	}
	
	
	

}