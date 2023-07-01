package com.mytwitter.client;

import com.mytwitter.direct.Message;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;

public class SeenChangeListener implements ChangeListener<Message> {
    private final ObservableList<Message> observableList;
    int lastSeenId;

    static boolean createdBefore = false;
    boolean firstInstance;

    public SeenChangeListener(ObservableList<Message> observableList, int lastSeenId) {
        this.observableList = observableList;
        this.lastSeenId = lastSeenId;
        if(createdBefore)
            firstInstance = false;
        else {
            createdBefore = true;
            firstInstance = true;
        }

    }


    //FIXME: currently not working. sets every message seen
    @Override
    public void changed(ObservableValue<? extends Message> observableValue, Message message, Message t1) {
        if (t1 != null) {
            if (observableList.indexOf(t1) >= lastSeenId && firstInstance) {
                Requester.getRequester().setSeen(t1.getId());
                lastSeenId += 1;
                System.out.println(lastSeenId);
            }
        }
    }
}
