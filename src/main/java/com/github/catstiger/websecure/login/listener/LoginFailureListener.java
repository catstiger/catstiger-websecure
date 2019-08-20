package com.github.catstiger.websecure.login.listener;

import com.github.catstiger.websecure.login.LoginException;

public interface LoginFailureListener {
  /**
   * Called when an login attempt fails.
   * @param exception the exception which was thrown to reject the login request.
   */
  void onAuthenticationFailure(LoginException exception);
}
