package com.beyond.photofall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.widget.TextView;
import android.widget.Toast;

import com.beyond.photofall.fragments.FragmentMain;
import com.beyond.photofall.fragments.FragmentMy;
import com.beyond.photofall.fragments.FragmentSearch;
import com.google.gson.*;

import com.beyond.photofall.adapter.RecyAdapter;
import com.beyond.photofall.entity.RecItem;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity
        implements FragmentMain.OnFragmentInteractionListener,
                    FragmentSearch.OnFragmentInteractionListener,
                    FragmentMy.OnFragmentInteractionListener {
    final static String REQUEST_URL = "https://api.unsplash.com/photos/";
    final static String ACCESS_KEY = "?client_id=7dbd22e90f148d7173b6632d4857a68cc414362cadfdd6de721eb916600cdbb7";
    String QUERY = "search/photos?query=";  // eg:query=minimal
    final static String QUALITY = "thumb";  // high to low: raw, full, regular, small, thumb
    final static String USER_PARAM = "name";

    final private String[] INTERNET_PER = {Manifest.permission.INTERNET};

    private TextView mTextMessage;
    RecyclerView recView;
    private BottomNavigationView bottomNavigationView;
    private FragmentMain fragmentMain;
    private FragmentSearch fragmentSearch;
    private FragmentMy fragmentMy;
    private Fragment[] fragments;
    private int lastFragment;       // 用于记录上个选择的Fragment


    /**
     * 在这里设置点击每个 navigation 的 item 之后的跳转
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                if (lastFragment != 0) {
//                    FragmentMain fragmentMain = FragmentMain.newInstance();
                    switchFragment(lastFragment, 0);
//                    switchFragment(lastFragment, 0);
                    lastFragment = 0;
                }
                // showPhotosInRecview(recView);
                return true;
            case R.id.navigation_dashboard:
//                mTextMessage.setText(R.string.title_dashboard);
                if (lastFragment != 1) {
                    switchFragment(lastFragment, 1);
                    lastFragment = 1;
                }
                return true;
            case R.id.navigation_notifications:
//                mTextMessage.setText(R.string.title_notifications);
                if (lastFragment != 2) {
                    switchFragment(lastFragment, 2);
                    lastFragment = 2;
                }
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 申请网络权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, INTERNET_PER, 3210);
        }

        mTextMessage = findViewById(R.id.message);
        recView = findViewById(R.id.recyclerView);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentMain = new FragmentMain();
        fragmentSearch = new FragmentSearch();
        fragmentMy = new FragmentMy();
        fragments = new Fragment[]{fragmentMain, fragmentSearch, fragmentMy};
        lastFragment = 0;
        // 初始化主页Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.mainLinearView, fragmentMain).show(fragmentMain).commit();
/*        StaggeredGridLayoutManager sgLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);*/

        /*recView = findViewById(R.id.recyclerView);
        recView.setLayoutManager(sgLayoutManager);

        showPhotosInRecview(recView);*/
    }

    /**
     * 切换 Fragment
     * @param lastfragment 此时（切换前）所在的fragment编号
     * @param index 目的fragment编号
     */
    private void switchFragment(int lastfragment, int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
        if (!fragments[index].isAdded()) {

            transaction.add(R.id.message, fragments[index]);
            // transaction.add(R.id.mainview,com.beyond.photofall.fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }

    /**
     * 实现系统权限管理
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 3210:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "拒绝权限将无法使用程序", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onFragmentMainInteraction(Uri uri) {

    }

    @Override
    public void onFragmentSearchInteraction(Uri uri) {

    }

    @Override
    public void onFragmentMyInteraction(Uri uri) {

    }
}
