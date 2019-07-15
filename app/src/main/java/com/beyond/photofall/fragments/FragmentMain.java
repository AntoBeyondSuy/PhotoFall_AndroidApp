package com.beyond.photofall.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
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
    private static final String SEARCH_KEYWORD = "keyword";

    private final static String REQUEST_URL = "https://api.unsplash.com/";
    private final static String ACCESS_KEY = "client_id=7dbd22e90f148d7173b6632d4857a68cc414362cadfdd6de721eb916600cdbb7";
    private final static String QUALITY_THUMB = "thumb";  // high to low: raw, full, regular, small, thumb
    private final static String QUALITY_REGULAR = "regular";  // high to low: raw, full, regular, small, thumb
    private final static String USER_PARAM = "name";
    String QUERY = "search/photos?query=";  // eg:query=minimal

    private RecyclerView recView;
    private ViewPager detailView;
    private RecyAdapter recyAdapter;
    private ArrayList<RecItem> recItems;
    private String mKeyword;

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
    public static FragmentMain newInstance(String keyword) {
        FragmentMain fragment = new FragmentMain();
        Bundle args = new Bundle();
        args.putString(SEARCH_KEYWORD, keyword);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 获取fragment中的arguments，为搜索关键词 keyword 赋值
        mKeyword = getArguments() != null ? getArguments().getString(SEARCH_KEYWORD) : "";
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);
//        recView = getActivity().findViewById(R.id.recyclerView);
        recView = view.findViewById(R.id.recyclerView);
        detailView = view.findViewById(R.id.detailView);
        StaggeredGridLayoutManager sgLayoutManager =
                new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recView.setLayoutManager(sgLayoutManager);
        showPhotosInRecview(recView, detailView);
        return view;
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
        void onFragmentMainInteraction(Uri uri);
    }

    public void showPhotosInRecview(RecyclerView recView, ViewPager detailView) {
        @SuppressLint("HandlerLeak") Handler mHandler = new Handler() {
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
            recyAdapter = new RecyAdapter(FragmentMain.this, recItems, detailView);
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
            JsonArray jsonArray;
            if ("[".equals(jsonString.substring(0, 1))) {
                jsonArray = (JsonArray) parser.parse(jsonString);
            } else {
                jsonArray = parser.parse(jsonString).getAsJsonObject().get("results").getAsJsonArray();
            }

            /* 注意：如果发送的是查询请求而不是获取最新图片的请求，则返回的是 JsonObject 对象格式的字符串 */
            // 通过 String 创建 JsonObject 对象
//            JsonObject jsonObject = (JsonObject) parser.parse(jsonString);
            // 获取 JsonObject 中的 JsonArray，继续对 JsonArray 进行操作
//            JsonArray jsonArray = (JsonArray) jsonObject.get("results");

            // 遍历 JsonArray 对象，获取每一个图片信息，并提取 URL 和 user 的信息
            for (int i = 0; i < jsonArray.size(); i++) {
                JsonObject subObject = jsonArray.get(i).getAsJsonObject(); // subObject 是最外层每对 {} 中的内容，第一项是 id
                // 获取每个组（subObject）中的 urls 组中的 thumb（图片质量：略缩图） 的 url，String
                String imgUrlThumb = subObject.get("urls").getAsJsonObject().get(QUALITY_THUMB).getAsString();
                // 获取 regular（正常质量）的 url，String
                String imgUrlRegular = subObject.get("urls").getAsJsonObject().get(QUALITY_REGULAR).getAsString();
                String imgUserName = subObject.get("user").getAsJsonObject().get(USER_PARAM).getAsString();
                RecItem ri = new RecItem(imgUserName, imgUrlThumb, imgUrlRegular);
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
        Log.d("mKeyword", "newInstance: mKeyword = " + mKeyword);
        Request jsonRequest;
        if ("".equals(mKeyword)) {
            jsonRequest = new Request.Builder().url(REQUEST_URL + "photos?" + ACCESS_KEY + "&per_page=50").build();
        } else {
            jsonRequest = new Request.Builder().url(REQUEST_URL + QUERY + mKeyword + "&" + ACCESS_KEY + "&per_page=50").build();
        }
        Response jsonResponse = okHttpClient.newCall(jsonRequest).execute();
        if (!jsonResponse.isSuccessful()) throw new
                IOException("Unexpected code " + jsonResponse);
        assert jsonResponse.body() != null;
        return jsonResponse.body().string();
    }


}
