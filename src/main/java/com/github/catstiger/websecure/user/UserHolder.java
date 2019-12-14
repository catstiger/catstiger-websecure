package com.github.catstiger.websecure.user;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.catstiger.websecure.subject.Subject;
import com.github.catstiger.websecure.user.model.User;
import com.github.catstiger.websecure.web.SubjectHolder;

/**
 * 如果配置了{@code WebSecurityFilter}, 则可以通过{@code UserHolder#getUser()}
 * 获取当前登录用户
 * 
 * @author samlee
 */
public final class UserHolder {
  private static Logger logger = LoggerFactory.getLogger(UserHolder.class);
  /**
   * 获取当前登录用户，如果没有登录，或者超出{@code WebSecurityFilter}范围，则返回{@code null}
   * @return 如果已经登录，则返回当前登录用户，否则返回{@code null}
   */
  public static User getUser() {
    try {
      Subject subject = SubjectHolder.getSubject();
      if (subject != null) {
        return (User) subject.getPrincipal();
      }
    } catch (Exception e) {
      logger.info("无法获取当前登录用户 {}", e.getMessage());
    }
    return null;
  }
  
  private UserHolder() {
    
  }
}
