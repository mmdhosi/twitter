package com.mytwitter.util;

import javafx.scene.image.Image;

public class ProfileImage {
    public static Image getAvatarImage(String img){
        if(img == null || img.equals(""))
            return new Image("file:icons/blank_avatar.jpg");
        else
            return new Image(ImageBase64.convertToStream(img));

    }
    public static Image getHeaderImage(String img){
        if(img == null || img.equals(""))
            return new Image("file:icons/blank_header.jpg");
        else
            return new Image(ImageBase64.convertToStream(img));

    }
}
