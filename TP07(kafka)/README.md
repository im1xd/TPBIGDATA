# TP07 – Apache Kafka 4.1.1 على Windows + مشروع IoT

هذا الملف يشرح جميع الخطوات التي قمنا بها في الـ TP:

- تثبيت **Java 17** و **Kafka 4.1.1** على Windows عبر الـ terminal فقط  
- تهيئة Kafka بوضع **KRaft** (بدون ZooKeeper)  
- إنشاء topics واختبار **Producer / Consumer** من الكونسول  
- مشروع **IoT** بسيط لتتبع آلات chantier باستعمال Python و Kafka  

> ملاحظة: اللوج التالي  
> `Reconfiguration failed: No configuration found for '5e2de80c' at 'null' in 'null'`  
> هو تحذير من Log4j وليس خطأ، يمكن تجاهله ما دام Kafka يعمل بشكل طبيعي.

---

## 1. المتطلبات

- نظام التشغيل: Windows 10 / 11  
- اتصال إنترنت  
- Python 3 مثبت (لجزء الـ IoT)  

---

## 2. تثبيت Java 17 عبر الـ CMD

### 2.1 إنشاء مجلد لـ JDK وتحميله

```cmd
C:\>mkdir C:\java
C:\>cd C:\java

C:\java>curl -L "https://github.com/adoptium/temurin17-binaries/releases/download/jdk-17.0.17%2B10/OpenJDK17U-jdk_x64_windows_hotspot_17.0.17_10.zip" -o openjdk17.zip
2.2 فك الضغط عن ملف الـ ZIP
C:\java>tar -xvf openjdk17.zip
C:\java>dir
2.3 ضبط JAVA_HOME و PATH (لكل نافذة CMD نحتاج فيها Java 17)
C:\java>set JAVA_HOME=C:\java\jdk-17.0.17+10
C:\java>set PATH=%JAVA_HOME%\bin;%PATH%

C:\java>java -version
3. تنزيل Kafka 4.1.1 على Windows
3.1 إنشاء مجلد Kafka وتحميل الملف
C:\>mkdir C:\kafka
C:\>cd C:\kafka

C:\kafka>curl -L "https://downloads.apache.org/kafka/4.1.1/kafka_2.13-4.1.1.tgz" -o kafka_2.13-4.1.1.tgz

3.2 فك الضغط
C:\kafka>tar -xvf kafka_2.13-4.1.1.tgz
C:\kafka>cd kafka_2.13-4.1.1

4. تهيئة Kafka بوضع KRaft
4.1 تعديل ملف الإعدادات server.properties

نفتح الملف بـ Notepad:

C:\kafka\kafka_2.13-4.1.1>notepad config\server.properties


نعدّل/نضيف (أو نتأكد من وجود) الأسطر التالية:

process.roles=broker,controller
node.id=1

listeners=PLAINTEXT://:9092,CONTROLLER://:9093
advertised.listeners=PLAINTEXT://localhost:9092,CONTROLLER://localhost:9093
controller.listener.names=CONTROLLER
controller.quorum.voters=1@localhost:9093

log.dirs=C:/kafka/kraft-combined-logs


log.dirs هو مكان تخزين بيانات Kafka (سيُنشأ تلقائيًا إن لم يكن موجودًا).
4.2 توليد cluster.id

نتأكد أولاً أننا في نافذة CMD فيها Java 17:

C:\kafka\kafka_2.13-4.1.1>set JAVA_HOME=C:\java\jdk-17.0.17+10
C:\kafka\kafka_2.13-4.1.1>set PATH=%JAVA_HOME%\bin;%PATH%
C:\kafka\kafka_2.13-4.1.1>java -version   (يجب أن تكون 17)


ثم:

C:\kafka\kafka_2.13-4.1.1>bin\windows\kafka-storage.bat random-uuid


ستحصل على قيمة مثل:

t5Ph23mWRDyxYBzoEV3O0w


هذه هي قيمة cluster.id.

4.3 تهيئة التخزين (format)
C:\kafka\kafka_2.13-4.1.1>bin\windows\kafka-storage.bat format -t t5Ph23mWRDyxYBzoEV3O0w -c config\server.prope
4.4 تشغيل Kafka Server
C:\kafka\kafka_2.13-4.1.1>bin\windows\kafka-server-start.bat config\server.properties


دع هذه النافذة مفتوحة؛ هي التي تشغّل الـ broker.
في الـ logs سنرى في النهاية سطرًا شبيهًا بـ:

[KafkaRaftServer nodeId=1] Kafka Server started

5. إنشاء Topic واختبار Producer / Consumer (Kafka CLI)

في كل نافذة جديدة نفتحها لـ Kafka CLI، يجب أولًا ضبط Java 17:

set JAVA_HOME=C:\java\jdk-17.0.17+10
set PATH=%JAVA_HOME%\bin;%PATH%
cd C:\kafka\kafka_2.13-4.1.1

5.1 إنشاء topic تجريبي test-topic
bin\windows\kafka-topics.bat --create --topic test-topic --bootstrap-server localhost:9092

bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092


النتيجة:

test-topic

5.2 Producer من الكونسول
bin\windows\kafka-console-producer.bat --topic test-topic --bootstrap-server localhost:9092


ثم نكتب رسائل:

hello kafka
message 1
message 2

5.3 Consumer من الكونسول
bin\windows\kafka-console-consumer.bat --topic test-topic --from-beginning --bootstrap-server localhost:9092


ستظهر الرسائل:

hello kafka
message 1
message 2
Processed a total of 3 messages

6. Topic لمشروع الـ IoT

ننشئ topic جديد مثلاً باسم machines:

bin\windows\kafka-topics.bat --create --topic machines --bootstrap-server localhost:9092

bin\windows\kafka-topics.bat --list --bootstrap-server localhost:9092

7. إعداد بيئة Python
7.1 التأكد من وجود Python
python --version

7.2 تثبيت مكتبة Kafka لـ Python
pip install kafka-python

8. كود مشروع IoT (Producer / Consumer)
8.1 Producer – ملف iot_producer.py

يرسل قراءات عشوائية لآلات chantier (درجة الحرارة + الاهتزاز):

from kafka import KafkaProducer
import json
import random
from time import sleep
from datetime import datetime

# إعداد الـ Producer
producer = KafkaProducer(
    bootstrap_servers='localhost:9092',
    value_serializer=lambda v: json.dumps(v).encode('utf-8')
)

MACHINES = ["EXCAVATOR-1", "CRANE-2", "BULLDOZER-3"]

print("Starting IoT producer... (Ctrl+C to stop)")
while True:
    for m in MACHINES:
        temp = random.uniform(60, 100)   # درجة الحرارة
        vib = random.uniform(0.0, 1.0)   # الاهتزاز
        status = "OK"
        if temp > 85 or vib > 0.8:
            status = "ALERT"

        msg = {
            "machine_id": m,
            "temperature": round(temp, 2),
            "vibration": round(vib, 2),
            "status": status,
            "timestamp": datetime.now().isoformat(timespec='seconds')
        }

        producer.send('machines', msg)
        print("sent:", msg)

    producer.flush()
    sleep(2)


تشغيله:

cd <مسار_الملف>
python iot_producer.py

8.2 Consumer – ملف iot_consumer.py

يقرأ من topic machines ويطبع تنبيه في حالة الخطر:

from kafka import KafkaConsumer
import json

consumer = KafkaConsumer(
    'machines',
    bootstrap_servers='localhost:9092',
    auto_offset_reset='earliest',   # قراءة من البداية
    value_deserializer=lambda v: json.loads(v.decode('utf-8'))
)

print("Monitoring machines... (Ctrl+C to stop)")
for msg in consumer:
    data = msg.value
    print("READ:", data)

    if data["status"] == "ALERT":
        print(">>> !!! MACHINE ALERT !!! <<<")
        print(f"Machine {data['machine_id']} in problem state:")
        print(f"  Temp = {data['temperature']} C, Vib = {data['vibration']}")
        print("-" * 40)


تشغيله:

cd <مسار_الملف>
python iot_consumer.py

8.3 مثال لخرج الـ Consumer (من صور الشاشة)
READ: {'machine_id': 'BULLDOZER-3', 'temperature': 70.57, 'vibration': 0.97, 'status': 'ALERT', 'timestamp': '2025-11-26T20:26:08'}
>>> !!! MACHINE ALERT !!! <<<
Machine BULLDOZER-3 in problem state:
  Temp = 70.57 C, Vib = 0.97
----------------------------------------
READ: {'machine_id': 'CRANE-2', 'temperature': 96.74, 'vibration': 0.62, 'status': 'ALERT', 'timestamp': '2025-11-26T20:26:10'}
>>> !!! MACHINE ALERT !!! <<<
Machine CRANE-2 in problem state:
  Temp = 96.74 C, Vib = 0.62
----------------------------------------
...

9. الأخطاء التي واجهناها وحلّها
9.1 خطأ UnsupportedClassVersionError
has been compiled by a more recent version of the Java Runtime (class file version 61.0), this version ... up to 52.0


السبب: تشغيل Kafka بأداة Java 8.
الحل:

تثبيت Java 17.

في كل نافذة CMD نستخدم فيها Kafka:

set JAVA_HOME=C:\java\jdk-17.0.17+10
set PATH=%JAVA_HOME%\bin;%PATH%
java -version   (تكون 17)

9.2 خطأ Because controller.quorum.voters is not set

ظهر عند تشغيل:

kafka-storage.bat format ...


الحل: إضافة السطر التالي في config/server.properties:

controller.quorum.voters=1@localhost:9093


ثم إعادة أمر الـ format.

9.3 خطأ No readable meta.properties files found

ظهر عند تشغيل kafka-server-start.bat بدون أن يكون تمّ الـ format بنجاح.
الحل: تنفيذ أمر الـ format:

bin\windows\kafka-storage.bat format -t <cluster-id> -c config\server.properties


ثم إعادة تشغيل Kafka.

9.4 تحذير Reconfiguration failed: No configuration found for '5e2de80c'

هذا تحذير من Log4j 2 (dynamic reconfiguration)، يمكن تجاهله لأن Kafka يعمل بشكل عادي.
