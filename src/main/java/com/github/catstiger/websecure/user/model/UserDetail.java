package com.github.catstiger.websecure.user.model;

import java.io.Serializable;

public interface UserDetail extends Serializable {
  /**
   * @return Username，此Username与 {@link User#getUsername()}相对应
   */
  String getUsername();
  
  /**
   * @return 返回UserID，此ID与{@link User#getId()} 相对应
   */
  Long getUserId();
  
  /**
   * @return 返回用户手机号码
   */
  String getPhone();
}
