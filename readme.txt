手写简单springmvc的思路：
1、一般公司对代码的结构：
	浏览器请求==》Controller层--》Service层--》Dao层==》数据库
	数据请求到后，就会响应返回给浏览器展示
2、大纲是：浏览器请求--》controller层    即返回响应给浏览器展示。
	controller层： 一般有两种注解@Controller、@RequestMapping
	所以先定义这两个注解，实现其功能
3、实现思路：
	配置web.xml，加载自定义的servlet类（DispacherServlet）
     初始化阶段（init），有以下步骤
	加载配置类
	扫描当前项目下的所有文件
	拿到扫描到的类，通过java反射机制、实例化，并且放到自定义map中（ioc容器的bean）
	判断有注解的类和方法，并初始化该类中的方法与path的映射，存放在自定义map中
    运行阶段，
	每一次请求，都会调用service方法，根据url请求去map(handlerMapping)中找到对应的method,
	然后利用java反射机制调用（invoke）controller中的url对应的方法，并得到返回结果。
	其步骤如下：
		获取请求传入的参数，处理参数
		通过初始化好的map中找到对应的method，反射调用。
