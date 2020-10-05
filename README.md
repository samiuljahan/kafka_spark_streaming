# BDT Project - kafka_spark_streaming

The aim of the project is to create a Spark Streaming project that will pull twitter data with keyword - Bangladesh and store them in a Kafka Topic.
From th =e Kafka Topic and Consumer Program will pull the data in realtime, use Spark Streaming and generate counts of locations. That means I would like to see from which location, state or country people are mostly talking about Bangladesh. It's obvious that people from bangladesh will mostly talk about Bangladesh, but I sometimes I saw people from India or Myanmar are talking about Bangladesh. May be about Cricket or Rhingya refugee crisis. This analysis is simple, mainly counting the locations.

# List of commands used in the project

At first I had to install kafka on the Couldera quickstart VM. Using the following commands - 
```
d /etc/yum.repos.d 
wget http://archive.cloudera.com/kafka/red...
sudo yum clean all
sudo yum install kafka
sudo yum install kafka-server
sudo service kafka-server start 
```
It started Kafka
Then used the following command to see the services running. And it showed kafka

```
sudo jps
```
Create topic
```
bin/kafka-topics.sh --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic newtweets
```
List topics
```
bin/kafka-topics.sh --list --zookeeper localhost:2181
```

Start comsumer to see the tweets from the begining
```
bin/kafka-console-consumer.sh --zookeeper localhost:2181 -topic newtweets --from-beginning
```
After that, I ran the producer program using the following command
```
java -jar stream_producer.jar
```
It sent the tweets to topic and printed on the console. I was able to check from the consumer shell that I ran before
to see the tweets were coming.

Finally, I ran the producer program using the following command

```
spark-submit --class "mum.edu.App" --master yarn  Desktop/stream_consumer.jar
```
as well as tried the following command  

```
java -jar stream_consumer.jar
```

## Links to the Jar files of both the consuemr and producer

To  find the Jar file of producer, visit

https://drive.google.com/drive/folders/1OiWl3XmfNSUmmJvIuSyoU4BdcG2_53Gd?usp=sharing


To  find the Jar file of consumer, visit

https://drive.google.com/drive/folders/1OwT44WKEZ5mDoGuJj4oGlCQvLgR-YM33?usp=sharing


## Link to the project video

To watch the project video please click 

https://web.microsoftstream.com/video/f1f6ed6d-e487-452f-9e1d-7d0c0be91ad3


### Limitations

Unfortunately, I was not able to store the streaming results into Hive

### Screenshots 

* Twitter Consumer running. Pulling tweets, printing them and sending to Kafka topic

<img width="831" alt="prodcuer" src="https://user-images.githubusercontent.com/7520167/95054947-39e52600-06b8-11eb-89be-76948abc05c7.PNG">

* Twitter Producer with Spark Streaming running. Pulling tweets from the Kafka topic, using Spark Streaming and Generating counts for locations

<img width="421" alt="consumer" src="https://user-images.githubusercontent.com/7520167/95055267-a8c27f00-06b8-11eb-9007-447d627e18c7.PNG">
