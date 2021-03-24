package com.thepointmoscow.catalog.catalogservice.web.views

sealed trait WebResult[T <: AnyRef] {
  def getErrorCode(): Int

  def getError(): String

  def getPayload(): T
}

case class WebSuccess[T <: AnyRef](getPayload: T) extends WebResult[T] {
  override def getErrorCode(): Int = 0

  override def getError(): String = null
}

case class WebFailure[T <: AnyRef](getErrorCode: Int, getError: String) extends WebResult[T] {
  override def getPayload(): T = null.asInstanceOf[T]
}
