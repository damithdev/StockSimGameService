package com.stockmarket.actors.management

import akka.actor.{Actor, ActorLogging, Props}
import com.stockmarket.actors.management.UserManager._
import com.stockmarket.db.DatabaseHandler
import com.stockmarket.messages.JsonMapper

object UserManager {
  //  def getPasshash(password: String): String =
  //    DigestUtils.sha256Hex(password)

  def props: Props = Props(classOf[UserManager])


  def apply(
             username: String,
             password: String,
             fullname: String,
           ): User = {
    new User(username,password,fullname)
  }

  def apply(
             username: String,
             password: String,
           ): User = {
    new User(username,password,null)
  }

  case class User(
                   username: String,
                   password: String,
                   fullname: String,
                 ) {

  }

  case class GetUser(username: String)

  case object GetAllUsers

  case class AddUser(user: User)

  case class DeleteOne(username: String)

  case class UserListResponse(users: Seq[(String,String)])

  case class Authenticate(username: String, password: String)

  case class UpdateUserData(username: String, password: String,fullname: String, newpassword: String)

  case class AuthRes(res: Boolean, message: String)

}




class UserManager extends Actor with ActorLogging{


  def receive = {
    case AddUser(user: User) =>

      if (DatabaseHandler.UserExists(user.username)) {

        sender() ! JsonMapper.getStatusResponse("0x8000")
      } else {
        sender() ! JsonMapper.getStatusResponse("0x7000")
        DatabaseHandler.InsertUser(user.username,user.password,user.fullname)
      }
//
    case GetAllUsers =>
      val fu = DatabaseHandler.getUsers
      sender() ! UserListResponse(fu)

    case Authenticate(username, password) =>


      val auth = DatabaseHandler.ValidateUser(username,password)

      sender() ! auth

    case UpdateUserData(username,password,fullname,newpassword) =>

      val update = DatabaseHandler.UpdateUser(username,password,fullname,newpassword)
      sender() ! update

  }
}
