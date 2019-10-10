package com.stockmarket.db

import slick.driver.H2Driver.api._
import slick.lifted.{ProvenShape, ForeignKeyQuery}

// A Suppliers table with 6 columns: id, name, street, city, state, zip
class Users(tag: Tag)
  extends Table[(String, String,String)](tag, "USERS") {

  // This is the primary key column:
//  def id: Rep[Int] = column[Int]("U_ID", O.PrimaryKey)
  def username: Rep[String] = column[String]("U_NAME", O.PrimaryKey)
  def password: Rep[String] = column[String]("U_PASS")
  def fullname: Rep[String] = column[String]("F_NAME")


  // Every table needs a * projection with the same type as the table's type parameter
  def * : ProvenShape[(String, String,String)] =
    (username, password,fullname)
}

