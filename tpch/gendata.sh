rm -rf metastore_ddb
rm derby.log

../spark3/bin/spark-shell \
	--jars ../spark-sql-perf/target/scala-2.12/spark-sql-perf_2.12-0.5.1-SNAPSHOT.jar \
      	--master local[*] \
       	--deploy-mode client \
	--executor-memory 4G \
	--num-executors 4 \
	--executor-cores 2 \
	-i ./GenTPCHData.scala
