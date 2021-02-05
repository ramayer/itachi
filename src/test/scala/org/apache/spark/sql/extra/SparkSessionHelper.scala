/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.spark.sql.extra

import org.apache.hadoop.hive.ql.metadata.Hive
import org.apache.hadoop.hive.ql.session.SessionState
import org.apache.itachi.ItachiFunSuite

import org.apache.spark.sql.SparkSession
import org.apache.spark.util.Utils

trait SparkSessionHelper extends ItachiFunSuite {

  protected var spark: SparkSession = _

  protected lazy val sql = spark.sql _

  override def beforeAll(): Unit = {
    val warehousePath = Utils.createTempDir()
    val metastorePath = Utils.createTempDir()
    warehousePath.delete()
    metastorePath.delete()
    spark = SparkSession.builder()
      .appName("test name")
      .master("local[3, 1]")
      .config("spark.sql.warehouse.dir", warehousePath.toString)
      .config("javax.jdo.option.ConnectionURL",
        s"jdbc:derby:;databaseName=$metastorePath;create=true")
      .getOrCreate()
    super.beforeAll()
  }

  override def afterAll(): Unit = {
    super.afterAll()
    if (spark != null) {
      spark.stop()
      spark = null
    }
    SparkSession.clearActiveSession()
    SparkSession.clearDefaultSession()
    SessionState.detachSession()
    Hive.closeCurrent()
  }
}
