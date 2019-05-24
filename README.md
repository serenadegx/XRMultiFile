# XRMultiFile

1.浏览文件

2.选择文件

3.支持自定义文件夹，实现快速浏览

4.限制选择个数

5.是否显示隐藏文件

6.可打开图片、音频、视频、常见office文件

7.已做6.0权限适配，和7.0 FileProvider 适配

![image](https://github.com/serenadegx/XRWebview/blob/master/1545730427868.gif)
![image](https://github.com/serenadegx/XRWebview/blob/master/1546404544823.gif)


# 使用

Add it in your root build.gradle at the end of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
    
Add the dependency

	dependencies {
	        implementation 'com.github.serenadegx:XRWebview:1.1.2'
	}


	}
  
  文件浏览器：
  
              XRMultiFile.get()
                        .with(context)
                        .lookHiddenFile(false)
                        .custom(file)
                        .browse();
                        
   文件选择器：
   
              XRMultiFile.get()
                        .with(context)
                        .lookHiddenFile(false)
                        .custom(file)
                        .limit(5)
                        .select(activity, 715);
