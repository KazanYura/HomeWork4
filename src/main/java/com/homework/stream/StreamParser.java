package com.homework.stream;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class StreamParser {
    private String url;

    public StreamParser(String url) {
        this.url = url;
    }

    public InputStream openStream() {
        try {
            return new URL(this.url).openStream();
        } catch (IOException e) {
            System.out.println("Error happened");
            e.printStackTrace();
        }
        return null;
    }
}
