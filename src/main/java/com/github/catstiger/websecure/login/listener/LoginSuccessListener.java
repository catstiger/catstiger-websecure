package com.github.catstiger.websecure.login.listener;

import com.github.catstiger.websecure.subject.Subject;

public interface LoginSuccessListener {
  /**
   * Called when a user has been successfully login.
   *
   * @param subject the <tt>Subject</tt> object which was created during the authentication process.
   */
  public void onSuccess(Subject subject);
}
