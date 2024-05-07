# SimpleAdapter
    Android 使用ViewBinding的RecyclerView Adapter的封装，简单易用。
## **1、导入**

 1.引入jitpack
 项目根目录中的build.gradle
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
implementation("com.github.summersrest:SimpleAdapter:v1.0.3")
```

## **2、使用**

### 1、单布局使用方法
```java
recyclerView.setAdapter(new BaseAdapter<ItemMainBinding, String>(this, list) {

            @Override
            protected ItemMainBinding getViewBinding(int viewType, LayoutInflater layoutInflater, ViewGroup parent) {
                return ItemMainBinding.inflate(layoutInflater, parent, false);
            }

            @Override
            protected void onBind(Context context, ViewHolder<ItemMainBinding> holder, String item, int position) {
                holder.binding.tvText.setText(item);
            }
        });
```
点击事件
```java
adapter.setOnItemClickListener(new SimpleOnItemClickListener<ItemBean>() {
            @Override
            public void onItemClick(View view, ItemBean item, int position) {
                
            }
        });
```

### 2、多布局使用方法
```java
 MultipleAdapter<ItemBean> multipleAdapter = new MultipleAdapter<>(this, datas);
 multipleAdapter.add(new LeftEntrust());
 multipleAdapter.add(new RightEntrust());
 recyclerView.setAdapter(multipleAdapter);
```
Entrust
```java
public class LeftEntrust implements Entrust<ItemMainLeftBinding, ItemBean> {
    @Override
    public ViewBinding getViewBinding(LayoutInflater layoutInflater, ViewGroup parent) {
        return ItemMainLeftBinding.inflate(layoutInflater, parent, false);
    }

    @Override
    public boolean isThisType(ItemBean itemBean) {
       return (itemBean.getType() == 0);
    }

    @Override
    public void onBind(Context context, ViewHolder<ItemMainLeftBinding> holder, ItemBean item, int position) {
        holder.binding.tvTextLeft.setText(item.getTitie());
    }
}
```


## 感谢


* [https://github.com/hongyangAndroid/baseAdapter](https://github.com/hongyangAndroid/baseAdapter)



* [https://github.com/meijingkang/baseAdapter](https://github.com/meijingkang/baseAdapter)
	

