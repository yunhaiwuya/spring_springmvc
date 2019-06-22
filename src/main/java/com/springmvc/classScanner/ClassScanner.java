package com.springmvc.classScanner;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * 注解扫描器ClassScanner
 * @author cjm
 *
 */
public class ClassScanner {
	//为什么是Map<String,Class<?>>类型呢？  因为String存储类名，
	//Class对象存储反射生成的类对象
	//basePackage为传入的包名
	public static Map<String,Class<?>> getClass(String basePackage){
		Map<String,Class<?>> results = new HashMap<>();
		//通过包将 . 替换成/ （com/springmvc/controller）
		String filePath = basePackage.replace(".", "/");
		try {
			//获取当前正在执行的线程对象的引用   Thread.currentThread()
			Thread currentThread = Thread.currentThread();
			ClassLoader contextClassLoader = currentThread.getContextClassLoader();
			//定位资源文件（file:/E:/workspace/spring/.metadata/...）
			URL resource = contextClassLoader.getResource(filePath);
			//返回resource路径部分（/E:/workspace/spring/.metadata/...）
			String rootPath = resource.getPath();
			if(rootPath!=null){
				//filePath:（com/springmvc/controller）
				//rootPath:com/springmvc/controller/
				rootPath = rootPath.substring(rootPath.lastIndexOf(filePath));
			}
			//Enumeration接口本身不是一个数据结构
			//Enumeration接口定义了从一个数据结构得到连续数据的手段
			Enumeration<URL> dirs = contextClassLoader.getResources(rootPath);
			//判断Enumeration枚举对象中是否还含有元素，如果返回true，
			//则表示还含有至少一个的元素
			while(dirs.hasMoreElements()){
				//获取对象中的下一个元素
				URL url = dirs.nextElement();
				//getProtocol获取客户端向服务器端传送数据所依据的协议名称。
				//包括HTTP, HTTPS, FTP, 和file
				//例：HTTP/1.11
				System.out.println("===="+url.getProtocol());
				//返回url路径部分（/E:/workspace/spring/.metadata/...）
				System.out.println("===="+url.getPath());
				//返回 （E:/workspace/spring/.metadata/...）
				System.out.println("===="+url.getPath().substring(1));
				if(url.getProtocol().equals("file")){
					//返回（E:\workspace\spring\.metadata\...）
					File file = new File(url.getPath().substring(1));
					scannerFile(file,rootPath,results);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return results;
	}

	private static void scannerFile(File folder, String rootPath,
			Map<String, Class<?>> classes) throws Exception {
		//返回某个目录下所有文件和目录的绝对路径
		File[] files = folder.listFiles();
		if(files!=null){
			for(int i=0;i<files.length;i++){
				//（E:\workspace\spring\.metadata\...）
				File file = files[i];
				//判断是否目录，是则回调scannerFile方法
				if(file.isDirectory()){
					scannerFile(file,rootPath+file.getName()+"/",classes);
				}else{
					//返回抽象路径名的绝对路径名字符串
					//如果此抽象路径名已经是绝对路径名，则返回该路径名字符串，
					//这与 getPath() 方法一样。如果此抽象路径名是空的抽象路径名，
					//则返回当前用户目录的路径名字符串，该目录由系统属性 user.dir 指定
					String path = file.getAbsolutePath();
					if(path.endsWith("class")){
						path = path.replace("\\", "/");
						String className = rootPath+path.substring(path.lastIndexOf("/")+1,path.indexOf(".class"));
						//(com/springmvc/controller/HelloController)
						className = className.replace("/", ".");
						//class.forname:jvm查找并加载指定的类，返回的是一个class对象的引用
						//放入map中(类名：类实例对象)
						classes.put(className, Class.forName(className));
					}
				}
			}
		}
		
	}
}
