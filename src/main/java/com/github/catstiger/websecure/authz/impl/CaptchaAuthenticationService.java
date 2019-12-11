package com.github.catstiger.websecure.authz.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.catstiger.common.util.Exceptions;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.authz.AuthzToken;
import com.github.catstiger.websecure.authz.CaptchaChecker;
import com.github.catstiger.websecure.login.CredentialException;

@Component
public class CaptchaAuthenticationService extends AbstractAuthenticationService  {
  @Autowired(required = false)
  private CaptchaChecker captchaChecker;
  
  @Override
  public void verifyCredential(AuthzToken token, Object anwser) throws CredentialException {
    if (token == null) {
      throw Exceptions.unchecked("错误的Token！");
    }

    //如果未提供验证器，或者未提供验证码
    if (captchaChecker == null || !token.hasCaptcha()) {
      if(getNext() != null) {
        getNext().verifyCredential(token, anwser);
      }
      return;
    }
    
    if (!captchaChecker.isCorrect(token.getCaptcha())) { //验证未通过
      throw new CredentialException(SecureConstants.MSG_CAPTCHA_MISTAKE);
    } 
    //执行下一个验证
    if(getNext() != null) {
      getNext().verifyCredential(token, anwser);
    }
  }

  @Override
  public int getOrder() {
    return 0;
  }

}
