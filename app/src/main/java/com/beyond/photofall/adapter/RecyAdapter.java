package com.beyond.photofall.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.beyond.photofall.R;
import com.beyond.photofall.controller.ZoomTutorial;
import com.beyond.photofall.entity.RecItem;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class RecyAdapter extends RecyclerView.Adapter {

    private Context cContext;
    private ViewPager cDetailView;
    private Fragment cFragment;
    private List<RecItem> itemList;

    /**
     * 把要展示的数据源传入，并赋值给全局变量 itemList
     */
    public RecyAdapter(Context context, List<RecItem> recItems) {
        this.cContext = context;
        this.itemList = recItems;
    }

    public RecyAdapter(Fragment fragment, List<RecItem> recItems, ViewPager detailView) {
        this.cFragment = fragment;
        this.cDetailView = detailView;
        this.itemList = recItems;
    }


    /**
     * 创建ViewHolder实例，加载recItem布局，传入到构造函数中，并返回ViewHolder实例
     *
     * @return ViewHolder实例
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.rec_item, parent, false);
        return new RecyViewHolder(view);
    }

    /**
     * 对recyclerView子项的数据进行赋值，在每个子项滚动到屏幕内时执行
     *
     * @param position 当前项的 item 实例位置
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, @SuppressLint("RecyclerView") final int position) {
        RecyViewHolder recyViewHolder = (RecyViewHolder) viewHolder;
        RecItem recItem = itemList.get(position);
        recyViewHolder.photoGrapher.setText(String.format("%s", recItem.getPhotoGrapher()));
        Log.d("POSITION", "onBindViewHolder: position = " + position);
        @SuppressLint("HandlerLeak") Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
                        Bitmap[] photoSet = (Bitmap[]) msg.obj;
                        Glide.with(recyViewHolder.itemView).load(photoSet[0]).into(recyViewHolder.imgView);
                        // 绑定给当前位置上的imageView控件
                        recyViewHolder.itemView.setTag(position);
                        recyViewHolder.itemView.setOnClickListener(v -> {
                            setViewPagerAndZoom(recyViewHolder.itemView, photoSet[1], cDetailView, position);
                        });

                        // 要不我再 new 一个 Handler，让他来再 Handle 新的 msg？
                        // 不！我传入一个Bitmap组就可以了！
                        break;
                    default:
                        break;
                }
            }
        };

        new Thread(() -> {
            Message msg = new Message();
            // obj实际上是一个String数组，0为略缩图URL，1为正常质量图URL
//            msg.obj = new String[]{recItem.getImgUrlThumb(), recItem.getImgUrlRegular()};
            try {
                // 这个obj是一个Bitmap数组，包含两个Bitmap，0是略缩图，1是正常质量图
                msg.obj = new Bitmap[]{getBitmapFromURL(recItem.getImgUrlThumb()), getBitmapFromURL(recItem.getImgUrlRegular())};
            } catch (IOException e) {
                e.printStackTrace();
            }
            mHandler.sendMessage(msg);
        }).start();
    }


    private void setViewPagerAndZoom(View thumbView, Bitmap regularPhoto, ViewPager cDetailView, int position) {
        // cDetailView: 得到要放大展示的视图界面
        // 最外层的容器，用来计算
        View containerView = cDetailView.getRootView().findViewById(R.id.container);

        // 实现放大缩小类，传入当前的容器和要放大展示的对象
        ZoomTutorial mZoomTutorial = new ZoomTutorial(containerView, cDetailView);

        ViewPagerAdapter adapter = new ViewPagerAdapter(cFragment, regularPhoto, mZoomTutorial);
        cDetailView.setAdapter(adapter);
        cDetailView.setCurrentItem(position);

        // 通过传入Id来从小图片扩展到大图，开始执行动画
        mZoomTutorial.zoomImageFromThumb(thumbView);

        mZoomTutorial.setOnZoomListener(new ZoomTutorial.OnZoomListener() {
            @Override
            public void onThumbed() {
                System.out.println("现在是-------------------> 小图状态");
            }

            @Override
            public void onExpanded() {
                System.out.println("现在是-------------------> 大图状态");
            }
        });
    }

    /**
     * 通过指向图片的 url 获取二进制 response 并转换为 bitmap 对象
     */
    private Bitmap getBitmapFromURL(String urlPath) throws IOException {

        URL url = new URL(urlPath);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5000);
        conn.setRequestMethod("GET");
        conn.connect();
        InputStream in = conn.getInputStream();

        return BitmapFactory.decodeStream(in);
    }

    /**
     * @return recyclerView 一共有多少子项
     */
    @Override
    public int getItemCount() {
        return itemList.size();
    }

    /**
     * 静态内部类
     */
    static class RecyViewHolder extends RecyclerView.ViewHolder {
        private TextView photoGrapher;  // 摄影师名称
        private ImageView imgView;      // 图片

        /**
         * 构造函数
         *
         * @param recyItemView RecyclerView 子项的最外层布局
         */
        RecyViewHolder(@NonNull View recyItemView) {
            super(recyItemView);
            photoGrapher = recyItemView.findViewById(R.id.grapherTextView);
            imgView = recyItemView.findViewById(R.id.recyImageView);
        }
    }
}

// https://blog.csdn.net/lixiang_Y/article/details/62238504 点击放大事件 参考
