package com.github.catstiger.websecure.authz;

import com.github.catstiger.websecure.login.CredentialException;

/**
 * 从上下文（Servlet）中提取用户认证信息。
 * @author samlee
 *
 */
public interface AuthenticationService {
  /**
   * 从HttpServletReqeust中提取用户认证信息，包括用户名、密码等。实现者根据不同的规范，从HttpServletRequest中提取数据。
   * 实现者可以从{@code WebObjectsHolder}中获取HttpServletRequest.
   * @return AuthzToken，如果没有任何登录信息，则返回{@code null}
   */
  AuthzToken get();
  
  /**
   * 验证 Credential 是否正确
   * @param token 用户提交的{@link AutzToken}
   * @param anwser 正确答案，如果是用户名密码验证
   * @throws CredentialException 如果凭证验证失败
   */
  void verifyCredential(AuthzToken token, Object anwser) throws CredentialException;
}
