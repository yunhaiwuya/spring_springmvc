package com.springmvc.controller;

import com.springmvc.util.BasicController;
import com.srpingmvc.annotation.Controller;
import com.srpingmvc.annotation.RequestMapping;

@Controller
@RequestMapping("/hello")
public class HelloController extends BasicController{

	@RequestMapping("/print")
	public void pringHello(){
		System.out.println("hello world 运行");
	}
	@RequestMapping("/find")
	public void find(){
		System.out.println("find 运行");
	}
}
