package spark.batch.tp21;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.Tuple2;
import java.util.Arrays;

public class WordCountTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(WordCountTask.class);

    public static void main(String[] args) {
        // التحقق من المدخلات (الملف ومسار الخرج)
        if (args.length < 2) {
            System.err.println("Usage: WordCountTask <input-file> <output-dir>");
            System.exit(1);
        }
        new WordCountTask().run(args[0], args[1]);
    }

    public void run(String inputFilePath, String outputDir) {
        // إعداد Spark Configuration
        // نستخدم setMaster("local") للتجربة المحلية، لكن عند الإرسال للكلاستر يمكن إزالتها أو تجاوزها
        SparkConf conf = new SparkConf()
                .setAppName(WordCountTask.class.getName());
                // .setMaster("local[*]"); // سنحدد الماستر عند التشغيل عبر spark-submit

        JavaSparkContext sc = new JavaSparkContext(conf);

        // 1. قراءة الملف
        JavaRDD<String> textFile = sc.textFile(inputFilePath);

        // 2. تقسيم الكلمات وحساب التكرار
        JavaPairRDD<String, Integer> counts = textFile
                .flatMap(s -> Arrays.asList(s.split("\\s+")).iterator())
                .mapToPair(word -> new Tuple2<>(word, 1))
                .reduceByKey((a, b) -> a + b);

        // 3. حفظ النتيجة
        counts.saveAsTextFile(outputDir);
        
        sc.close();
    }
}