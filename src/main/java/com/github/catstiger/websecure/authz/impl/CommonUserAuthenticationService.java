package com.github.catstiger.websecure.authz.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.authz.AuthenticationService;
import com.github.catstiger.websecure.authz.AuthzToken;
import com.github.catstiger.websecure.cfg.SecurityProperties;
import com.github.catstiger.websecure.login.CredentialException;
import com.github.catstiger.websecure.password.PasswordEncoder;

@Component
public class CommonUserAuthenticationService implements AuthenticationService {
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
  public AuthzToken get() {
    HttpServletRequest request = WebObjectsHolder.getRequest();

    String host = request.getRemoteHost();
    String username = request.getParameter(SecureConstants.PARAMETER_USERNAME);
    if (username == null) {
      return new AnonymousAuthzToken(host);
    }
    String password = request.getParameter(SecureConstants.PARAMETER_PASSWORD);
    String strRem = request.getParameter(SecureConstants.PARAMETER_REMEMBERME);

    Boolean rememberMe = ("1".equals(strRem) || "on".equals(strRem) || Boolean.TRUE.toString().equals(strRem));

    return new UserAuthzToken(username, password, rememberMe, host);
  }

  @Override
  public void verifyCredential(AuthzToken token, Object anwser) {
    if (StringUtils.isBlank(token.getCredentials())
        || !passwordEncoder.matches(token.getCredentials(), (String) anwser)) {
      throw new CredentialException(SecureConstants.MSG_PASSWORD_MISTAKE);
    }
  }

  public SecurityProperties getConfig() {
    return config;
  }

  public void setConfig(SecurityProperties config) {
    this.config = config;
  }

}
