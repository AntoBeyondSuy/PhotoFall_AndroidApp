package com.beyond.photofall.entity;

public class RecItem {

    private String photoGrapher;
    private String imgURL;
    private int NativeImg;

    public RecItem(String photoGrapher, int nativeImg) {
        this.photoGrapher = photoGrapher;
        this.NativeImg = nativeImg;
    }

    public RecItem(String photoGrapher, String imgURL) {
        this.photoGrapher = photoGrapher;
        this.imgURL = imgURL;
    }

    public int getNativeImg() {
        return NativeImg;
    }

    public String getPhotoGrapher() {
        return photoGrapher;
    }

    public String getImgURL() {
        return imgURL;
    }

}
