package com.sum.simplerecyclerview;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewbinding.ViewBinding;

import com.sum.simple.SimpleRecyclerView;
import com.sum.simpleadapter.BaseAdapter;
import com.sum.simpleadapter.base.ViewHolder;
import com.sum.simplerecyclerview.databinding.ActivityMainBinding;
import com.sum.simplerecyclerview.databinding.ItemMainBinding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author LiuJiang
 * created  at: 2024/5/7 15:35
 * Desc:
 */
public class MainActivity extends AppCompatActivity {
    ActivityMainBinding viewBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(viewBinding.getRoot());
        List<String> data = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            data.add("锄禾日当午" + i);
        }
        viewBinding.recyclerView.setAdapter(new BaseAdapter<ItemMainBinding, String>(this, data) {

            @Override
            protected ItemMainBinding getViewBinding(int viewType, LayoutInflater layoutInflater, ViewGroup parent) {
                return ItemMainBinding.inflate(layoutInflater, parent, false);
            }

            @Override
            protected void onBind(Context context, ViewHolder<ItemMainBinding> holder, String item, int position) {
                holder.binding.tvItemName.setText(item);
            }
        });
    }
}