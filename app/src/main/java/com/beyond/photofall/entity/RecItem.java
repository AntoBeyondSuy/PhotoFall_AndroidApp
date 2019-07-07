package com.beyond.photofall.entity;

public class RecItem {

    private String photoGrapher;
    private String imgUrlThumb;
    private String imgUrlRegular;
    private int NativeImg;

    public RecItem(String photoGrapher, int nativeImg) {
        this.photoGrapher = photoGrapher;
        this.NativeImg = nativeImg;
    }

    public RecItem(String photoGrapher, String imgUrl) {
        this.photoGrapher = photoGrapher;
        this.imgUrlThumb = imgUrl;
    }

    public RecItem(String photoGrapher, String imgUrlThumb, String imgUrlRegular) {
        this.photoGrapher = photoGrapher;
        this.imgUrlThumb = imgUrlThumb;
        this.imgUrlRegular = imgUrlRegular;
    }

    public String getImgUrlRegular() {
        return imgUrlRegular;
    }

    public int getNativeImg() {
        return NativeImg;
    }

    public String getPhotoGrapher() {
        return photoGrapher;
    }

    public String getImgUrlThumb() {
        return imgUrlThumb;
    }

}
