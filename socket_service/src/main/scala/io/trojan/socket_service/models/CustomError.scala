package io.trojan.socket_service.models

abstract class CustomError(message: String) extends Exception(message) {
  def isRecoverable: Boolean
}

object CustomError {
  case class RecoverableError() extends CustomError("Custom Recoverable Error") {
    override val isRecoverable = true
  }

  case class NotRecoverableError() extends CustomError("Custom Not Recoverable Error") {
    override val isRecoverable = false
  }
}