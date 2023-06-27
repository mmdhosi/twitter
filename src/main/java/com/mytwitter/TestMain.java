package com.mytwitter;

import com.mytwitter.server.database.Database;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TestMain {
    public static void main(String[] args) {
        Logger log = LoggerFactory.getLogger(TestMain.class);
        log.info("Server started");
        Database database=new Database();
        System.out.println(database.getTimeline("mmd"));

    }


}
