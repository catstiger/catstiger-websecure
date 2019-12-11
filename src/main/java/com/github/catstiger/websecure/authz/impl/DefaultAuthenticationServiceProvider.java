package com.github.catstiger.websecure.authz.impl;

import java.util.Collections;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.catstiger.websecure.authz.AuthenticationService;
import com.github.catstiger.websecure.authz.AuthenticationServiceProvider;

@Service
public class DefaultAuthenticationServiceProvider implements AuthenticationServiceProvider {
  private static Logger logger = LoggerFactory.getLogger(DefaultAuthenticationServiceProvider.class);
  
  @Autowired
  private List<AuthenticationService> authChain;
  
  private AuthenticationService root;

  @Override
  public AuthenticationService getAuthenticationService() {
    if (root != null) {
      return root;
    }
    
    if (CollectionUtils.isEmpty(authChain)) {
      throw new java.lang.IllegalStateException("未提供任何AuthenticationService的实现");
    }
    Collections.sort(authChain);
    // 认证链
    AuthenticationService temp = null;
    for(AuthenticationService authenticationService : authChain) {
      if (root == null) {
        root = authenticationService;
        temp = root;
      } else {
        temp.setNext(authenticationService);
        temp = authenticationService;
      }
    }
    AuthenticationService authenticationService = root;
    while (authenticationService != null) {
      logger.debug("认证Service {}, {}", authenticationService.getClass().getSimpleName(), authenticationService.getOrder());
      authenticationService = authenticationService.getNext();
    }
    
    return root;
  }
  
  
}
