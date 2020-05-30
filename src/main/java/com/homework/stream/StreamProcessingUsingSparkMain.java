package com.homework.stream;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.homework.containers.Meeting;
import com.homework.containers.ProgrammingContainer;
import com.homework.containers.USMeetingContainer;
import com.homework.containers.WindowPerMinuteContainer;
import com.homework.containers.pcontainers.GroupTopic;
import com.homework.producer.KafkaProducerConstructive;
import kafka.serializer.StringDecoder;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;
import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

public class StreamProcessingUsingSparkMain {
    private static final KafkaProducerConstructive usMeetingProducer = new KafkaProducerConstructive("US-meetups");
    private static final KafkaProducerConstructive usMinutesProducer = new KafkaProducerConstructive("US-cities-every-minute");
    private static final List<String> programmersCodes = Arrays.asList("Computer programming",
            "Big Data",
            "Machine Learning",
            "Python",
            "Java",
            "Web Development");
    private static final KafkaProducerConstructive pMeetingProducer = new KafkaProducerConstructive("Programming-meetups");
    private static final ObjectMapper mapper = new ObjectMapper();
    private static int counter_1 = 1;
    private static int counter_2 = 1;
    private static int counter_3 = 1;

    private static void writeUsingFiles(String data, String file_name) {
        int counter = 0;
        if (file_name.equals("./results_from_test2.json"))
            counter = counter_1;
        if (file_name.equals("./results_from_test3.json"))
            counter = counter_2;
        if (file_name.equals("./results_from_test4.json"))
            counter = counter_3;
        data = "\"Result #" + counter + "\":" + data + ",";
        try {
            Files.createFile(Paths.get(file_name));
        } catch (IOException ignored) {
        }
        try {
            Files.write(Paths.get(file_name), data.getBytes(), StandardOpenOption.APPEND);
        } catch (IOException ignored) {
        }
        counter++;
        if (file_name.equals("./results_from_test2.json"))
            counter_1 = counter;
        if (file_name.equals("./results_from_test3.json"))
            counter_2 = counter;
        if (file_name.equals("./results_from_test4.json"))
            counter_3 = counter;
    }

    public static void main(String[] args) throws InterruptedException {
        SparkSession sparkSession = SparkSession.builder().master("local[*]")
                .appName("Homework4").getOrCreate();
        JavaSparkContext sc = new JavaSparkContext(sparkSession.sparkContext());
        JavaStreamingContext ssc = new JavaStreamingContext(sc, Durations.minutes(1));
        final long[] time = {sparkSession.sparkContext().startTime()};
        Set<String> topics = Collections.singleton("storage");
        Map<String, String> kafkaParams = new HashMap<>();
        kafkaParams.put("metadata.broker.list", "localhost:9092");
        JavaPairInputDStream<String, String> directKafkaStream = KafkaUtils.createDirectStream(ssc,
                String.class, String.class, StringDecoder.class, StringDecoder.class, kafkaParams, topics);

        directKafkaStream.foreachRDD(rdd -> {
            System.out.println("--- New RDD with " + rdd.partitions().size()
                    + " partitions and " + rdd.count() + " records");
            rdd.foreach(record -> {
                JSONObject jsonObject = new JSONObject(record._2);

                Meeting m = mapper.readValue(jsonObject.toString(), Meeting.class);
                if (m.group.group_country.equals("us")) {
                    USMeetingContainer meetingContainer = new USMeetingContainer(m);
                    String jsonStr = mapper.writeValueAsString(meetingContainer);
                    writeUsingFiles(jsonStr, "./results_from_test2.json");
                    usMeetingProducer.sendMessage(jsonStr);
                }
            });
        });
        JavaDStream<String> citiesPerMinute = directKafkaStream.window(Durations.minutes(1)).filter(rdd ->
        {
            JSONObject jsonObject = new JSONObject(rdd._2);
            Meeting m = mapper.readValue(jsonObject.toString(), Meeting.class);
            return (m.group.group_country.equals("us"));
        }).map((rdd) -> {
            JSONObject jsonObject = new JSONObject(rdd._2);
            Meeting m = mapper.readValue(jsonObject.toString(), Meeting.class);
            return m.group.group_city;
        });
        JavaDStream<List<String>> j = citiesPerMinute.glom();
        j.foreachRDD(listJavaRDD -> {
            try {
                List<String> list = listJavaRDD.first();
                if (list.size() > 0) {
                    list = list.stream().distinct().collect(Collectors.toList());
                    WindowPerMinuteContainer window = new WindowPerMinuteContainer(time[0], list);
                    String jsonStr = mapper.writeValueAsString(window);
                    usMinutesProducer.sendMessage(jsonStr);
                    writeUsingFiles(jsonStr, "./results_from_test3.json");
                    time[0] += 60000;
                }
            } catch (IllegalArgumentException ignored) {
            }
        });
        JavaPairDStream<String, String> programersStream = directKafkaStream.filter((rdd) -> {
            JSONObject jsonObject = new JSONObject(rdd._2);
            Meeting m = mapper.readValue(jsonObject.toString(), Meeting.class);
            return (m.group.group_country.equals("us"));
        }).filter((rdd) -> {
            JSONObject jsonObject = new JSONObject(rdd._2);
            Meeting m = mapper.readValue(jsonObject.toString(), Meeting.class);
            for (GroupTopic g : m.group.group_topics) {
                if (programmersCodes.contains(g.topic_name))
                    return true;
            }
            return false;
        });
        programersStream.foreachRDD((rdd) -> rdd.foreach(record -> {
            JSONObject jsonObject = new JSONObject(record._2);
            Meeting m = mapper.readValue(jsonObject.toString(), Meeting.class);
            ProgrammingContainer programmingContainer = new ProgrammingContainer(m);
            String jsonStr = mapper.writeValueAsString(programmingContainer);
            pMeetingProducer.sendMessage(jsonStr);
            writeUsingFiles(jsonStr, "./results_from_test4.json");
        }));
        ssc.start();
        ssc.awaitTermination();
    }
}
