package com.beyond.photofall.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.beyond.photofall.R;
import com.beyond.photofall.adapter.RecyAdapter;
import com.beyond.photofall.entity.RecItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentMain.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentMain#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentMain extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private Context context;

    private final static String REQUEST_URL = "https://api.unsplash.com/photos/";
    private final static String ACCESS_KEY = "?client_id=7dbd22e90f148d7173b6632d4857a68cc414362cadfdd6de721eb916600cdbb7";
    private String QUERY = "search/photos?query=";  // eg:query=minimal
    private final static String QUALITY = "thumb";  // high to low: raw, full, regular, small, thumb
    private final static String USER_PARAM = "name";

    RecyclerView recView;
    RecyAdapter recyAdapter;
    ArrayList<RecItem> recItems;

    // TODO: Rename and change types of parameters

    private OnFragmentInteractionListener mListener;

    public FragmentMain() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment FragmentMain.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentMain newInstance() {
        return new FragmentMain();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
/*
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }*/
    }

    /**
     * onCreate operations here !
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        StaggeredGridLayoutManager sgLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        FragmentActivity activity = getActivity();
        recView = activity.findViewById(R.id.recyclerView);
        recView.setLayoutManager(sgLayoutManager);

        showPhotosInRecview(recView);

        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }


    public void showPhotosInRecview(RecyclerView recView) {
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
            recyAdapter = new RecyAdapter(FragmentMain.this, recItems);
            mHandler.sendEmptyMessage(0);
        }).start();
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