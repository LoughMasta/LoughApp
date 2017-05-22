package com.loughmasta.loughapp;

public class ImgurPost {

	String title, postId;
	
	public ImgurPost(String title, String postId){
		
	this.title = title;
	this.postId = postId;
	
	}
	public ImgurPost(ImgurPost temp){
		this.title = temp.getTitle();
		this.postId = temp.getPostId();
	}
	public String getTitle(){
		return this.title;
	}
	public String getPostId(){
		return this.postId;
	}
}
