package com.beyond.photofall.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.beyond.photofall.R;
import com.beyond.photofall.entity.RecItem;
import com.bumptech.glide.Glide;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;


public class RecyAdapter extends RecyclerView.Adapter {

    private Context cContext;
    private Fragment fragment;
    private Bitmap bitmap;
    private InputStream in;
    private List<RecItem> itemList;

    /**
     * 把要展示的数据源传入，并赋值给全局变量 itemList
     */
    public RecyAdapter(Context context, List<RecItem> recItems) {
        this.cContext = context;
        this.itemList = recItems;
    }

    public RecyAdapter(Fragment fragment, List<RecItem> recItems) {
        this.fragment = fragment;
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
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int position) {
        RecyViewHolder recyViewHolder = (RecyViewHolder) viewHolder;
        RecItem recItem = itemList.get(position);
//        System.out.println("position = " + position);
        recyViewHolder.photoGrapher.setText(String.format("%s", recItem.getPhotoGrapher()));

        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
/*                        String[] urls = (String[]) msg.obj;
                        Bitmap imgThumb = null;
                        Bitmap imgRegular= null;
                        try {
                            imgThumb = getBitmapFromURL(urls[0]);
                            imgRegular = getBitmapFromURL(urls[1]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        Glide.with(recyViewHolder.itemView).load((Bitmap)msg.obj).into(recyViewHolder.imgView);
//                        Glide.with(recyViewHolder.itemView).load(imgThumb).into(recyViewHolder.imgView);
                        // 绑定给当前位置上的imageView控件
                        recyViewHolder.itemView.setTag(position);
                        /*recyViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                String thumbUrl = urls[1];
                            }
                        });*/
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
                msg.obj = getBitmapFromURL(recItem.getImgUrlThumb());
            } catch (IOException e) {
                e.printStackTrace();
            }
            mHandler.sendMessage(msg);
        }).start();
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
        in = conn.getInputStream();
        bitmap = BitmapFactory.decodeStream(in);

        return bitmap;
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
