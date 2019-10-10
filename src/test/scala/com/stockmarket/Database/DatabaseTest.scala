package com.stockmarket.Database

import com.stockmarket.db.{DatabaseHandler, Users}
import com.stockmarket.messages.JsonMapper
import org.scalatest._
import org.scalatest.concurrent.ScalaFutures
import org.scalatest.time.{Seconds, Span}
import slick.driver.H2Driver.api._
import slick.jdbc.meta._

class DatabaseTest extends FunSuite with BeforeAndAfter with ScalaFutures {
  implicit override val patienceConfig = PatienceConfig(timeout = Span(5, Seconds))

  // Testing H2 In Memory Database

  val users = TableQuery[Users]

  var db: Database = _

  def createSchema() =
    db.run((users.schema).create).futureValue

  def insertSupplier(): Int =
    db.run(users += ("TUser", "Tpass","TFname")).futureValue

  before { db = Database.forConfig("h2mem1") }

  test("Creating the Schema works") {
    createSchema()

    val tables = db.run(MTable.getTables).futureValue

    assert(tables.size == 1)
    assert(tables.count(_.name.name.equalsIgnoreCase("users")) == 1)
//    assert(tables.count(_.name.name.equalsIgnoreCase("coffees")) == 1)
  }

  test("Inserting a Users works") {
    createSchema()

    val insertCount = insertSupplier()
    assert(insertCount == 1)
  }

  test("Query Users works") {
    createSchema()
    insertSupplier()
    val results = db.run(users.result).futureValue
    println(results)
    println(results.head._1)
    assert(results.size == 1)
    assert(results.head._1 == "TUser")
    assert(results.head._2 == "Tpass")
  }

  test("Test My DB Functions"){

    var result1 = DatabaseHandler.InsertUser("adam","eden","adam west")
    var result2 = DatabaseHandler.InsertUser("eve","apple","eve north")
    //  var result2 = handler.InsertUser("sands2","yoyo")

//    println(result1)

    println(result2)


    var res = DatabaseHandler.getFullNameByUserName("adam")

    println(res)
//    var result3 = DatabaseHandler.getUsers
//    println(result3)
//
//
//    var result4 = DatabaseHandler.ValidateUser("adam","eden")
//    println(result4)
//
//    var result5 = DatabaseHandler.ValidateUser("adam","hell")
//    println(result5)
//
//    assert(result1 == true)
//    assert(result2 == true)
//    assert(result3.size == 2)
//    assert(result4 == JsonMapper.getUserResponse("0x7000","adam west","adam"))
//    assert(result5 == JsonMapper.getStatusResponse("0x8000"))
//
//    var result6 = DatabaseHandler.InsertUser("adam","eden","adam west")
//    println(result6)
//    assert(result6 == false)
//
//    var result7 = DatabaseHandler.UserExists("adam")
//    assert(result7 == true)
//
//    var result8 = DatabaseHandler.UserExists("god")
//    assert(result8 == false)
//
//    println("New Test")
//    var result9 = DatabaseHandler.UpdateUser("adam","eden","adam south",null)
//    var result10 = DatabaseHandler.ValidateUser("adam","eden")
//    assert(result10 == JsonMapper.getUserResponse("0x7000","adam south","adam"))
//
//    var result11 = DatabaseHandler.UpdateUser("adam","eden",null,"banana")
//    var result12 = DatabaseHandler.ValidateUser("adam","banana")
//    assert(result12 == JsonMapper.getUserResponse("0x7000","adam south","adam"))
//
//    var result13 = DatabaseHandler.UpdateUser("adam","eden","adam south",null)
//    assert(result13 == JsonMapper.getStatusResponse("0x9000"))

  }

  test("Auth Bool"){
    var result1 = DatabaseHandler.InsertUser("adam","eden","adam west")

    var res = DatabaseHandler.ValidateUser("adam","eden")

    var isValid = JsonMapper.parseUserResponse(res)
    assert(isValid == true)
  }

  after { db.close }
}
