package com.mytwitter.util;

import javafx.scene.image.Image;

import java.io.*;
import java.util.Base64;

public class ImageBase64 {
    public static String convertToBase64(InputStream file){
        try {
            byte[] bytes = file.readAllBytes();
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static InputStream convertToStream(String image){
        return new ByteArrayInputStream(Base64.getDecoder().decode(image));
    }
}
