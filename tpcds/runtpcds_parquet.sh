$SPARK_HOME/bin/spark-shell \
       	    --jars ../spark-sql-perf/target/scala-2.12/spark-sql-perf_2.12-0.5.1-SNAPSHOT.jar \
            --master yarn \
            --deploy-mode client \
	    --executor-memory 10G \
	    --num-executors 8 \
 	    --executor-cores 4 \
            -i ./TPCDS_99_parquet_Queries.scala
