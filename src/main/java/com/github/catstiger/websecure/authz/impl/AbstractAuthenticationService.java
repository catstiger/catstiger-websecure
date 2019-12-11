package com.github.catstiger.websecure.authz.impl;

import com.github.catstiger.websecure.authz.AuthenticationService;

public abstract class AbstractAuthenticationService implements AuthenticationService {
  protected AuthenticationService next;
  
  @Override
  public AuthenticationService getNext() {
    return next;
  }

  @Override
  public void setNext(AuthenticationService next) {
    this.next = next;
  }

  @Override
  public int compareTo(AuthenticationService o) {
    if (o == null) {
      return 1;
    }
    
    return Integer.valueOf(getOrder()).compareTo(o.getOrder());
  }

}
