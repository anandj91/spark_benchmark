import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.udf
import org.apache.spark.storage.StorageLevel

val spark = SparkSession.builder().getOrCreate()

val time_query = (df:org.apache.spark.sql.Dataset[org.apache.spark.sql.Row]) => { spark.time(df.write.mode("overwrite").format("noop").save()) }

val df = spark.read.parquet("parquet/store_sales")
df.createOrReplaceTempView("store_sales")

val sales_df = spark.sql("SELECT ss_item_sk AS `si`, ss_ticket_number AS `st` FROM store_sales").persist(StorageLevel.MEMORY_ONLY)
sales_df.createOrReplaceTempView("sales")

val paired_df = spark.sql("SELECT s1.si AS `si_1`, s2.si AS `si_2` FROM sales s1, sales s2 WHERE s1.st == s2.st AND s1.si < s2.si")
paired_df.createOrReplaceTempView("paired")

val count_df = spark.sql("SELECT si_1, si_2, COUNT(*) AS `count` FROM paired GROUP BY si_1, si_2")
count_df.createOrReplaceTempView("count")

val group_df = spark.sql("SELECT st, collect_set(si) AS set FROM sales GROUP BY st")
group_df.createOrReplaceTempView("group")

val make_pair = (lst :Array[Int]) => { for(x <- lst; y <- lst; if x<y) yield (x, y) }
val make_pair_udf = udf(make_pair)
spark.udf.register("make_pair", make_pair_udf)

val make_pair_df = spark.sql("SELECT make_pair(set) AS pairs FROM group")
make_pair_df.createOrReplaceTempView("make_pair")

val explode_df = spark.sql("SELECT explode(pairs) AS pair FROM make_pair").select($"pair._1", $"pair._2").toDF("si_1", "si_2")
explode_df.createOrReplaceTempView("explode")

val count2_df = spark.sql("SELECT si_1, si_2, COUNT(*) AS `count` FROM explode GROUP BY si_1, si_2")

// spark.time(count_df.write.mode("overwrite").format("noop").save())
// spark.time(count2_df.write.mode("overwrite").format("noop").save())


import com.sun.jna.Library
import com.sun.jna.Native

trait libcalc extends Library {
  def add(a: Int, b: Int): Int
}

val testDf = Seq((1,9), (2, 8), (3, 7)).toDF("A", "B")
testDf.show()
testDf.createOrReplaceTempView("testDf")

val libc = Native.load("calc", classOf[libcalc])
val add_udf = udf(libc.add(_,_))
spark.udf.register("add_udf", add_udf)

val res = spark.sql("SELECT A, B, add_udf(A, B) FROM testDf")
res.show()


class Test {
  @native def add(a: Int, b: Int): Int
}
