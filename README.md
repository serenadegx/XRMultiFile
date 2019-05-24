# XRMultiFile

1.浏览文件

2.选择文件

3.支持自定义文件夹，实现快速浏览

4.限制选择个数

5.是否显示隐藏文件

6.可打开图片、音频、视频、常见office文件

7.已做6.0权限适配，和7.0 FileProvider 适配

![image](https://github.com/serenadegx/XRMultiFile/blob/master/1558675141407.gif)
![image](https://github.com/serenadegx/XRMultiFile/blob/master/1558675580829.gif)


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
		...
	        implementation 'com.github.serenadegx:XRMultiFile:1.0.0'
		
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
			
          protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		if (requestCode == 715 && data != null) {
            	   ArrayList<String> list = XRMultiFile.getSelectResult(data);
        	}
	  }
	     
# 附：

1.office文件的浏览使用的腾讯的TBS

2.图片加载及缓存策略使用的Picasso

3.音频和视频的打开调用的系统
