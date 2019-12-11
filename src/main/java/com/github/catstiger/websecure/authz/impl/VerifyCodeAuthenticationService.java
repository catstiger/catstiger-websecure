package com.github.catstiger.websecure.authz.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.catstiger.common.util.Exceptions;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.authz.AuthzToken;
import com.github.catstiger.websecure.authz.VerifyCodeChecker;
import com.github.catstiger.websecure.login.CredentialException;

@Component
public class VerifyCodeAuthenticationService extends AbstractAuthenticationService implements InitializingBean {
  private static Logger logger = LoggerFactory.getLogger(VerifyCodeAuthenticationService.class);

  @Autowired(required = false)
  private VerifyCodeChecker verifyCodeChecker;
  
  @Override
  public void verifyCredential(AuthzToken token, Object anwser) throws CredentialException {
    if (token == null) {
      throw Exceptions.unchecked("错误的Token！");
    }

    //如果未提供验证器，或者未提供验证码
    if (verifyCodeChecker == null || !token.hasVerifyCode()) {
      if(getNext() != null) {
        getNext().verifyCredential(token, anwser);
      }
      return;
    }
    
    if (!verifyCodeChecker.isCorrect(token.getMobile(), token.getVerifyCode())) { //验证未通过
      throw new CredentialException(SecureConstants.MSG_VERIFY_CODE_MISTAKE);
    } 
    //执行下一个验证
    if(getNext() != null) {
      getNext().verifyCredential(token, anwser);
    }
  }

  @Override
  public void afterPropertiesSet() throws Exception {
    if (verifyCodeChecker == null) {
      logger.warn("未提供VerifyCodeChecker的实现，无法进行验证码登录。");
    }
  }

  @Override
  public int getOrder() {
    return 5;
  }

}
