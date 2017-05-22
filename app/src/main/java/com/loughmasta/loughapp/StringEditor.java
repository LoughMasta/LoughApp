package com.loughmasta.loughapp;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringEditor {

	static public ImgurPost extractUrl(String incoming){
		//Credit: http://blog.houen.net/java-get-url-from-string/
		//Finds any URL in a given string then separates it
		String regex = "\\(?\\b(https://|www[.])[-A-Za-z0-9+&amp;@#/%?=~_()|!:,.;]*[-A-Za-z0-9+&amp;@#/%=~_()|]";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(incoming);
		String urlStr = "";
		ImgurPost returnPost;
		while(m.find()){
			urlStr = m.group();
			if (urlStr.startsWith("(") && urlStr.endsWith(")")){
				urlStr = urlStr.substring(1, urlStr.length() - 1);
			}
		}
		//Getting title
		String title = incoming.replace(urlStr, "");
		//Getting post id
		String postId = urlStr.replace("https://imgur.com/gallery/", "");
		returnPost = new ImgurPost(title, postId);
		return returnPost;
	}
}
