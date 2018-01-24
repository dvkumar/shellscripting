package ssl;

import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.Callback;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.config.SslConfigs;

import java.util.Properties;
import java.util.Random;

public class KafkaSSLProducer {

	public static void main(String[] args) {

		Properties props = new Properties();
		props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "ch3qr027055.express-scripts.com:9092,ch3qr027056.express-scripts.com:9092,ch3qr027057.express-scripts.com:9092");

		// configure the following three settings for SSL Encryption
		props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, "SSL");
		props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, "C:\\tools\\workspace_test\\Kafka\\config\\server.truststore.jks");
		props.put(SslConfigs.SSL_TRUSTSTORE_PASSWORD_CONFIG, "test1234");

		// configure the following three settings for SSL Authentication
		props.put(SslConfigs.SSL_KEYSTORE_LOCATION_CONFIG, "C:\\tools\\workspace_test\\Kafka\\config\\server.keystore.jks");
		props.put(SslConfigs.SSL_KEYSTORE_PASSWORD_CONFIG, "test1234");
		props.put(SslConfigs.SSL_KEY_PASSWORD_CONFIG, "test1234");

		props.put(ProducerConfig.ACKS_CONFIG, "all");//message durability -- 1 mean ack after writing to leader is success. value of "all" means ack after replication.
		props.put(ProducerConfig.RETRIES_CONFIG, 0);
		props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
		props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");

		Producer<String, String> producer = new KafkaProducer<String, String>(props);
		TestCallback callback = new TestCallback();

		try {
			while (true) {
				ProducerRecord<String, String> data = new ProducerRecord<String, String>("BOSE_TEST_TOPIC", "key-" + System.currentTimeMillis(), "message-" + new java.util.Date().toString());
				producer.send(data, callback);				
				Thread.sleep(1000);//send 1 msg per sec
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		producer.close();
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