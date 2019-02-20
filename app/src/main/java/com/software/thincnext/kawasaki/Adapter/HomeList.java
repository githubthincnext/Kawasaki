package com.software.thincnext.kawasaki.Adapter;

public class HomeList {
    private int imageId;
    private int title;
    private String subtitle;
    public HomeList(Integer imageId,int title){
        this.imageId=imageId;
        this.title =title;
        this.subtitle=subtitle;
    }
    public int getImageId(){return imageId;}
    public void setImageId(int imageId){
        this.imageId=imageId;
    }
    public  int getTitle(){return title;}
    public void setTitle(int title){
        this.title=title;
    }
    public String getSubtitle(){return subtitle;}
    public void setSubtitle(String subtitle){
        this.subtitle=subtitle;
    }
}
