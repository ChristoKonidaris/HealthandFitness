package com.opsc7311.healthandfitness19013888;

import com.google.firebase.database.Exclude;

public class UploadMeal {

    private String mName;
    private String mImageUrl;
    private String mKey;

    public UploadMeal() {
        //empty constructor needed
    }

    public UploadMeal(String name, String imageUrl) {
        if (name.trim().equals("")) {
            name = "No Date was Provided";
        }
        mName = name;
        mImageUrl = imageUrl;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        mImageUrl = imageUrl;
    }

    @Exclude
    public String getmKey() {
        return mKey;
    }
@Exclude
    public void setmKey(String mKey) {
        this.mKey = mKey;
    }
}
