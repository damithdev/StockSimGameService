package com.stockmarket.models


  sealed trait SECTOR { def name: String }



  case object FINANCE extends Enumeration with SECTOR {
    val style = "info"
    val id = "FINANCE"
    val name = "Finance"
    type Stock = Value
    val VISA, MASTERCARD, AMEX, PAYPAL = Value
  }

  case object TECHNOLOGY extends Enumeration with SECTOR {
    val style = "success"
    val id = "TECHNOLOGY"
    val name = "Technology"
    type Stock = Value
    val APPLE, GOOGLE, MICROSOFT, FACEBOOK = Value
  }

  case object CONSUMER_SERVICES extends Enumeration with SECTOR {
    val style = "primary"
    val id = "CONSUMER_SERVICES"
    val name = "Consumer Service"
    type Stock = Value
    val AMAZON, WALMART, STARBUCKS, UBER = Value
  }

  case object MANUFACTURING extends Enumeration with SECTOR{
    val style = "rose"
    val id = "MANUFACTURING"
    val name = "Manufacturing"
    type Stock = Value
    val SAMSUNG, TOYOTA, VOLKSWAGEN, FORD = Value
  }

  class Company(val n : String) extends SECTOR {
    val name = n
  }









