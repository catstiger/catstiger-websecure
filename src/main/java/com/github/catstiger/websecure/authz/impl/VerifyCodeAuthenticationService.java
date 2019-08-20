package com.github.catstiger.websecure.authz.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.catstiger.common.util.Exceptions;
import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.sms.service.SmsVerifyCodeService;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.authz.AuthenticationService;
import com.github.catstiger.websecure.authz.AuthzToken;
import com.github.catstiger.websecure.login.CredentialException;
import com.github.catstiger.websecure.user.model.User;
import com.github.catstiger.websecure.user.service.UserService;

@Service
public class VerifyCodeAuthenticationService implements AuthenticationService {
  private static Logger logger = LoggerFactory.getLogger(VerifyCodeAuthenticationService.class);

  @Autowired
  private SmsVerifyCodeService verifyCodeService;
  @Autowired
  private UserService userService;
  

  @Override
  public AuthzToken get() {
    HttpServletRequest request = WebObjectsHolder.getRequest();
    
    String mobile = request.getParameter(SecureConstants.PARAMETER_MOBILE);
    if (StringUtils.isBlank(mobile)) {
      request.getParameter(SecureConstants.PARAMETER_USERNAME);
    }
    String verifyCode = request.getParameter(SecureConstants.PARAMETER_VERIFY_CODE);
    if (StringUtils.isBlank(verifyCode)) {
      throw new CredentialException(SecureConstants.MSG_VERIFY_CODE_MISTAKE);
    }

    User user = userService.byAlias(mobile);
    if (user == null) {
      try {
        user = (User) userService.byName(mobile);
      } catch (Exception e) {
        logger.error(e.getMessage());
        return null;
      }
    }

    String strRem = request.getParameter(SecureConstants.PARAMETER_REMEMBERME);
    Boolean rememberMe = ("1".equals(strRem) || "on".equals(strRem) || Boolean.TRUE.toString().equals(strRem)); //1和“no”是checkbox提交的数据

    VerifyCodeAuthzToken authzToken = new VerifyCodeAuthzToken();
    authzToken.setHost(request.getRemoteHost());
    authzToken.setMobile(mobile);
    authzToken.setVerifyCode(verifyCode);
    authzToken.setUsername(user.getUsername());
    authzToken.setPassword(verifyCode);
    authzToken.setRememberMe(rememberMe);

    return authzToken;
  }

  @Override
  public void verifyCredential(AuthzToken token, Object anwser) throws CredentialException {
    if (token == null || !(token instanceof VerifyCodeAuthzToken)) {
      throw Exceptions.unchecked("错误的Token！");
    }

    VerifyCodeAuthzToken verifyCodeToken = (VerifyCodeAuthzToken) token;
    if (!verifyCodeService.isCorrect(verifyCodeToken.getMobile(), verifyCodeToken.getVerifyCode())) {
      throw new CredentialException(SecureConstants.MSG_VERIFY_CODE_MISTAKE);
    }
  }

}
