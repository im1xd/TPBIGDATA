# TP09: Batch & Streaming Processing with Apache Spark

This project demonstrates Big Data processing using **Apache Spark** (Batch & Streaming) within a **Dockerized Hadoop Ecosystem**. It was implemented as part of the Big Data module.

## 📌 Overview
We implemented three core applications using **Java**:
1.  [cite_start]**Batch Processing:** A classic WordCount application processing static data[cite: 242].
2.  [cite_start]**Stream Processing:** A real-time WordCount application using Netcat and Spark Streaming[cite: 450].
3.  **Data Analytics (Bonus):** Analyzing restaurant data to determine cuisine popularity using `restaurants.csv`.

## 🛠️ Technologies & Tools
* [cite_start]**Apache Spark 3.0.0** (Core & Streaming) [cite: 15]
* [cite_start]**Hadoop HDFS** (Simulated environment) [cite: 14]
* **Java 8** (OpenJDK)
* **Maven** (Build automation)
* [cite_start]**Docker & Docker Compose** (Containerization) [cite: 16]
* [cite_start]**Netcat** (Data stream simulation) 

---

## 🚀 Environment Setup
The cluster consists of one Master node and two Worker nodes managed via Docker Compose.

### 1. Start the Cluster
```bash
docker-compose up -d
docker exec -it hadoop-master bash
2. Install Dependencies (Inside Container)
We used Alpine Linux containers, so we installed JDK 8, Maven, and Netcat inside the master node:

Bash

apk add --no-cache openjdk8 maven netcat-openbsd
export JAVA_HOME=/usr/lib/jvm/java-1.8-openjdk
export PATH=$PATH:$JAVA_HOME/bin
📂 Part 1: Batch Processing (WordCount)

Goal: Count the frequency of words in a static text file using Spark RDDs.

Input: file1.txt (Stored in the container/HDFS).

Code: src/main/java/spark/batch/tp21/WordCountTask.java.


Logic: Uses flatMap to split lines, mapToPair to create tuples, and reduceByKey to aggregate counts.

Execution Command:

Bash

spark-submit --class spark.batch.tp21.WordCountTask --master local target/stream-spark-1.0-SNAPSHOT.jar /file1.txt /output-java
📡 Part 2: Real-Time Streaming

Goal: Count words in real-time from a data stream on port 9999 with a 5-second window.


Tool: Netcat (nc) acts as the data sender.

Code: src/main/java/spark/streaming/tp22/Stream.java.


Logic: Listens to localhost:9999, creates micro-batches, and updates counts.

Execution:

Terminal 1 (Sender):

Bash

nc -lk 9999
Terminal 2 (Spark App):

Bash

/spark/bin/spark-submit --class spark.streaming.tp22.Stream --master local[*] target/stream-spark-1.0-SNAPSHOT.jar
📊 Part 3: Restaurant Analytics (Bonus)
Goal: Analyze a real-world dataset (restaurants.csv) to find the distribution of cuisines.


Input: restaurants.csv (Columns: ID, Name, Borough, ..., Cuisine).

Code: src/main/java/spark/batch/restaurant/RestaurantApp.java.

Logic: Extracts the "Cuisine" column (index 7), counts occurrences, and sorts them by popularity.

Execution Command:

Bash

spark-submit --class spark.batch.restaurant.RestaurantApp --master local target/stream-spark-1.0-SNAPSHOT.jar
Result Example:

Plaintext

(1,American)
(1,French)
(1,Italian)
(1,Delicatessen)
👤 Author
Imad Eddine Abid (im1xd) Student at Faculty of Computers and Artificial Intelligence,
