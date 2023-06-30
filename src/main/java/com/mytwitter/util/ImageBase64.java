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

    private static String getNewId(String directory){
        try {
            FileInputStream in = new FileInputStream(directory + "/last_id.txt");
            int lastId = Integer.parseInt(new String(in.readAllBytes()));
            in.close();
            String newId = (""+(lastId+1));

            FileOutputStream out = new FileOutputStream(directory + "/last_id.txt");
            out.write(newId.getBytes());
            out.close();
            return newId;
        } catch (FileNotFoundException e) {
            File dir = new File(directory);
            dir.mkdir();
            try {
                FileOutputStream out = new FileOutputStream(directory + "/last_id.txt");
                out.write("1".getBytes());
                out.close();
                return "1";
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String downloadImage(String directory, String image){
        byte[] bytes = Base64.getDecoder().decode(image);
        String newLocation = directory + "/"+getNewId(directory)+".jpg";
        try(FileOutputStream out = new FileOutputStream(newLocation)) {
            out.write(bytes);
        } catch (IOException e){
            e.printStackTrace();
        }
        return newLocation;
    }

    public static String getImageFromLocation(String location) throws FileNotFoundException {

        if(location == null)
            return null;
        InputStream file = new FileInputStream(location);
        return convertToBase64(file);
    }


}
