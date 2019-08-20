package com.github.catstiger.websecure.login.listener;

import com.github.catstiger.websecure.subject.Subject;

public interface LogoutListener {
  /**
   * 在登出之后执行某些操作
   */
  void afterLogout(Subject subject);
}
