package com.github.catstiger.websecure.authc;

@SuppressWarnings("serial")
public class AccessDeniedException extends RuntimeException {
	/**
	 * Constructs an <code>AccessDeniedException</code> with the specified message.
	 *
	 * @param msg the detail message
	 */
	public AccessDeniedException(String msg) {
		super(msg);
	}

	/**
	 * Constructs an <code>AccessDeniedException</code> with the specified message and root cause.
	 *
	 * @param msg the detail message
	 * @param t   root cause
	 */
	public AccessDeniedException(String msg, Throwable t) {
		super(msg, t);
	}
}
