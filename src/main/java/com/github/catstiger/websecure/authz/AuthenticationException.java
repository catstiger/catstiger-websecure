package com.github.catstiger.websecure.authz;

/**
 * 认证过程中的Exception
 * 
 * @author samlee
 *
 */
@SuppressWarnings("serial")
public class AuthenticationException extends RuntimeException {

	public AuthenticationException() {
		super();
	}

	public AuthenticationException(String message) {
		super(message);
	}

	public AuthenticationException(Throwable cause) {
		super(cause);
	}

	public AuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}
}
