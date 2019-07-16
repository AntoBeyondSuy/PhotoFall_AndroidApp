package com.beyond.photofall;

import android.Manifest;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.beyond.photofall.fragments.FragmentMain;
import com.beyond.photofall.fragments.FragmentMy;
import com.beyond.photofall.fragments.FragmentSearch;

public class MainActivity extends AppCompatActivity
        implements FragmentMain.OnFragmentInteractionListener,
        FragmentSearch.OnFragmentInteractionListener,
        FragmentMy.OnFragmentInteractionListener {

    final private String[] INTERNET_PER = {Manifest.permission.INTERNET};

    RecyclerView recView;
    private FragmentMain fragmentMain;
    private FragmentMain fragmentSearchResult;
    private FragmentSearch fragmentSearch;
    private FragmentMy fragmentMy;
    private Fragment[] fragments;
    private int lastFragment;       // 用于记录上个选择的Fragment

    private EditText txtKeyword;
    private Button btnSearch;

    /**
     * 在这里设置点击每个 navigation 的 item 之后的跳转
     */
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_new:
                if (lastFragment != 0) {
                    switchFragment(lastFragment, 0);
                    lastFragment = 0;
                }
                return true;
            case R.id.navigation_search:
                if (lastFragment != 1) {
                    switchFragment(lastFragment, 1);
                    lastFragment = 1;
                }
                return true;
            case R.id.navigation_mine:
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
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 4333);
        }

        recView = findViewById(R.id.recyclerView);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        fragmentMain = new FragmentMain();
        fragmentSearch = new FragmentSearch();
        fragmentMy = new FragmentMy();
        fragments = new Fragment[]{fragmentMain, fragmentSearch, fragmentMy};
        lastFragment = 0;
        // 初始化主页Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.mainView, fragmentMain).show(fragmentMain).commit();

    }

    /**
     * 切换 Fragment
     *
     * @param lastFragment 此时（切换前）所在的fragment编号
     * @param index        目的fragment编号
     */
    private void switchFragment(int lastFragment, int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastFragment]);      // 隐藏上个Fragment
        if (fragmentSearchResult != null) transaction.hide(fragmentSearchResult);
        if (!fragments[index].isAdded()) {
            transaction.add(R.id.mainView, fragments[index]);
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
                    Toast.makeText(MainActivity.this, "拒绝网络权限将无法使用程序！", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case 4333:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "拒绝存储权限将无法保存照片！", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            default:
                break;
        }
    }

    /**
     * 点击搜索按纽，获取输入内容，用之实例化FragmentMain，并切换
     *
     * @param view 此时的视图
     */
    public void searchBtn(View view) {
        txtKeyword = findViewById(R.id.searchKeyword);
        String keyword = txtKeyword.getText().toString();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastFragment]);      // 隐藏上个Fragment

        fragmentSearchResult = FragmentMain.newInstance(keyword);
        transaction.replace(R.id.mainView, fragmentSearchResult);
        transaction.show(fragmentSearchResult).commitAllowingStateLoss();

        lastFragment = 1;
//        btnSearch.setText(keyword);
    }

    /**
     * 实现三个Fragment类的接口
     *
     * @param uri realization
     */
    @Override
    public void onFragmentMainInteraction(Uri uri) {

    }

    @Override
    public void onFragmentSearchInteraction(View view) {

    }

    @Override
    public void onFragmentMyInteraction(Uri uri) {

    }
}
