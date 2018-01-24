package com.esi.kafka.kerberos;

import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;

public class KafkaKeytabProducer {
	static KafkaProducer<String, String> producer = null;
	static TestCallback callback = null;

	public static void main(String[] args) {
		System.out.println("start============================================");
		Properties properties = new Properties();

		// -Djava.security.auth.login.config=C:\\tools\\workspace_kafka\\Kafka\\config\\kafkasvcpoc1_jaas.conf
		// -Djava.security.krb5.conf=C:\\tools\\workspace_kafka\\Kafka\\config\\krb5.conf
		// -Djavax.security.auth.useSubjectCredsOnly=false
		// -Dsun.security.krb5.debug=true
		// -Dlog4j.configuration=file:\\\C:\\tools\\workspace_kafka\\Kafka\\config\\log4j.properties

		// System.setProperty("java.security.auth.login.config","C:\\tools\\workspace_test\\Kafka\\config\\kafkasvcpoc1_jaas.conf");
		// System.setProperty("java.security.krb5.conf", "C:\\tools\\workspace_test\\Kafka\\config\\krb5.conf");
		// System.setProperty("javax.security.auth.useSubjectCredsOnly", "false");
		// System.setProperty("sun.security.krb5.debug", "true");

		// ## HDP DEV - bootstrap.server = ch3dr028353:6667,ch3dr028354:6667,ch3dr028355:6667
		properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "ch3dr028353.express-scripts.com:6667,ch3dr028354.express-scripts.com:6667,ch3dr028355.express-scripts.com:6667");
		properties.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer"); // value Serializer
		properties.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer"); // key Serializer
		properties.put(ProducerConfig.ACKS_CONFIG, "1"); // message durability -- 1 mean ack after writing to leader is success. value of "all" means ack after replication.
		properties.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SASL_PLAINTEXT"); // Security protocol to use for communication.
		properties.put("sasl.mechanism", "GSSAPI");
		properties.put("sasl.kerberos.service.name", "kafka");
		properties.put("batch.size", "16384");// maximum size of message
		properties.put(ProducerConfig.RETRIES_CONFIG, 0);

		System.out.println("loaded============================================");

		try {
			producer = new KafkaProducer<String, String>(properties);
			System.out.println(producer.partitionsFor("SET_YOUR_TOPIC_NAME_HERE").toString());
			callback = new TestCallback();
		} catch (Exception e) {
			System.out.println("===================EXCEPTION=============");
			e.printStackTrace();
			System.out.println("===================EXCEPTION=============");
		}

		try {
			while (true) {
				ProducerRecord<String, String> data = new ProducerRecord<String, String>("SET_YOUR_TOPIC_NAME_HERE", "key-" + System.currentTimeMillis(), "message-" + new java.util.Date().toString());
				producer.send(data, callback);
				System.out.println(".");
				Thread.sleep(1000);// send 1 msg per sec
			}
		} catch (Exception e) {
			System.out.println("===================EXCEPTION1=============");
			e.printStackTrace();
			System.out.println("===================EXCEPTION1=============");
		}

		producer.close();

		System.out.println("END============================================");
	}

	private static class TestCallback implements Callback {
		@Override
		public void onCompletion(RecordMetadata recordMetadata, Exception e) {
			if (e != null) {
				System.out.println("Error while producing message to topic :" + recordMetadata);
				e.printStackTrace();
			} else {
				String message = String.format("sent message to topic:%s partition:%s  offset:%s", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
				System.out.println(message);
			}
		}
	}

}