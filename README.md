# ScrollSetValueCirclePb
a circle progressbar likes the sun can be scrolled to set value.<br>
这是一个圆形进度条,通过顺时针或逆时针滑动设置记录数据,可以设定在指定范围内的进度条颜色,当前支持偏低/正常/偏高三个范围的颜色.<br>
* 图片概要(光标悬停在图片上有说明哦)<br>
![](https://github.com/jackbear168/ScrollSetValueCirclePb/raw/master/low.jpg "偏低")
![](https://github.com/jackbear168/ScrollSetValueCirclePb/raw/master/normal.jpg "正常")
![](https://github.com/jackbear168/ScrollSetValueCirclePb/raw/master/high.jpg "偏高")
* 怎么用<br>
1.在你project下的build.gradle里添加如下代码<br>
 ```Java
 	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```
2.在你主module下的build.gradle里添加如下依赖<br>
```Java
	dependencies {
	        compile 'com.github.jackBear168:ScrollSetValueCirclePb:v1.0'
	}
```




