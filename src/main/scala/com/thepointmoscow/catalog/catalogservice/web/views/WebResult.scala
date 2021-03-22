package com.thepointmoscow.catalog.catalogservice.web.views

sealed trait WebResult[T <: AnyRef] {
  def getCode(): Int

  def getMessage(): String

  def getPayload(): T
}

case class WebSuccess[T <: AnyRef](getPayload: T) extends WebResult[T] {
  override def getCode(): Int = 0

  override def getMessage(): String = null
}

case class WebFailure[T <: AnyRef](getCode: Int, getMessage: String) extends WebResult[T] {
  override def getPayload(): T = null.asInstanceOf[T]
}
