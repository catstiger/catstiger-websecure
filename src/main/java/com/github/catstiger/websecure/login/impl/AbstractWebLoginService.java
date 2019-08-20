package com.github.catstiger.websecure.login.impl;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSON;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.authz.AuthenticationService;
import com.github.catstiger.websecure.authz.AuthenticationServiceProvider;
import com.github.catstiger.websecure.authz.AuthzToken;
import com.github.catstiger.websecure.login.CredentialException;
import com.github.catstiger.websecure.login.LoginException;
import com.github.catstiger.websecure.subject.Subject;
import com.github.catstiger.websecure.subject.impl.DelegatingSubject;
import com.github.catstiger.websecure.user.model.User;

public abstract class AbstractWebLoginService extends AbstractLoginService {
  @Autowired
  private AuthenticationServiceProvider authenticationServiceProvider;
  
  @Override
  protected AuthzToken getLoginToken() {
    return authenticationServiceProvider.getAuthenticationService().get();
  }

  @Override
  protected Subject attemptLogin(AuthzToken token) {
    logger.debug("正在登录 {} {} {}", token.getPrincipal(), token.getCredentials(), token.getHost());
    
    if (token.getCredentials() == null) {
      logger.debug("登录失败 {} {}", SecureConstants.MSG_PASSWORD_MISTAKE, token.getPrincipal());
      throw new CredentialException(SecureConstants.MSG_PASSWORD_MISTAKE);
    }
    
    DelegatingSubject subject = null;
    try {
      subject = new DelegatingSubject(userService, token);
    } catch (LoginException e) {
      //e.printStackTrace();
      logger.info("登录失败，用户名不存在 {} {}", SecureConstants.MSG_USER_NOT_FOUND, token.getPrincipal());
      throw new LoginException(SecureConstants.MSG_USER_NOT_FOUND);
    }
   
    AuthenticationService authenticationService = authenticationServiceProvider.getAuthenticationService();
    try {
      authenticationService.verifyCredential(token, ((User) subject.getPrincipal()).getPassword());
      subject.setAuthenticated(true);
    } catch (Exception e) {
      logger.info("登录失败，密码错误 {}", JSON.toJSONString(token));
      subject.setAuthenticated(false);
      throw new CredentialException(e.getMessage());
    }
    
    return subject;
  }

  public AuthenticationServiceProvider getAuthenticationServiceProvider() {
    return authenticationServiceProvider;
  }

}
