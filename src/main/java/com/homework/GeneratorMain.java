package com.homework;

import com.homework.producer.KafkaProducerConstructive;
import com.homework.stream.StreamParser;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

public class GeneratorMain {

    public static void main(String[] args) throws IOException, ExecutionException, InterruptedException {
        StreamParser streamParser = new StreamParser("http://stream.meetup.com/2/rsvps");
        InputStream inputStream = streamParser.openStream();
        String result;
        KafkaProducerConstructive kafkaProducerConstructive = new KafkaProducerConstructive("storage");
        do {
            result = new BufferedReader(new InputStreamReader(inputStream)).readLine();
            try {
                JSONObject object = new JSONObject(result);
                kafkaProducerConstructive.sendMessage(object.toString());
            } catch (JSONException e) {
                System.out.println(e.getMessage());
            }
        } while (!result.isEmpty());

    }
}
