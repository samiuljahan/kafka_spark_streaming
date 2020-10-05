package mum.edu;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import kafka.serializer.StringDecoder;
import mum.edu.model.Tweet;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.Optional;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.streaming.Durations;
import org.apache.spark.streaming.api.java.JavaDStream;
import org.apache.spark.streaming.api.java.JavaPairDStream;
import org.apache.spark.streaming.api.java.JavaPairInputDStream;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import org.apache.spark.streaming.kafka.KafkaUtils;

import scala.Tuple2;

import com.google.gson.Gson;




public class App 
{ 
	
	public static void main(String[] args) throws InterruptedException {
		


    // Create a Java Streaming Context with a Batch Interval of 5 seconds
    SparkConf conf = new SparkConf().setAppName("KafkaSparktweet").setMaster("local");
    JavaSparkContext sc = new JavaSparkContext(conf);
    JavaStreamingContext jssc = new JavaStreamingContext(sc, Durations.seconds(5));

    jssc.checkpoint("~/Desktop/checkpoint5");
    
    // Specify the Kafka Broker Options and set of Topics
    String broker = "localhost:9092";
    Map<String, String> kafkaParameters = new HashMap<String, String>();
    kafkaParameters.put("metadata.broker.list", broker);
    Set<String> topics = Collections.singleton("newtweets");

    // Create an input DStream using KafkaUtils and simple plain-text message processing
    JavaPairInputDStream<String, String> kafkaDirectStream = KafkaUtils.createDirectStream(jssc,
            String.class, String.class, StringDecoder.class, StringDecoder.class, kafkaParameters, topics);
    
    JavaDStream<String> lines = kafkaDirectStream.map(new Function<Tuple2<String, String>, String>() { /**
		 * 
		 */
		private static final long serialVersionUID = 1L;

	@Override public String call(Tuple2<String, String> tuple2) { return tuple2._2(); } });

    
    JavaPairDStream<String,Integer> counts = 
    	lines.mapToPair(
        		record -> {
        			
        	        Gson gson = new Gson(); 
        	        String recordStr = record.toString().replaceAll("\\\\", "").trim();
        	        String recordStrProper = recordStr.substring(1, recordStr.length() - 1).trim();
        	        Tweet tweet = gson.fromJson(recordStrProper, Tweet.class);
        	        String location = tweet.getUser().getLocation().trim();
        	        
        	        String[] locations = location.split(",");
        	       
        	        String locatinName = locations[locations.length-1].trim();
        	        if (locatinName != null && (!locatinName.equals("null")) && (!locatinName.equals(""))) {
        	        	return new Tuple2<String, Integer>(locatinName, 1);
					}
        	       
					return new Tuple2<String, Integer>("0", 1);
					
        		}
        		
        		)
        		.filter(x -> (!x._1.equals("0")))
        		.reduceByKey((x, y) -> x + y);

    

    
    
    // Function to maintain state
    Function2<List<Integer>, Optional<Integer>, Optional<Integer>> updateFunction =
            new Function2<List<Integer>, Optional<Integer>, Optional<Integer>>() {
                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public Optional<Integer> call(List<Integer> values, Optional<Integer> state) {
                    Integer newSum = state.or(0);
                    for(int i : values)
                    {
                        newSum += i;
                    }
                    return Optional.ofNullable(newSum);
                }
            };
            
    // Maintain state

    JavaPairDStream<String, Integer> cumulativeLocationCounts = counts.updateStateByKey(updateFunction);
            
    
    System.out.println("Printing start ---- ");
    cumulativeLocationCounts.print();
    
    System.out.println("Printing end ---- ");
   
    jssc.start();
    
    

    // Wait for the computation to terminate
    jssc.awaitTermination();
    
    
}





}
