package com.github.catstiger.websecure.authz;

public interface AuthenticationServiceProvider {
  /**
   * 返回AuthenticationService的实例。实现类根据参数、cookie、header等信息，判断采用哪种方式验证。
   */
  public AuthenticationService getAuthenticationService();
}