package com.github.catstiger.websecure.login;

@SuppressWarnings("serial")
public class AccountNotFoundException extends AccountException {
  /**
   * Constructs a <code>AccountNotFoundException</code> with the specified
   * message.
   *
   * @param msg the detail message
   */
  public AccountNotFoundException(String msg) {
    super(msg);
  }

  /**
   * Constructs a <code>AccountNotFoundException</code> with the specified
   * message and root cause.
   *
   * @param msg the detail message
   * @param t root cause
   */
  public AccountNotFoundException(String msg, Throwable t) {
    super(msg, t);
  }
}
