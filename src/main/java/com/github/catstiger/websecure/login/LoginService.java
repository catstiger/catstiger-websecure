package com.github.catstiger.websecure.login;

public interface LoginService {
  /**
   * 用于创建一个认证的{@code Subject} 
   * <p>
   * 这个方法的实现，用于认证一个{@code Subject} ，例如，根据username取得用户信息，并验证password。
   * </p>
   * @exception LoginException 如果认证失败
   * @return 如果认证成功返回{@code true}, 如果认证失败，返回{@code false}，表示这个认证模块被忽略
   */
  Object login();
  
  /**
   * 用于{@code Subject}登出的方法。
   * <p>
   * 这个方法的实现，用于删除或者销毁一个Subject的Principals,和Credentials
   * </p>
   * @exception LoginException 如果登出失败
   * @return 如果成功，返回{@code true}，否则，返回{@code false}
   */
  Object logout() throws LoginException;
}
