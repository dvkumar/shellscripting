package ssl;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Properties;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRebalanceListener;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.errors.WakeupException;

public class KafkaSSLConsumer {
	static KafkaConsumer<String, String> kafkaConsumer = null;

	public static void main(String[] args) {

		Properties props = new Properties();
		props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "ch3qr027057.express-scripts.com:9092,ch3qr027056.express-scripts.com:9092,ch3qr027057.express-scripts.com:9092");

		// configure the following three settings for SSL Encryption
		props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
		props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "C:\\tools\\workspace_test\\Kafka\\config\\server.truststore.jks");
		props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "test1234");

		// configure the following three settings for SSL Authentication
		props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "C:\\tools\\workspace_test\\Kafka\\config\\server.keystore.jks");
		props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "test1234");
		props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "test1234");

		props.put(ConsumerConfig.GROUP_ID_CONFIG, "my-group");
		props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
		props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");
		props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringDeserializer");

		kafkaConsumer = new KafkaConsumer<String, String>(props);
		kafkaConsumer.subscribe(Arrays.asList("BOSE_TEST_TOPIC"));
		try {
			while (true) {
				System.out.println(new Date().toString());
					ConsumerRecords<String, String> records = kafkaConsumer.poll(10000);
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
