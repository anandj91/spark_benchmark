$SPARK_HOME/bin/spark-shell \
            --jars ./jna-5.6.0.jar \
            --master yarn \
            --deploy-mode client \
            --executor-memory 10G \
            --num-executors 8 \
            --executor-cores 4 \
	    --files libcalc.so \
            -i ./test.scala

