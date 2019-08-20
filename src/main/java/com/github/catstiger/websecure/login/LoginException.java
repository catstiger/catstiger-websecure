package com.github.catstiger.websecure.login;

import com.github.catstiger.websecure.authz.AuthenticationException;

@SuppressWarnings("serial")
public class LoginException extends AuthenticationException {

	/**
	 * Constructs a LoginException with no detail message. A detail message is a String that describes this particular exception.
	 */
	public LoginException() {
		super();
	}

	/**
	 * Constructs a LoginException with the specified detail message. A detail message is a String that describes this particular exception.
	 *
	 * @param msg the detail message.
	 */
	public LoginException(String msg) {
		super(msg);
	}

	/**
	 * 创建一个LoginException，带有详细信息，并传递Throwable
	 * 
	 * @param msg 详细信息
	 */
	public LoginException(String msg, Throwable t) {
		super(msg, t);
	}
}
