package org.user.repositories

import org.db.config.DbConfig
import org.db.data.Employee
import org.mongodb.scala.{Completed, MongoCollection}
import org.mongodb.scala.bson.conversions.Bson
import org.mongodb.scala.model.Filters.equal
import org.mongodb.scala.model.FindOneAndUpdateOptions
import org.mongodb.scala.model.Updates.{combine, set}
import org.mongodb.scala.result.DeleteResult
import org.utils.JsonUtils

import scala.concurrent.Future

object EmployeeRepo extends JsonUtils {
  private val employeeDoc: MongoCollection[Employee] = DbConfig.employees

  def createCollection(): Unit = {
    DbConfig.database.createCollection("employee").subscribe(
      (result: Completed) => println(s"$result"),
      (e: Throwable) => println(e.getLocalizedMessage),
      () => println("complete"))
  }

  def insertData(emp: Employee): Future[Completed] = {
    employeeDoc.insertOne(emp).toFuture()
  }

  def findAll(): Future[Seq[Employee]] = {
    employeeDoc.find().toFuture()
  }

  def update(emp: Employee, id: String):Future[Employee] = {

    employeeDoc
      .findOneAndUpdate(equal("_id", id),
        setBsonValue(emp),
        FindOneAndUpdateOptions().upsert(true)).toFuture()
  }

  def delete(id: String): Future[DeleteResult] = {
    employeeDoc.deleteOne(equal("_id", id)).toFuture()
  }

  private def setBsonValue(emp:Employee): Bson = {
    combine(
      set("name", emp.name),
      set("dateOfBirth",emp.dateOfBirth)
    )
  }
}
