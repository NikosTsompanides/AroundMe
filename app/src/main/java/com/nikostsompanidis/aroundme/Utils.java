package com.nikostsompanidis.aroundme;

import java.util.ArrayList;

/**
 * Created by Nikos Tsompanidis on 6/11/2017.
 */

public class Utils {

    static String IMGS[] = {
            "https://images.unsplash.com/photo-1444090542259-0af8fa96557e",
            "https://images.unsplash.com/photo-1439546743462-802cabef8e97",
            "https://images.unsplash.com/photo-1441155472722-d17942a2b76a",

    //more image links
};

public static ArrayList<ImageModel> getData() {
        ArrayList<ImageModel> arrayList = new ArrayList<>();
        for (int i = 0; i < IMGS.length; i++) {
        ImageModel imageModel = new ImageModel();
        imageModel.setName("Image " + i);
        imageModel.setUrl(IMGS[i]);
        arrayList.add(imageModel);
        }
        return arrayList;
        }
        }