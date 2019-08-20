package com.github.catstiger.websecure.authz.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.authz.AuthenticationService;
import com.github.catstiger.websecure.authz.AuthenticationServiceProvider;

@Service
public class DefaultAuthenticationServiceProvider implements AuthenticationServiceProvider {
  private static Logger logger = LoggerFactory.getLogger(DefaultAuthenticationServiceProvider.class);
  
  @Autowired
  private CommonUserAuthenticationService commonUserAuthenticationService;
  @Autowired
  private VerifyCodeAuthenticationService verifyCodeAuthenticationService;
  
  /**
   * 根据{@link HttpServletRequest}是否带有参数{@link SecurityConfig#getParamVerifyCode()}, 决定是使用{@link VerifyCodeAuthenticationService}
   * 还是{@link CommonUserAuthenticationService}
   * @return 如果{@link HttpServletRequest}带有参数{@link SecurityConfig#getParamVerifyCode()}，返回{@link VerifyCodeAuthenticationService}，
   *     否则返回{@link CommonUserAuthenticationService}
   */
  public AuthenticationService getAuthenticationService() {
    HttpServletRequest request = WebObjectsHolder.getRequest();
    
    if (StringUtils.isNotBlank(request.getParameter(SecureConstants.PARAMETER_VERIFY_CODE))) {
      logger.info("验证码认证 {}", SecureConstants.PARAMETER_VERIFY_CODE);
      return verifyCodeAuthenticationService;
    } else {
      logger.info("密码认证 {}", SecureConstants.PARAMETER_PASSWORD);
      return commonUserAuthenticationService;
    }
  }
}
