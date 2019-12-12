package com.github.catstiger.websecure.authz.impl;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.authz.AuthzToken;
import com.github.catstiger.websecure.authz.AuthzTokenExtractor;

@Component
public class AuthzTokenExtractorImpl implements AuthzTokenExtractor {

  @Override
  public AuthzToken get() {
    HttpServletRequest request = WebObjectsHolder.getRequest();

    String host = request.getRemoteHost();
    String username = request.getParameter(SecureConstants.PARAMETER_USERNAME);
    String mobile = request.getParameter(SecureConstants.PARAMETER_MOBILE);
    
    if (username == null && mobile == null) {
      return new AnonymousAuthzToken(host);
    }
    
    String password = request.getParameter(SecureConstants.PARAMETER_PASSWORD);
    String strRem = request.getParameter(SecureConstants.PARAMETER_REMEMBERME);

    Boolean rememberMe = ("1".equals(strRem) || "on".equals(strRem) || Boolean.TRUE.toString().equals(strRem));
    String verifyCode = request.getParameter(SecureConstants.PARAMETER_VERIFY_CODE);
    String captcha = request.getParameter(SecureConstants.PARAMETER_CAPTCHA);
    
    UserAuthzToken authToken = new UserAuthzToken();
    authToken.setHost(host);
    authToken.setUsername(username);
    authToken.setMobile(mobile);
    authToken.setPassword(password);
    authToken.setRememberMe(rememberMe);
    authToken.setVerifyCode(verifyCode);
    
    authToken.setCaptcha(captcha);
    
    return authToken;
  }
  
}
