package com.homework.producer;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.serialization.LongSerializer;
import org.apache.kafka.common.serialization.StringSerializer;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

public class KafkaProducerConstructive {
    private final static String BOOTSTRAP_SERVERS =
            "localhost:9092";
    private KafkaProducer<Long, String> producer;
    private String topic;

    public KafkaProducerConstructive(String topic) {
        this.topic = topic;
        this.producer = createProducer();
    }

    private KafkaProducer<Long, String> createProducer() {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                BOOTSTRAP_SERVERS);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                LongSerializer.class.getName());
        props.put(ProducerConfig.CLIENT_ID_CONFIG, this.topic);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class.getName());
        return new KafkaProducer<>(props);
    }

    public RecordMetadata sendMessage(String message) throws ExecutionException, InterruptedException {
        final ProducerRecord<Long, String> record = new ProducerRecord<>(this.topic, message);
        return this.producer.send(record).get();
    }
}
