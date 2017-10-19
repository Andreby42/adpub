package com.bus.chelaile.flow.uc;

import java.util.List;

public class ArticleRecomResponseData {

	private List<ArticleResponseData> articles;
	private int status;
	private String message;
	
	
	public List<ArticleResponseData> getArticles() {
		return articles;
	}
	public void setArticles(List<ArticleResponseData> articles) {
		this.articles = articles;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "ArticleRecomResponseData [articles=" + articles + ", status=" + status + ", message=" + message + "]";
	}
}
