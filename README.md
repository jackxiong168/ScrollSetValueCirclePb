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
3.在你的布局文件中引用这个控件
```Java
 <com.jackxiong.scrollsetvaluecircleprogressbar.ScrollSetValueProgressCircle
        android:id="@+id/pb_test"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_centerInParent="true"
        />
```
4.界面初始化时设置显示的默认值的函数
```Java
testPb.setValue(6.9f);
```
5.获取当前值的回调函数
```Java
testPb.setValueChangeListener(new ScrollSetValueProgressCircle.ValueChangeListener() {
            @Override
            public void currentValue(float value) {

                Log.d(TAG,"current value= "+ value);
            }
        });
```






