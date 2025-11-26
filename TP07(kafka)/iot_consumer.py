from kafka import KafkaConsumer
import json

consumer = KafkaConsumer(
    'machines',
    bootstrap_servers='localhost:9092',
    auto_offset_reset='earliest',   # يقرأ من البداية
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
