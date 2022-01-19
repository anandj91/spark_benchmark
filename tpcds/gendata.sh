rm -rf metastore_ddb
rm derby.log

../spark-3.2.0-bin-hadoop3.2/bin/spark-shell \
	--jars ../spark-sql-perf/target/scala-2.12/spark-sql-perf_2.12-0.5.1-SNAPSHOT.jar \
      	--master spark://localhost:7077 \
       	--deploy-mode client \
	-i ./GenTPCDSData.scala
