package com.springmvc.servlet;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.springmvc.classScanner.ClassScanner;
import com.springmvc.util.BasicController;
import com.srpingmvc.annotation.Controller;
import com.srpingmvc.annotation.RequestMapping;

/**
 * 创建DispacherServlet 类，配置servlet属性 相当于在web.xml配置servlet
 * @WebServlet是用来配置servlet的属性的
 * @WebInitParam是用来配置一些初始化属性的(<init-param>中的name、value  )
 * @author cjm
 *
 */
@SuppressWarnings("serial")
@WebServlet(urlPatterns={"*.do"},initParams={@WebInitParam(name="basePackage",value="com.springmvc.controller")})
public class DispacherServlet extends HttpServlet{
		
		//定义有注解@Controller,@RequestMapping的方法和类
		private Map<String,Object> controllers = new HashMap<>();
		private Map<String,Object> methods = new HashMap<>();
		
		@SuppressWarnings({ "unchecked", "rawtypes" })
		public void init(){
			//Servlet的配置对象，容器在初始化Servlet时通过它传递信息给Servlet。
			//ServletConfig=Servlet初始化参数
			ServletConfig config = this.getServletConfig();
			//获取初始化参数
			String basePackage = config.getInitParameter("basePackage");
			try {
				Map<String,Class<?>> cons = ClassScanner.getClass(basePackage);
				Iterator<String> it = cons.keySet().iterator();
				while(it.hasNext()){
					String className = it.next();
					Class clazz = cons.get(className);
					String path="";
					//判断类上是否有@Controller,@RequestMapping
					if(clazz.isAnnotationPresent(RequestMapping.class)&& clazz.isAnnotationPresent(Controller.class)){
						RequestMapping reqAnno = (RequestMapping)clazz.getAnnotation(RequestMapping.class);
						//获取@RequestMapping的值，得到映射路径
						path = reqAnno.value();
						//放入定义的map中（类名：类实例对象）
						//在使用newInstance()方法的时候，必须保证这个类已经加载并且已经连接了，
						//而这可以通过Class的静态方法forName()来完成的
						//newInstance()是弱类型的，效率相对较低
						controllers.put(className,clazz.newInstance());
						//获取类中所有定义的方法 (对象表示的类或接口声明的所有方法)
						//包括公共、保护、默认（包）访问和私有方法，但不包括继承的方法。
						//当然也包括它所实现接口的方法。
						Method[] ms = clazz.getDeclaredMethods();
						for(Method method : ms){
							//判断方法上是否有@RequestMapping
							//没有，就跳过继续循环
							if(!method.isAnnotationPresent(RequestMapping.class)){
								continue;
							}
							//有，就放入定义的map中（类上路径+方法上的路径：方法实例）
							methods.put(path+method.getAnnotation(RequestMapping.class).value(),method);
						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
		@SuppressWarnings("unused")
		public void service(HttpServletRequest request,HttpServletResponse response){
			//返回除去host（域名或者ip）部分的路径（/springmvc/index/print.do）
			String uri = request.getRequestURI();
			//返回工程名部分,如果工程映射为/，此处返回则为空
			//（/springmvc）
			String contextPath = request.getContextPath();
			int value = uri.indexOf(contextPath)+contextPath.length();
			//获取映射路径（/index/print）
			String mappingPath = uri.substring(value,uri.indexOf(".do")); 
			//根据路径去存放method的map找到对应的方法
			Method method = (Method)methods.get(mappingPath);
			try {
				if(method==null){
					response.getWriter().println("<font style='size:100px'>404 404 404 404 404 </font>");
					return;
				}
				//反射得到Class<?>对象（声明由此Method对象表示的方法的类的Class对象）
				Class<?> declaringClass = method.getDeclaringClass(); 
				BasicController controller =         
                (BasicController)controllers.get(declaringClass.getName());
				//将request，response对象传入
				controller.init(request, response); 
				//执行该controller的method方法
				Object invoke = method.invoke(controller);

				/*String name = method.getDeclaringClass().getName();
				 method.invoke(controllers.get(name));*/
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
}
