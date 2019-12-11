package com.github.catstiger.websecure.authz;

import com.github.catstiger.websecure.login.CredentialException;

/**
 * 从上下文（Servlet）中提取用户认证信息。
 * @author samlee
 *
 */
public interface AuthenticationService extends Comparable<AuthenticationService> {
  /**
   * 验证 Credential 是否正确，并且，根据 情况，决定是否
   * @param token 用户提交的{@link AutzToken}
   * @param anwser 正确答案，如果是用户名密码验证
   * @throws CredentialException 如果凭证验证失败
   */
  void verifyCredential(AuthzToken token, Object anwser) throws CredentialException;
  
  /**
   * 返回下一个{@code AuthenticationService} 的实现类
   * @return
   */
  AuthenticationService getNext();
  
  /**
   * 设置下一个AuthenticationService的实现类
   * @param next
   */
  void setNext(AuthenticationService next);
  
  int getOrder();
}
