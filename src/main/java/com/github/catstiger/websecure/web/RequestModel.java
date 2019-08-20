package com.github.catstiger.websecure.web;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

import com.github.catstiger.common.web.WebUtil;

/**
 * 当用户访问一个URL被拒之后，{@code RequestModel}封装了当前{@code HttpServletRequest}的信息。当用户登录成功之后，再重定向到这个URL。 这通常用于通过表单登录的情况，如果通过Ajax方式登录则不适用。
 * 
 * @author leesam
 *
 */
@SuppressWarnings("serial")
public class RequestModel implements Serializable {
	private String method;
	private String queryString;
	private String requestURI;
	private Boolean isJson = false;

	public RequestModel() {

	}

	/**
	 * Constructs a new instance from the given HTTP request.
	 *
	 * @param request the current request to save.
	 */
	public RequestModel(HttpServletRequest request) {
		this.method = request.getMethod();
		this.queryString = request.getQueryString();
		this.requestURI = request.getRequestURI();
		this.isJson = WebUtil.isJsonRequest(request);
	}

	public String getMethod() {
		return method;
	}

	public String getQueryString() {
		return queryString;
	}

	public String getRequestURI() {
		return requestURI;
	}

	public Boolean isJson() {
		return this.isJson;
	}

	public Boolean getIsJson() {
		return isJson;
	}

	public void setIsJson(Boolean isJson) {
		this.isJson = isJson;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}

	public void setRequestURI(String requestURI) {
		this.requestURI = requestURI;
	}

	/**
	 * 返回请求URL，去除参数
	 */
	public String getRequestUrl() {
		StringBuilder requestUrl = new StringBuilder(getRequestURI());
		if (getQueryString() != null) {
			requestUrl.append("?").append(getQueryString());
		}
		return requestUrl.toString();
	}
}
