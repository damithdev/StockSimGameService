package com.stockmarket.db


import com.stockmarket.messages.JsonMapper
import slick.driver.H2Driver.api._

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import slick.backend.DatabasePublisher

object DatabaseHandler{

  // the base query for the Users table
  // The query interface for the Suppliers table


  lazy val db = Database.forConfig("h2mem1")

//  def createSchema() =
//    db.run((users.schema).create)

  def InsertUser(user : String, pass : String, fullname : String):Boolean = {
    try {
      val users: TableQuery[Users] = TableQuery[Users]

      val setupAction: DBIO[Unit] = DBIO.seq(
        // Create the schema by combining the DDLs for the Suppliers and Coffees
        // tables using the query interfaces
        users.schema.createIfNotExists,

        // Insert some suppliers
//        users += ("admin", "123456")
      )

      var buffer = new ListBuffer[String]()
      val setupFuture: Future[Unit] = db.run(setupAction)


        val f = setupFuture.flatMap { _ =>



        // Insert some coffees (using JDBC's batch insert feature)
        val insertAction: DBIO[Option[Int]] = users ++= Seq (
          (user,pass,fullname),
        )

        val insertAndPrintAction: DBIO[Unit] = insertAction.map { usersInsertResult =>
          // Print the number of rows inserted
          usersInsertResult foreach { numRows =>
            println(s"Inserted $numRows rows into the Users table")
          }
        }

        val allUsersAction: DBIO[Seq[(String, String,String)]] =
          users.result

         val filterQuery: Query[Users, (String, String,String), Seq] =
            users.filter(_.username === user)

        val combinedAction: DBIO[Seq[( String, String,String)]] =
          insertAndPrintAction >> allUsersAction

        val combinedFuture: Future[Seq[(String, String,String)]] =
          db.run(combinedAction)

          combinedFuture.map { allUsers =>
            allUsers.foreach( item => {
              buffer += item._1
            } )
          }

//          return buffer.toList

      }
      Await.result(f, Duration.Inf)

      if(UserExists(user)){
        return true
      }else{
        return false
      }

    }catch {
      case x : Exception => {
        println(x)
      }
        return false
    }finally {}
  }


  def getUsers:List[(String,String)] = {
    var buffer = new ListBuffer[(String,String)]()
    try {
      // The query interface for the Suppliers table
      val users: TableQuery[Users] = TableQuery[Users]

      val setupAction: DBIO[Unit] = DBIO.seq(
        // Create the schema by combining the DDLs for the Suppliers and Coffees
        // tables using the query interfaces
        users.schema.createIfNotExists,

        // Insert some suppliers
        //        users += ("admin", "123456")
      )

      val setupFuture: Future[Unit] = db.run(setupAction)


      val f = setupFuture.flatMap { _ =>


          val allUsersAction: DBIO[Seq[(String, String,String)]] =
          users.result

          val combinedFuture: Future[Seq[(String, String,String)]] =
            db.run(allUsersAction)

          combinedFuture.map { allUsers =>
            allUsers.foreach( item => {
              buffer.+= ((item._1,item._3))
            } )
          }
//          val userNamesAction: StreamingDBIO[Seq[String], String] =
//          users.map(_.username).result
//
//
//
//          val userNamesPublisher: DatabasePublisher[String] =
//          db.stream(userNamesAction)
//
//          userNamesPublisher.foreach(item => {
//            buffer += item
//          })

      }
      Await.result(f, Duration.Inf)

      return buffer.toList


    } finally {}
  }

  def ValidateUser(user: String, pass: String):String = {
    var buffer = new ListBuffer[String]()
    try {
      // The query interface for the Suppliers table
      val users: TableQuery[Users] = TableQuery[Users]

      val setupAction: DBIO[Unit] = DBIO.seq(
        // Create the schema by combining the DDLs for the Suppliers and Coffees
        // tables using the query interfaces
        users.schema.createIfNotExists,

        // Insert some suppliers
        //        users += ("admin", "123456")
      )

      val setupFuture: Future[Unit] = db.run(setupAction)


      val f = setupFuture.flatMap { _ =>


          val filterQuery: Query[Users, (String, String,String), Seq] =
          users.filter(_.username === user)

          // Print the SQL for the filter query
          println("Generated SQL for filter query:\n" + filterQuery.result.statements)

          // Execute the query and print the Seq of results
          db.run(filterQuery.result.headOption)

      }
      var x = Await.result(f, Duration.Inf)
      var res = x.map(s=>(s._2==pass))
      var state : Boolean = res.getOrElse(false)

      var response : String = null
      if(state){
        x.map(s=>{
          response = JsonMapper.getUserResponse("0x7000",s._3,s._1)
        })
      }
      if(response == null){
        response = JsonMapper.getStatusResponse("0x8000")
      }
      return response

    }catch {
      case x : Exception => {
        println("Exception")
      }
      return JsonMapper.getStatusResponse("0x9000")
    }finally {}
  }


  def getFullNameByUserName(user: String):String = {
//    var buffer = new ListBuffer[String]()
    try {
      // The query interface for the Suppliers table
      val users: TableQuery[Users] = TableQuery[Users]

      val setupAction: DBIO[Unit] = DBIO.seq(
        // Create the schema by combining the DDLs for the Suppliers and Coffees
        // tables using the query interfaces
        users.schema.createIfNotExists,

        // Insert some suppliers
        //        users += ("admin", "123456")
      )

      val setupFuture: Future[Unit] = db.run(setupAction)


      val f = setupFuture.flatMap { _ =>


        val filterQuery: Query[Users, (String, String,String), Seq] =
          users.filter(_.username === user)

        // Print the SQL for the filter query
        println("Generated SQL for filter query:\n" + filterQuery.result.statements)

        // Execute the query and print the Seq of results
        db.run(filterQuery.result.headOption)

      }
      var x = Await.result(f, Duration.Inf)
      var res = x.map(s=>s._3).getOrElse(null)
      return res;
//      var state : Boolean = res.getOrElse(false)
//
//      var response : String = null
//      if(state){
//        x.map(s=>{
//          response = JsonMapper.getUserResponse("0x7000",s._3,s._1)
//        })
//      }
//      if(response == null){
//        response = JsonMapper.getStatusResponse("0x8000")
//      }
//      return response

    }catch {
      case x : Exception => {
        println("Exception")
      }
        return null
    }finally {}
  }

  def UserExists(user: String):Boolean = {
    var buffer = new ListBuffer[String]()
    try {
      // The query interface for the Suppliers table
      val users: TableQuery[Users] = TableQuery[Users]

      val setupAction: DBIO[Unit] = DBIO.seq(
        // Create the schema by combining the DDLs for the Suppliers and Coffees
        // tables using the query interfaces
        users.schema.createIfNotExists,

        // Insert some suppliers
        //        users += ("admin", "123456")
      )

      val setupFuture: Future[Unit] = db.run(setupAction)


      val f = setupFuture.flatMap { _ =>


        val filterQuery: Query[Users, (String, String,String), Seq] =
          users.filter(_.username === user)

        // Print the SQL for the filter query
        println("Generated SQL for filter query:\n" + filterQuery.result.statements)

        // Execute the query and print the Seq of results
        db.run(filterQuery.result.headOption)

      }
      var x = Await.result(f, Duration.Inf)
      var res = x.map(s=>(s._1==user))
      return res.getOrElse(false)



    }catch {
      case x : Exception => {
        println("Exception")
      }
        return false
    }finally {}
  }


  def UpdateUser(user : String, password : String, fullname : String, newpassword: String):String = {
    try {
      val users: TableQuery[Users] = TableQuery[Users]

      val setupAction: DBIO[Unit] = DBIO.seq(
        // Create the schema by combining the DDLs for the Suppliers and Coffees
        // tables using the query interfaces
        users.schema.createIfNotExists,

        // Insert some suppliers
        //        users += ("admin", "123456")
      )

      val setupFuture: Future[Unit] = db.run(setupAction)


      val f = setupFuture.flatMap { _ =>



        val filterQuery: Query[Users, (String, String,String), Seq] =
          users.filter(_.username === user).filter(_.password === password)

        // Print the SQL for the filter query
        println("Generated SQL for filter query:\n" + filterQuery.result.statements)

        // Execute the query and print the Seq of results
        var x = Await.result(db.run(filterQuery.result.headOption), Duration.Inf)

        if(x == None)throw new Exception("Invalid Credentials")

        var updateAction: DBIO[Int] = null
        var query : Query[Rep[String], String, Seq]= null
        if(fullname != null){
          val updateQuery: Query[Rep[String], String, Seq] = users.filter(_.username === user).map(_.fullname)
          updateAction = updateQuery.update(fullname)
          query = updateQuery
        }

        if(newpassword != null){
          val updateQuery: Query[Rep[String], String, Seq] = users.filter(_.username === user).map(_.password)
          updateAction = updateQuery.update(newpassword)
          query = updateQuery
        }

        if(updateAction == null)throw new NullPointerException("Invalid Update Information")




        // Print the SQL for the Users update query
        println("Generated SQL for Users update:\n" + query.updateStatement)

        // Perform the update
        db.run(updateAction.map { numUpdatedRows =>
          println(s"Updated $numUpdatedRows rows")
        })

        val filterQuery2: Query[Users, (String, String,String), Seq] =
          users.filter(_.username === user)

        // Print the SQL for the filter query
        println("Generated SQL for filter query:\n" + filterQuery2.result.statements)

        // Execute the query and print the Seq of results
        db.run(filterQuery2.result.headOption)

      }

      var x = Await.result(f, Duration.Inf)

      var response : String = null

        x.map(s=>{
          response = JsonMapper.getUserResponse("0x7000",s._3,s._1)
        })
      if(response == null){
        response = JsonMapper.getStatusResponse("0x8000")
      }

      return response

    }catch {
      case x : Exception => {
        println(x)
      }
      return JsonMapper.getStatusResponse("0x9000")
    }finally {}
  }


  def CreateGame(user : String, password : String, fullname : String, newpassword: String):String = {
    try {
      val users: TableQuery[Users] = TableQuery[Users]

      val setupAction: DBIO[Unit] = DBIO.seq(
        // Create the schema by combining the DDLs for the Suppliers and Coffees
        // tables using the query interfaces
        users.schema.createIfNotExists,

        // Insert some suppliers
        //        users += ("admin", "123456")
      )

      val setupFuture: Future[Unit] = db.run(setupAction)


      val f = setupFuture.flatMap { _ =>



        val filterQuery: Query[Users, (String, String,String), Seq] =
          users.filter(_.username === user).filter(_.password === password)

        // Print the SQL for the filter query
        println("Generated SQL for filter query:\n" + filterQuery.result.statements)

        // Execute the query and print the Seq of results
        var x = Await.result(db.run(filterQuery.result.headOption), Duration.Inf)

        if(x == None)throw new Exception("Invalid Credentials")

        var updateAction: DBIO[Int] = null
        var query : Query[Rep[String], String, Seq]= null
        if(fullname != null){
          val updateQuery: Query[Rep[String], String, Seq] = users.filter(_.username === user).map(_.fullname)
          updateAction = updateQuery.update(fullname)
          query = updateQuery
        }

        if(newpassword != null){
          val updateQuery: Query[Rep[String], String, Seq] = users.filter(_.username === user).map(_.password)
          updateAction = updateQuery.update(newpassword)
          query = updateQuery
        }

        if(updateAction == null)throw new NullPointerException("Invalid Update Information")




        // Print the SQL for the Users update query
        println("Generated SQL for Users update:\n" + query.updateStatement)

        // Perform the update
        db.run(updateAction.map { numUpdatedRows =>
          println(s"Updated $numUpdatedRows rows")
        })

        val filterQuery2: Query[Users, (String, String,String), Seq] =
          users.filter(_.username === user)

        // Print the SQL for the filter query
        println("Generated SQL for filter query:\n" + filterQuery2.result.statements)

        // Execute the query and print the Seq of results
        db.run(filterQuery2.result.headOption)

      }

      var x = Await.result(f, Duration.Inf)

      var response : String = null

      x.map(s=>{
        response = JsonMapper.getUserResponse("0x7000",s._3,s._1)
      })
      if(response == null){
        response = JsonMapper.getStatusResponse("0x8000")
      }

      return response

    }catch {
      case x : Exception => {
        println(x)
      }
        return JsonMapper.getStatusResponse("0x9000")
    }finally {}
  }


}



//case class User(name: String, id: Option[Int] = None)

//class Users(tag: Tag) extends Table[User](tag, "USERS") {
//  // Auto Increment the id primary key column
//  def id = column[Int]("ID", O.PrimaryKey, O.AutoInc)
//  // The name can't be null
//  def name = column[String]("NAME")
//  // the * projection (e.g. select * ...) auto-transforms the tupled
//  // column values to / from a User
//  def * = (name, id.?) <> (User.tupled, User.unapply)
//}

//class Users(tag: Tag)
//  extends Table[(String, String)](tag, "USERS") {
//
//  // This is the primary key column:
////  def id: Rep[Int] = column[Int]("U_ID", O.PrimaryKey)
//  def name: Rep[String] = column[String]("U_NAME")
//  def password: Rep[String] = column[String]("U_PASS")
//
//
//  // Every table needs a * projection with the same type as the table's type parameter
//  def * : ProvenShape[(String, String)] =
//    (name, password)
//}



//object HelloSlicks extends App {
//  val db = Database.forConfig("h2mem1")
//
//  dbTest("Damith")
//
//  def dbTest(hello : String) = {
//    try {
//      println(hello)
//      // The query interface for the Suppliers table
//      val suppliers: TableQuery[Suppliers] = TableQuery[Suppliers]
//
//      // the query interface for the Coffees table
//      val coffees: TableQuery[Coffees] = TableQuery[Coffees]
//
//      val setupAction: DBIO[Unit] = DBIO.seq(
//        // Create the schema by combining the DDLs for the Suppliers and Coffees
//        // tables using the query interfaces
//        (suppliers.schema ++ coffees.schema).create,
//
//        // Insert some suppliers
//        suppliers += (101, "Acme, Inc.", "99 Market Street", "Groundsville", "CA", "95199"),
//        suppliers += ( 49, "Superior Coffee", "1 Party Place", "Mendocino", "CA", "95460"),
//        suppliers += (150, "The High Ground", "100 Coffee Lane", "Meadows", "CA", "93966")
//      )
//
//      val setupFuture: Future[Unit] = db.run(setupAction)
//
//
//      val f = setupFuture.flatMap { _ =>
//
//        // Insert some coffees (using JDBC's batch insert feature)
//        val insertAction: DBIO[Option[Int]] = coffees ++= Seq (
//          ("Colombian",         101, 7.99, 0, 0),
//          ("French_Roast",       49, 8.99, 0, 0),
//          ("Espresso",          150, 9.99, 0, 0),
//          ("Colombian_Decaf",   101, 8.99, 0, 0),
//          ("French_Roast_Decaf", 49, 9.99, 0, 0)
//        )
//
//        val insertAndPrintAction: DBIO[Unit] = insertAction.map { coffeesInsertResult =>
//          // Print the number of rows inserted
//          coffeesInsertResult foreach { numRows =>
//            println(s"Inserted $numRows rows into the Coffees table")
//          }
//        }
//
//        val allSuppliersAction: DBIO[Seq[(Int, String, String, String, String, String)]] =
//          suppliers.result
//
//        val combinedAction: DBIO[Seq[(Int, String, String, String, String, String)]] =
//          insertAndPrintAction >> allSuppliersAction
//
//        val combinedFuture: Future[Seq[(Int, String, String, String, String, String)]] =
//          db.run(combinedAction)
//
//        combinedFuture.map { allSuppliers =>
//          allSuppliers.foreach(println)
//        }
//
//      }
//      Await.result(f, Duration.Inf)
//
//    } finally db.close
//  }
//}


//  val coffeeNamesAction: StreamingDBIO[Seq[String], String] =
//  coffees.map(_.name).result
//
//  val coffeeNamesPublisher: DatabasePublisher[String] =
//  db.stream(coffeeNamesAction)
//
//  coffeeNamesPublisher.foreach(println)