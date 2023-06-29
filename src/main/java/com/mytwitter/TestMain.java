package com.mytwitter;

import com.mytwitter.client.Requester;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMain {
    public static void main(String[] args) {
        Requester requester = Requester.login("mmd", "1234");
        if (requester != null) {
            System.out.println(requester.getReplies(28));
        }
    }


}
