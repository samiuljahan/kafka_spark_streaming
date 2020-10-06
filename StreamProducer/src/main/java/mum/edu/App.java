package mum.edu;



import java.util.Properties;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

import mum.edu.model.Tweet;
import mum.edu.model.User;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class App {
	public static void main(String[] args) throws Exception {
		
		final LinkedBlockingQueue<Status> queue = new LinkedBlockingQueue<Status>(1000);
		String consumerKey = "WPKD9wHSeWpMZsiVLLJufyl6J"; //args[0].toString();
		String consumerSecret = "iMjkoHfHXjmiYDZsFLLWbVxsjQo2SNoKMRYCcpvjcqUu0vthYm"; 
		String accessToken = "40501314-uCpbBP6xOOuGZV54v2xXyyF4IACV6tECJXPFz8bYs";
		String accessTokenSecret = "B10LPtG217OFJ2wBaiVZGOmywKpUYjVC9dtyCNWH3FjsD";
		String[] keyWords = {"bangladesh"};
		String topicName = "newtweets";

		// Set twitter oAuth tokens in the configuration
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true).setOAuthConsumerKey(consumerKey).setOAuthConsumerSecret(consumerSecret)
				.setOAuthAccessToken(accessToken).setOAuthAccessTokenSecret(accessTokenSecret);

		// Create twitterstream using the configuration
		TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
		StatusListener listener = new StatusListener() {

			@Override
			public void onStatus(Status status) {
				queue.offer(status);
			}

			@Override
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
				System.out.println("Got a status deletion notice id:" + statusDeletionNotice.getStatusId());
			}

			@Override
			public void onTrackLimitationNotice(int numberOfLimitedStatuses) {
				System.out.println("Got track limitation notice:" + numberOfLimitedStatuses);
			}

			@Override
			public void onScrubGeo(long userId, long upToStatusId) {
				System.out.println("Got scrub_geo event userId:" + userId + "upToStatusId:" + upToStatusId);
			}

			@Override
			public void onStallWarning(StallWarning warning) {
				System.out.println("Got stall warning:" + warning);
			}

			@Override
			public void onException(Exception ex) {
				ex.printStackTrace();
			}
		};
		twitterStream.addListener(listener);

		
		
		
		FilterQuery query = new FilterQuery().track(keyWords).language("en");
		twitterStream.filter(query);

		

		// Add Kafka producer config settings
		Properties props = new Properties();
		props.put("metadata.broker.list", "localhost:9092");
		props.put("bootstrap.servers", "localhost:9092");
		props.put("acks", "all");
		props.put("retries", 0);
		props.put("batch.size", 16384);
		props.put("linger.ms", 1);
		props.put("buffer.memory", 33554432);

		props.put("key.serializer", "org.apache.kafka.common.serialization.LongSerializer");
		props.put("value.serializer", "org.springframework.kafka.support.serializer.JsonSerializer");//org.apache.kafka.common.serialization.StringSerializer");

		
		Producer<Long, String> producer = new KafkaProducer<Long, String>(props);
		while (true) {
			Status ret = queue.poll();

			if (ret == null) {
				Thread.sleep(100);
				
			} else {
				
					User user = new User(ret.getUser().getId(), ret.getUser().getName(), ret.getUser().getScreenName(), 
							ret.getUser().getLocation(), ret.getUser().getFollowersCount());
					Tweet tweet = new Tweet(ret.getId(), ret.getText().replaceAll("\"", "").replaceAll("\n", " "), 
							ret.getLang(), user, ret.getRetweetCount(), ret.getFavoriteCount());
					ProducerRecord<Long, String> record = new ProducerRecord<>(topicName, Long.valueOf(ret.getId()), tweet.toString());
					System.out.println("print tweet - " + tweet.toString());
					producer.send(record);
				
			}
			
			
			
		}
		
	}
}
