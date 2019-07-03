package com.beyond.photofall;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
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

public class MainActivity extends AppCompatActivity {
    final static String REQUEST_URL = "https://api.unsplash.com/photos/";
    final static String ACCESS_KEY = "?client_id=7dbd22e90f148d7173b6632d4857a68cc414362cadfdd6de721eb916600cdbb7";
    String QUERY = "search/photos?query=";  // eg:query=minimal
    final static String QUALITY = "thumb";  // high to low: raw, full, regular, small, thumb
    final static String USER_PARAM = "name";

    final private String[] INTERNET_PER = {Manifest.permission.INTERNET};

    private TextView mTextMessage;
    RecyclerView recView;
    RecyAdapter recyAdapter;
    ArrayList<RecItem> recItems;
    private BottomNavigationView bottomNavigationView;
    private FragmentMain fragmentMain;
    private FragmentSearch fragmentSearch;
    private FragmentMy fragmentMy;
    private Fragment[] fragments;
    private int lastFragment;       // 用于记录上个选择的Fragment


    // 在这里设置点击每个 navigation 的 item 之后的跳转
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.navigation_home:
//                Intent intent = new Intent(this, MainActivity.this);
//                startActivity(intent);
                if (lastFragment != 0) {
                    switchFragment(lastFragment, 0);
                    lastFragment = 0;
                }
                // enterMain(recView);
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


        FragmentMain fragmentMain = new FragmentMain();
        FragmentSearch fragmentSearch = new FragmentSearch();
        FragmentMy fragmentMy = new FragmentMy();
        Fragment[] fragments = {fragmentMain, fragmentSearch, fragmentMy};
        lastFragment = 0;
        // 初始化主页Fragment
        getSupportFragmentManager().beginTransaction().replace(R.id.recyclerView, fragmentMain).show(fragmentMain).commit();

        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        StaggeredGridLayoutManager sgLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);

        recView = findViewById(R.id.recyclerView);
        recView.setLayoutManager(sgLayoutManager);

        enterMain(recView);
    }

    // 切换 Fragment
    private void switchFragment(int lastfragment, int index) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.hide(fragments[lastfragment]);//隐藏上个Fragment
        if (!fragments[index].isAdded()) {
            transaction.add(R.id.message, fragments[index]);
            // transaction.add(R.id.mainview,com.beyond.photofall.fragments[index]);
        }
        transaction.show(fragments[index]).commitAllowingStateLoss();
    }


    public void enterMain(RecyclerView recView) {
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
//                    jsonResponse = (Response) msg.obj;
                        recView.setAdapter(recyAdapter);    // 设置适配器
                        break;
                    default:
                        break;
                }
            }
        };
        OkHttpClient okHttpClient = new OkHttpClient();
        new Thread(() -> {
            try {
                recItems = jsonParser(getData(okHttpClient));
            } catch (IOException e) {
                e.printStackTrace();
            }

            recyAdapter = new RecyAdapter(MainActivity.this, recItems);
            mHandler.sendEmptyMessage(0);
//            recView.setAdapter(recyAdapter);    // 设置适配器

//            for (RecItem ri : recItems) {
//                new getImgFromUrlThread(ri, okHttpClient, recView).start();
//            new getImgFromUrlThread(ri, okHttpClient, handler, recView).start();
//            }   // end of for-loop
        }).start();
    }

    public void enterSearch(RecyclerView recView) {
        Handler mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case 0:
//                    jsonResponse = (Response) msg.obj;
                        recView.setAdapter(recyAdapter);    // 设置适配器
                        break;
                    default:
                        break;
                }
            }
        };
        OkHttpClient okHttpClient = new OkHttpClient();
        new Thread(() -> {
            try {
                recItems = jsonParser(getData(okHttpClient));
            } catch (IOException e) {
                e.printStackTrace();
            }

            recyAdapter = new RecyAdapter(MainActivity.this, recItems);
            mHandler.sendEmptyMessage(0);
        }).start();
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

    /**
     * 解析 String 类型的 JSON 数据，获取图片URL和对应的作者名，保存为一个 RecItem 并加入 ArrayList
     *
     * @param jsonString 被解析的 JSON
     * @return 返回保存 RecItem 的 ArrayList
     */
    private ArrayList<RecItem> jsonParser(String jsonString) {
        ArrayList<RecItem> recItems = new ArrayList<>();
        try {

            JsonParser parser = new JsonParser();   // 创建JSON解析器
            /* 注意：发送的是获取最新图片的请求，返回的是 JsonArray 对象格式的字符串 */
            // 通过 String 创建 JsonArray 对象
            JsonArray jsonArray = (JsonArray) parser.parse(jsonString);

            /* 注意：如果发送的是查询请求而不是获取最新图片的请求，则返回的是 JsonObject 对象格式的字符串 */
            // 通过 String 创建 JsonObject 对象
//            JsonObject jsonObject = (JsonObject) parser.parse(jsonString);
            // 获取 JsonObject 中的 JsonArray，继续对 JsonArray 进行操作
//            JsonArray jsonArray = (JsonArray) jsonObject.get("results");

            // 遍历 JsonArray 对象，获取每一个图片信息，并提取 URL 和 user 的信息
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject subObject = jsonArray.get(i).getAsJsonObject(); // subObject 是最外层每对 {} 中的内容，第一项是 id
                String imgUrl = subObject.get("urls").getAsJsonObject().get(QUALITY).getAsString(); // 获取每个组（subObject）中的 urls 组中的 small 的 url，String
                String imgUserName = subObject.get("user").getAsJsonObject().get(USER_PARAM).getAsString();
//                System.out.println(imgUrl);
                RecItem ri = new RecItem(imgUserName, imgUrl);
                recItems.add(ri);
            }
        } catch (JsonIOException e) {
            e.printStackTrace();
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }
        return recItems;
    }

    private String getData(OkHttpClient okHttpClient) throws IOException {
        Request jsonRequest = new Request.Builder().url(REQUEST_URL + ACCESS_KEY).build();
        Response jsonResponse = okHttpClient.newCall(jsonRequest).execute();
        if (!jsonResponse.isSuccessful()) throw new IOException("Unexpected code " + jsonResponse);
        return jsonResponse.body().string();
    }

}
