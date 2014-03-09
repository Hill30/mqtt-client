package com.hill30.android.mqttClient;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class MessageStash {
    private long timestamp;
    private int prefix;

    private List<Message> stash = new LinkedList<Message>();
    private File path;

    public Iterable<Message> get() {
        return new LinkedList<Message>(stash);
    }

    public class Message {
        private final String topic;
        private final String body;
        private final File file;

        Message(String topic, String body, File file) {
            this.topic = topic;
            this.body = body;
            this.file = file;
        }

        public void commit() {
            file.delete();
            stash.remove(this);
        }

        public String body() {
            return body;
        }

        public String topic() {
            return topic;
        }
    }

    public MessageStash(String path) throws IOException {

        this.path = new File(path + "/message-stash");
        this.path.mkdirs();
        File[] files = this.path.listFiles();
        Arrays.sort(files, new Comparator<File>() {
            public int compare(File f1, File f2) {
                int approx = Long.valueOf(f1.lastModified()).compareTo(f2.lastModified()); // compare tiemstamps
                if (approx != 0)
                    return approx;
                return f1.compareTo(f2); // if timestamps are the same compare names
            }
        });
        timestamp = System.currentTimeMillis();
        for(File file : files) {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            StringBuffer stringBuilder = new StringBuffer();
            String ls = System.getProperty("line.separator");
            while( ( line = reader.readLine() ) != null ) {
                stringBuilder.append( line );
                stringBuilder.append( ls );
            }
            String[] tokens = stringBuilder.toString().split("\n", 2);
            stash.add(new Message(tokens[0], tokens[1], file));
            if (timestamp < file.lastModified())
                timestamp = file.lastModified();
        }
        prefix = 0;
    }

    public void put(String topic, String message) throws IOException {

        if (timestamp != System.currentTimeMillis()) {
            prefix = 0;
        }
        File file = File.createTempFile(String.format("%05d", prefix++), null, path);
        OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file));
        writer.write(topic + "\n" + message);
        writer.close();
        timestamp = file.lastModified();

        stash.add(new Message(topic, message, file));
    }
}
