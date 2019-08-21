package com.github.catstiger.websecure.login;

@SuppressWarnings("serial")
public class AccountStatusException extends AccountException {
	/**
	 * Constructs a <code>AccountExpiredException</code> with the specified message.
	 *
	 * @param msg the detail message
	 */
	public AccountStatusException(String msg) {
		super(msg);
	}

	/**
	 * Constructs a <code>AccountExpiredException</code> with the specified message and root cause.
	 *
	 * @param msg the detail message
	 * @param t   root cause
	 */
	public AccountStatusException(String msg, Throwable t) {
		super(msg, t);
	}
}
