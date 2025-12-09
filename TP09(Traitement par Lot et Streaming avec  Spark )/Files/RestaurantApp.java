package spark.batch.restaurant;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

public class RestaurantApp {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf().setAppName("RestaurantAnalytics").setMaster("local");
        JavaSparkContext sc = new JavaSparkContext(conf);

        // قراءة الملف من الجذر
        JavaRDD<String> lines = sc.textFile("/restaurants.csv");

        JavaPairRDD<String, Integer> cuisineCounts = lines
            .mapToPair(line -> {
                String[] parts = line.split(";"); 
                String cuisine = parts[7];       
                return new Tuple2<>(cuisine, 1);
            })
            .reduceByKey((a, b) -> a + b);       

        JavaPairRDD<Integer, String> sortedCuisines = cuisineCounts
            .mapToPair(item -> new Tuple2<>(item._2(), item._1())) 
            .sortByKey(false); 

        sortedCuisines.saveAsTextFile("/output-restaurants");
        sc.close();
    }
}