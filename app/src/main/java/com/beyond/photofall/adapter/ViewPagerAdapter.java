package com.beyond.photofall.adapter;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.LayoutParams;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.beyond.photofall.controller.ZoomTutorial;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 实现滑动展示下一张图片
 */
public class ViewPagerAdapter extends PagerAdapter {

    private Bitmap mRegularPhoto;
    private Fragment mFragment;
    private ZoomTutorial mZoomTutorial;
    private File outputFile;

    ViewPagerAdapter(Fragment fragment, Bitmap regularPhoto, ZoomTutorial zoomTutorial) {
        this.mFragment = fragment;
        this.mRegularPhoto = regularPhoto;
        this.mZoomTutorial = zoomTutorial;
    }

    @Override
    public int getCount() {
        return 50;
    }

    @Override
    public View instantiateItem(ViewGroup container, final int position) {
        final ImageView imageView = new ImageView(mFragment.getContext());
        imageView.setImageBitmap(mRegularPhoto);
        Log.d("POSITION", "instantiateItem: position = " + position);
        container.addView(imageView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);

        imageView.setOnClickListener(view -> mZoomTutorial.closeZoomAnim(position));
        imageView.setOnLongClickListener(v -> {
            File file = new File(Environment.getExternalStorageDirectory(), "Photo_");
            outputFile = new File(file + createFileName());
            FileOutputStream fileOutputStream;
            try {
                fileOutputStream = new FileOutputStream(outputFile);
                mRegularPhoto.compress(Bitmap.CompressFormat.JPEG, 90, fileOutputStream);
                fileOutputStream.flush();
                fileOutputStream.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Toast.makeText(mFragment.getActivity(), "照片已保存：" + outputFile.toString(), Toast.LENGTH_SHORT).show();


            // 在内部存储上创建一个文件
            // Environment.getExternalStorageDirectory() = "/storage/emulated/0"
            /*outputFile = new File(dir + createFileName());
            if (!outputFile.exists()) {
                try {
                    outputFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, mRegularPhoto);
            Toast.makeText(mFragment.getActivity(), "照片已保存：" + outputFile.toString(), Toast.LENGTH_SHORT).show();
*/
            return true;
        });
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

    /**
     * create file name accroding to datetime, so filenames can be different from each other and prevent coverage
     *
     * @return String filename
     */
    private static String createFileName() {
        String filename;
        Date date = new Date(System.currentTimeMillis());
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
        filename = dateFormat.format(date) + ".jpg";
        return filename;
    }

}
