package com.evst.tvint.frontend

/**
 * User: EStankevich
 * Date: 24.05.2014
 */
class ParseException(reason: String, cause: Throwable) extends Exception(reason, cause) {
  def this(reason: String) = this(reason, null)
}
