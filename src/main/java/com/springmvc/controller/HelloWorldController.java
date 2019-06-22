package com.springmvc.controller;

import java.io.IOException;

import com.springmvc.util.BasicController;
import com.srpingmvc.annotation.Controller;
import com.srpingmvc.annotation.RequestMapping;

@Controller
@RequestMapping("/index")
public class HelloWorldController extends BasicController{

	@RequestMapping("/print")
	public void pringHello(){
		String username = getRequest().getParameter("username");
		System.out.println("hello world 运行 "+username);
		try {
			getResponse().getWriter().write("hello,"+username);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	@RequestMapping("/find")
	public void find(){
		System.out.println("find 运行");
	}
}
