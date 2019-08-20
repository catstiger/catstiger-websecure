package com.github.catstiger.websecure.login;

@SuppressWarnings("serial")
public class CredentialException extends LoginException {
  /**
   * Constructs a CredentialException with no detail message. A detail
   * message is a String that describes this particular exception.
   */
  public CredentialException() {
    super();
  }

  /**
   * Constructs a CredentialException with the specified detail message.
   * A detail message is a String that describes this particular
   * exception.
   *
   *
   * @param msg the detail message.
   */
  public CredentialException(String msg) {
    super(msg);
  }
}
