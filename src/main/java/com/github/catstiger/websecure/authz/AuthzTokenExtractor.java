package com.github.catstiger.websecure.authz;

/**
 * 获取登录信息的接口
 *
 */
public interface AuthzTokenExtractor {
  /**
   * 从HttpServletReqeust中提取用户认证信息，包括用户名、密码等。实现者根据不同的规范，从HttpServletRequest中提取数据。
   * 实现者可以从{@code WebObjectsHolder}中获取HttpServletRequest.
   * @return AuthzToken，如果没有任何登录信息，则返回{@code null}
   */
  AuthzToken get();
}
