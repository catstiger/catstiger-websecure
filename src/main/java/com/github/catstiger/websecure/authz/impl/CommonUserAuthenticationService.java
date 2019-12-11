package com.github.catstiger.websecure.authz.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.catstiger.common.util.Exceptions;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.authz.AuthzToken;
import com.github.catstiger.websecure.cfg.SecurityProperties;
import com.github.catstiger.websecure.login.CredentialException;
import com.github.catstiger.websecure.password.PasswordEncoder;

@Component
public class CommonUserAuthenticationService extends AbstractAuthenticationService {
  @Autowired
  private SecurityProperties config;
  @Autowired
  private PasswordEncoder passwordEncoder;

  public CommonUserAuthenticationService() {

  }

  public CommonUserAuthenticationService(SecurityProperties config) {
    this.config = config;
  }

  @Override
  public void verifyCredential(AuthzToken token, Object anwser) {
    if (token == null) {
      throw Exceptions.unchecked("错误的Token！");
    }
    
    if (token.getCredentials() == null) {
      if(getNext() != null) {
        getNext().verifyCredential(token, anwser);
      }
      return;
    }
    
    if (StringUtils.isBlank(token.getCredentials())
        || !passwordEncoder.matches(token.getCredentials(), (String) anwser)) {
      throw new CredentialException(SecureConstants.MSG_PASSWORD_MISTAKE);
    }
    
    if(getNext() != null) {
      getNext().verifyCredential(token, anwser);
    }
  }

  public SecurityProperties getConfig() {
    return config;
  }

  public void setConfig(SecurityProperties config) {
    this.config = config;
  }

  @Override
  public int getOrder() {
    return 10;
  }

}
