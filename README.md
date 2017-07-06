# SlidingTabLayout
在开发中，我们常常需要ViewPager结合Fragment一起使用，如下图：

![image](https://segmentfault.com/img/bVoQIu)

我们可以使用Design support library库的TabLayout去实现，但是TabLayout只能用横线指示器，如果想要其他指示器，比如三角下标，该控件就不能用了。

我们可以找网上成熟的轮子进行修改，比如：[PagerSlidingTabStrip](https://github.com/astuetz/PagerSlidingTabStrip)

楼主这里在项目中需要用到带三角下标的TabLayout，亲身研究了一番，结合鸿洋大神的博客：[Android 教你打造炫酷的ViewPagerIndicator 不仅仅是高仿MIUI](http://blog.csdn.net/lmj623565791/article/details/42160391)

实现了一个类似于今日头条的可滑动tab，带三角下标，可绑定ViewPager进行联动。

效果图如下

![image](E:\SlidingTabLayout.gif)

控件继承HorizontalScrollView，便于处理水平滚动。

三角下标用canvas绘制。

控件关键点在于处理好tab容器本身滑动的距离和三角下标滑动的距离。

详细见我的CSDN博客：
[自定义实现带三角下标的TabLayout](http://note.youdao.com/)
