package com.esi.kafka.kerberos;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.common.TopicPartition;

public class KafkaKeytabConsumer {
	static KafkaConsumer<String, String> kafkaConsumer = null;

	public static void main(String[] args) {
		
		Properties properties = new Properties();
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "ch3dr028353.express-scripts.com:6667,ch3dr028354.express-scripts.com:6667,ch3dr028355.express-scripts.com:6667");
		properties.put(ConsumerConfig.GROUP_ID_CONFIG, "kafkasvcpoc1-group"); //The identifier of the group this consumer belongs to.
		properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		properties.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		properties.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,"SASL_PLAINTEXT");// Security protocol to use for communication.
		//properties.put("sasl.kerberos.service.name", "kafka");
		
		kafkaConsumer = new KafkaConsumer<String, String>(properties);
		TestConsumerRebalanceListener rebalanceListener = new TestConsumerRebalanceListener();
		kafkaConsumer.subscribe(Collections.singletonList("SET_YOUR_TOPIC_NAME_HERE"), rebalanceListener); //SET YOUR TOPIC NAME HERE 
		try {
			while (true) {				
					ConsumerRecords<String, String> records = kafkaConsumer.poll(1000);
				for (ConsumerRecord<String, String> record : records) {
					System.out.printf("Received Message topic =%s, partition =%s, offset = %d, key = %s, value = %s\n", record.topic(), record.partition(), record.offset(), record.key(), record.value());
				}
				kafkaConsumer.commitSync();				
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		kafkaConsumer.close();
	}

	private static class TestConsumerRebalanceListener implements ConsumerRebalanceListener {
		@Override
		public void onPartitionsRevoked(Collection<TopicPartition> partitions) {
			System.out.println("Called onPartitionsRevoked with partitions:" + partitions);
		}

		@Override
		public void onPartitionsAssigned(Collection<TopicPartition> partitions) {
			System.out.println("Called onPartitionsAssigned with partitions:" + partitions);
		}
	}

}
