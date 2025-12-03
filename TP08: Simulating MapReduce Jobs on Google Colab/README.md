# TP08 – Simulating MapReduce Jobs on Google Colab

> Practical work for the **Big Data** course (Master 2 I2A, University of El Oued, 2025/2026).  
> Goal: understand the **map → group/shuffle → reduce** pattern by simulating MapReduce jobs in pure Python on Google Colab. :contentReference[oaicite:0]{index=0}  

---

## 📂 Repository Contents

This repository contains three small MapReduce-style experiments, each implemented in Python / Google Colab:

- **Word Count** – introductory example.
- **Sales per Region Analysis** – applied example on CSV-like data.
- **Web Log Analysis** – main exercise + bonus explorations. :contentReference[oaicite:1]{index=1}  

Suggested structure (adjust filenames if needed):

- `wordcount.ipynb` – Word Count notebook  
- `sales_mapreduce.ipynb` – Sales per Region notebook  
- `log_analysis_mapreduce.ipynb` – Web Log Analysis notebook  

Data files (created inside the notebooks):

- `data.txt` – input text for the Word Count example  
- `sales.txt` – product sales dataset  
- `weblogs.txt` – HTTP log dataset  

---

## 🧠 Concept: Map → Group/Shuffle → Reduce

All three examples follow the same pattern:

1. **Map**  
   - Read each line of the input file.  
   - Parse it and emit key–value pairs like `(key, value)`.

2. **Group / Shuffle**  
   - Group all values sharing the same key (conceptually like the shuffle phase in Hadoop).

3. **Reduce**  
   - Aggregate the grouped values (e.g. `sum`, `count`) to compute the final result.

This simulates how distributed frameworks like **Hadoop MapReduce** or **Spark** process large datasets, but here everything runs locally in Python on a single machine / Colab runtime. :contentReference[oaicite:2]{index=2}  

---

## 📘 Example
Create a virtual environment and install Jupyter if needed.

Launch Jupyter:

jupyter notebook


Open the notebooks and run them cell by cell.
