package com.github.catstiger.websecure.token.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.cfg.AbstractSecurityConfigurable;
import com.github.catstiger.websecure.cfg.SecurityProperties;
import com.github.catstiger.websecure.token.TokenStorage;

/**
 * 用于从HttpSession总存取Token
 * 
 * @author leesam
 *
 */
public class HttpSessionTokenStorage extends AbstractSecurityConfigurable implements TokenStorage {
  private Logger logger = LoggerFactory.getLogger(HttpSessionTokenStorage.class);

  public HttpSessionTokenStorage(SecurityProperties cfg) {
    super.setCfg(cfg);
  }
  
  @Override
  public void save(String token) {
    HttpServletRequest request = WebObjectsHolder.getRequest();
    HttpSession session = request.getSession(true);
    if (session != null) {
      logger.debug("保存Token到Session {}", token);
      session.setAttribute(SecureConstants.NAME_OF_HTTPSESSION, token);

    }
  }

  @Override
  public String get() {
    HttpServletRequest request = WebObjectsHolder.getRequest();
    HttpSession session = request.getSession(true);
    if (session != null) {
      String token = (String) session.getAttribute(SecureConstants.NAME_OF_HTTPSESSION);
      if (StringUtils.isNotBlank(token)) {
        logger.debug("从Session中获取Token");
      }
      return token;
    }
    return null;
  }

  @Override
  public void remove() {
    HttpServletRequest request = WebObjectsHolder.getRequest();
    HttpSession session = request.getSession(true);
    if (session != null) {
      session.invalidate();
    }
  }

}
