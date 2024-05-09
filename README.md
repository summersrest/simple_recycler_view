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
        app:rv_orientation="vertical"				//设置列表控件的方向
        app:rv_default_animator_open="true"			//设置是否开启局部刷新动画（不设置默认关闭）
		app:rv_cross_axis="3"					//交叉轴行/列数量
 	app:rv_divider_padding="15dp"				//分割线前后的padding
        app:rv_type="grid"/>					//设置列表类型（列表/网格、瀑布流）

### **1、导入**
1.引入jitpack
项目根目录中的settings.gradle

     ```
    dependencyResolutionManagement {
        repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
        repositories {
            google()
            mavenCentral()
            maven { url = uri("https://www.jitpack.io")}
        }
    }
    ```
    
2.添加
Module的build.gradle
    
    ```
    implementation 'com.github.summersrest:simple_recycler_view:v1.1.1'
    ```

### **2、设置SimpleRecyclerView的控件类型**

	①xml方式（有三种选择：list、grid、stagger）
	app:rv_type="" 

	②代码方式(有三种选择：TYPE_LIST、TYPE_GRID、TYPE_STAGGER)
	mRv.setType(SimpleRecyclerView.TYPE_XX);

### **3、设置SimpleRecyclerView的控件方向**

	①xml方式（有两种选择：vertical、horizontal）
	app:rv_orientation="" 

	②代码方式(有两种选择：ORIENTATION_VERTICAL、ORIENTATION_HORIZONTAL)
	mRv.setOrientation(SimpleRecyclerView.XX);

### **4、设置SimpleRecyclerView交叉轴行/列数**

	①xml方式
	app:rv_cross_axis="2"

	②代码方式
	mRv.setCrossAxisCount(2);


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
     
#### 3)设置分割线前后的padding
	
	①xml方式
	app:rv_divider_drawable="@mipmap/ic_launcher"
    	app:rv_divider_size="10dp" 

	②代码方式
    	mRv.setDividerDrawable();
    	mRv.setDividerSize();

### **6、设置默认局部刷新动画的开启和关闭**
*padding_start与padding_end的优先级高于padding，所以设置了padding_start或者padding_end属性，会覆盖padding属性。

	①xml方式
    	app:rv_divider_padding="15dp"		//同时设置前后的padding
        app:rv_divider_padding_start="20dp"	//分割线前padding
        app:rv_divider_padding_end="50dp"	//分割线后padding

	②代码方式
    	mRv.setDividerPadding();
    	mRv.setDividerPaddingStart();
     	mRv.setDividerPaddingEnd();


### **7、滑动指定位置**
*使用RecyclerView自带的smoothScrollToPosition方法和scrollToPosition方法实现滑动到指定位置时，不会将对应的条目置顶，使用以下方法可解决上述问题。

滚动：

	mRv.moveToPosition(position);

平滑滚动：

	mRv.smoothMoveToPosition(position);

### **8、监听SimpleRecyclerView的滚动**
因为该控件已经对RecyclerView进行过监听，用于实现平滑滚动条目置顶，故要监听其滚动事件，需要使用以下接口：OnScrollListenerExtension

	mRv.setOnScrollListenerExtension(new OnScrollListenerExtension...);

