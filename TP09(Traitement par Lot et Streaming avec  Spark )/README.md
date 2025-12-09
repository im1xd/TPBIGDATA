# TP09: Batch & Streaming Processing with Apache Spark

This project demonstrates Big Data processing using **Apache Spark** (Batch & Streaming) within a **Dockerized Hadoop Ecosystem**. It was implemented as part of the Big Data module.

## 📌 Overview
We implemented three core applications using **Java**:
1.  **Batch Processing:** A classic WordCount application.
2.  **Stream Processing:** A real-time WordCount using Netcat and Spark Streaming.
3.  **Data Analytics (Bonus):** Analyzing restaurant data to determine cuisine popularity using `restaurants.csv`.

## 🛠️ Technologies & Tools
* **Apache Spark 3.0.0** (Core & Streaming)
* **Hadoop HDFS** (Simulated environment)
* **Java 8** (OpenJDK)
* **Maven** (Build automation)
* **Docker & Docker Compose** (Containerization)
* **Netcat** (Data stream simulation)

---

## 🚀 Environment Setup
The cluster consists of one Master node and two Worker nodes managed via Docker Compose.

### 1. Start the Cluster
```bash
docker-compose up -d
docker exec -it hadoop-master bash
2. Install Dependencies (Inside Container)
We used Alpine Linux, so we installed JDK 8 and Maven inside the container:

Bash

apk add --no-cache openjdk8 maven netcat-openbsd
export JAVA_HOME=/usr/lib/jvm/java-1.8-openjdk
export PATH=$PATH:$JAVA_HOME/bin
📂 Project 1: Batch Processing (WordCount)
Goal: Count the frequency of words in a static text file.

Input: file1.txt

Code: src/main/java/spark/batch/tp21/WordCountTask.java

Logic: flatMap -> mapToPair -> reduceByKey.

Execution:

Bash

spark-submit --class spark.batch.tp21.WordCountTask --master local target/wordcount-spark-1.0-SNAPSHOT.jar /file1.txt /output-java
📡 Project 2: Real-Time Streaming
Goal: Count words in real-time from a data stream on port 9999 with a 5-second window.

Tool: Netcat (nc) acts as the data sender.

Code: src/main/java/spark/streaming/tp22/Stream.java

Logic: Listens to localhost:9999, creates micro-batches, and updates counts.

Execution:

Terminal 1 (Sender): nc -lk 9999

Terminal 2 (Spark App):

Bash

/spark/bin/spark-submit --class spark.streaming.tp22.Stream --master local[*] target/stream-spark-1.0-SNAPSHOT.jar
📊 Project 3: Restaurant Analytics (CSV Analysis)
Goal: Analyze a dataset of restaurants to find the distribution of cuisines.

Input: restaurants.csv (Columns: ID, Name, Borough, ..., Cuisine)

Code: src/main/java/spark/batch/restaurant/RestaurantApp.java

Logic: Extracts the "Cuisine" column, counts occurrences, and sorts them.

Execution:

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
