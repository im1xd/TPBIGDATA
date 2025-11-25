# TP6 – Bases de données NoSQL (Cassandra)
Ce document décrit toutes les étapes exécutées dans le terminal (CMD) pour installer Cassandra via Docker, créer le keyspace, importer les données, et exécuter les requêtes CQL du TP.

---

## 🚀 1. Lancement de Cassandra via Docker

```bash
docker run --name mon-cassandra -d -p 9042:9042 cassandra:4.1
docker ps
docker logs --tail 40 mon-cassandra
docker exec -it mon-cassandra cqlsh
CREATE KEYSPACE IF NOT EXISTS resto_NY
WITH REPLICATION = {
  'class' : 'SimpleStrategy',
  'replication_factor': 1
};
USE resto_NY;
CREATE TABLE Restaurant (
    id INT,
    name VARCHAR,
    borough VARCHAR,
    buildingnum VARCHAR,
    street VARCHAR,
    zipcode INT,
    phone TEXT,
    cuisinetype VARCHAR,
    PRIMARY KEY (id)
);
CREATE INDEX fk_Restaurant_cuisine ON Restaurant (cuisinetype);
CREATE TABLE Inspection (
    idrestaurant INT,
    inspectiondate DATE,
    violationcode VARCHAR,
    violationdescription VARCHAR,
    criticalflag VARCHAR,
    score INT,
    grade VARCHAR,
    PRIMARY KEY (idrestaurant, inspectiondate)
);
CREATE INDEX fk_Inspection_Restaurant ON Inspection (grade);
docker cp restaurants.csv mon-cassandra:/
docker cp restaurants_inspections.csv mon-cassandra:/
COPY Restaurant (id, name, borough, buildingnum, street, zipcode, phone, cuisinetype)
FROM '/restaurants.csv' WITH DELIMITER=',';

COPY Inspection (idrestaurant, inspectiondate, violationcode, violationdescription, criticalflag, score, grade)
FROM '/restaurants_inspections.csv' WITH DELIMITER=',';
SELECT count(*) FROM Restaurant;
SELECT count(*) FROM Inspection;
SELECT * FROM Restaurant;
SELECT name FROM Restaurant;
SELECT name, borough
FROM Restaurant
WHERE id = 41569764;
SELECT inspectiondate, grade
FROM Inspection
WHERE idrestaurant = 41569764;
SELECT name
FROM Restaurant
WHERE cuisinetype = 'French';
CREATE INDEX restaurant_borough_idx ON Restaurant (borough);
SELECT name FROM Restaurant WHERE borough = 'BROOKLYN';
SELECT name
FROM Restaurant
WHERE borough = 'BROOKLYN'
ALLOW FILTERING;
SELECT grade, score
FROM Inspection
WHERE idrestaurant = 41569764
  AND score >= 10
ALLOW FILTERING;
SELECT grade, score
FROM Inspection
WHERE score > 30
  AND grade IN ('A', 'B', 'C', 'D', 'Z', 'P', 'Not Yet Graded')
ALLOW FILTERING;
SELECT count(*)
FROM Inspection
WHERE score > 30
  AND grade IN ('A', 'B', 'C', 'D', 'Z', 'P', 'Not Yet Graded')
ALLOW FILTERING;
1145

