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
