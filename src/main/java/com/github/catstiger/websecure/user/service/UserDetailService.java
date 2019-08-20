package com.github.catstiger.websecure.user.service;

import com.github.catstiger.websecure.user.model.UserDetail;

public interface UserDetailService {
  /**
   * 根据用户名（登录名），返回UserDetail实例，如果不存在，返回{@code null}
   * @param username 用户名
   * @return 返回UserDetail实例，如果不存在，返回{@code null}
   */
  UserDetail byUsername(String username);
  
  /**
   * 根据UserDetail的手机号，返回UserDetail实例，如果不存在，返回{@code null}
   * @param phone 手机号
   * @return 返回UserDetail实例，如果不存在，返回{@code null}
   */
  UserDetail byPhone(String phone);
}
