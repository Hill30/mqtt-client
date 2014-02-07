package com.hill30.android.mqttClient;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by michaelfeingold on 2/6/14.
 */
public class MessageStash {

    // todo: implement stash over file system. Message to be resubmitted preserving their order

    private List<Message> stash = new LinkedList<Message>();

    public Iterable<Message> get() {
        return new LinkedList<Message>(stash);
    }

    public class Message {
        String topic;
        String body;
        public void commit() {
            stash.remove(this);
        }
    }

    public MessageStash(String path) {
    }

    public void put(String topic, String message) {
        Message m = new Message();
        m.topic = topic;
        m.body = message;
        stash.add(m);
    }
}
