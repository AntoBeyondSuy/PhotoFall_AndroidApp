package com.beyond.photofall.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ImageView;

import com.beyond.photofall.controller.ZoomTutorial;
import com.bumptech.glide.Glide;

/**
 * 实现滑动展示下一张图片
 */
public class ViewPagerAdapter extends PagerAdapter {

    private Bitmap mRegularPhoto;
    private Fragment mFragment;
    private ZoomTutorial mZoomTutorial;

    ViewPagerAdapter(Fragment fragment, Bitmap regularPhoto, ZoomTutorial zoomTutorial) {
        this.mFragment = fragment;
        this.mRegularPhoto = regularPhoto;
        this.mZoomTutorial = zoomTutorial;
    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        final ImageView imageView = new ImageView(mFragment.getContext());
        imageView.setImageBitmap(mRegularPhoto);
        Log.e("ViewPagerAdapter", "instantiateItem: position = " + position);
        container.addView(imageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        imageView.setOnClickListener(view -> mZoomTutorial.closeZoomAnim(position));
        return imageView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

}
