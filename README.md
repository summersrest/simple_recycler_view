# SimpleRecyclerView
RecyclerView的封装
## ***SimpleRecyclerView***
使用：

    <com.sum.simple.SimpleRecyclerView
        android:id="@+id/rv"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:rv_divider_color="#f00"				//设置纯色分割线颜色（还可以设置图片分割线）
        app:rv_divider_size="10dp"				//设置分割线大小
        app:rv_orientation="vertical"			//设置列表控件的方向
        app:rv_default_animator_open="true"		//设置是否开启局部刷新动画（不设置默认关闭）
        app:rv_type="grid"/>					//设置列表类型（列表/网格、瀑布流）

### **1、导入**

	implementation 'com.github.summersrest:simple_recycler_view:v1.0.0'

### **2、设置LQRRecyclerView的控件类型**

*LinearLayoutManager能做到的，GridLayoutManager也能做到，所以本工程去掉了"list"这一项

	①xml方式（有三种选择：grid、stagger）
	app:rv_type="" 

	②代码方式(有三种选择：TYPE_GRID、TYPE_STAGGER)
	mRv.setType(SimpleRecyclerView.TYPE_XX);

### **3、设置SimpleRecyclerView的控件方向**

	①xml方式（有两种选择：vertical、horizontal）
	app:rv_orientation="" 

	②代码方式(有两种选择：ORIENTATION_VERTICAL、ORIENTATION_HORIZONTAL)
	mRv.setOrientation(SimpleRecyclerView.XX);

### **4、设置SimpleRecyclerView的列数**

	①xml方式
	app:rv_column="2"

	②代码方式
	mRv.setColumn(2);


### **5、设置SimpleRecyclerView的分割线样式**

#### 1)设置纯色分割线

	①xml方式
	app:rv_divider_color="#f00"
    app:rv_divider_size="10dp" 

	②代码方式
	mRv.setDividerColor();
    mRv.setDividerSize();

#### 2)设置图片分割线
	
	①xml方式
	app:rv_divider_drawable="@mipmap/ic_launcher"
    app:rv_divider_size="10dp" 

	②代码方式
    mRv.setDividerDrawable();
    mRv.setDividerSize();

### **6、设置默认局部刷新动画的开启和关闭**
*因为开发时很多时候是不需要默认的条目动画的，所以默认设置为不要动画（即为false），注意，如果你的项目中用到了自定义条目动画，那么要将该属性打开！！！

	①xml方式
    app:rv_default_animator_open="true"

	②代码方式
    //打开默认局部刷新动画
    mRv.openDefaultAnimator();
    //关闭默认局部刷新动画
    mRv.loseDefaultAnimator();


### **7、滑动指定位置**
*使用RecyclerView自带的smoothScrollToPosition方法和scrollToPosition方法实现滑动到指定位置时，不会将对应的条目置顶，使用以下方法可解决上述问题。

滚动：

	mRv.moveToPosition(position);

平滑滚动：

	mRv.smoothMoveToPosition(position);

### **8、监听SimpleRecyclerView的滚动**
因为该控件已经对RecyclerView进行过监听，用于实现平滑滚动条目置顶，故要监听其滚动事件，需要使用以下接口：OnScrollListenerExtension

	mRv.setOnScrollListenerExtension(new OnScrollListenerExtension...);
# 感谢
·https://github.com/GitLqr/LQRRecyclerViewLibrary#readme
基于该开源库增加了AndroidX支持。
