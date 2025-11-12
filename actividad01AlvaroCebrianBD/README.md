# 1️⃣ Compilar y ejecutar manualmente

javac -cp ".:lib/mysql-connector-j-9.4.0.jar" $(find src -name "\*.java")
java -cp ".:lib/mysql-connector-j-9.4.0.jar:src" App
