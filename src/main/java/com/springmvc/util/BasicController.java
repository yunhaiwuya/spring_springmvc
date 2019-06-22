package com.springmvc.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * url传参数
 * service方法中的request与response对象传递到find方法
 * @author cjm
 *
 */
public class BasicController {
	private HttpServletRequest request;
	private HttpServletResponse response;
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
	public HttpServletResponse getResponse() {
		return response;
	}
	public void setResponse(HttpServletResponse response) {
		this.response = response;
	}
	public void init(HttpServletRequest request,HttpServletResponse response){
		this.request = request;
		this.response = response;
	}
}
