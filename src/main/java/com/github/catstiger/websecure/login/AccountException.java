package com.github.catstiger.websecure.login;


public class AccountException extends LoginException {

  private static final long serialVersionUID = -2112878680072211787L;

  /**
   * Constructs a AccountException with no detail message. A detail
   * message is a String that describes this particular exception.
   */
  public AccountException() {
    super();
  }

  /**
   * Constructs a AccountException with the specified detail message.
   * A detail message is a String that describes this particular
   * exception.
   
   * @param msg the detail message.
   */
  public AccountException(String msg) {
    super(msg);
  }
  
  /**
   * 创建一个AccountException，带有详细信息，并且传递Throwable
   * @param msg 详细信息
   */
  public AccountException(String msg, Throwable t) {
    super(msg, t);
  }
}
