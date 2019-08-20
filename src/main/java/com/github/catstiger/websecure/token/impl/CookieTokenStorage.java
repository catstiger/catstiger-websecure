package com.github.catstiger.websecure.token.impl;

import java.util.Objects;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.cfg.AbstractSecurityConfigurable;
import com.github.catstiger.websecure.cfg.SecurityProperties;
import com.github.catstiger.websecure.token.TokenStorage;

/**
 * 用于在Cookie中存取Token
 *
 */
public class CookieTokenStorage extends AbstractSecurityConfigurable implements TokenStorage {
  private static Logger logger = LoggerFactory.getLogger(CookieTokenStorage.class);
  
  public CookieTokenStorage(SecurityProperties cfg) {
    super.setCfg(cfg);
  }
  
  @Override
  public void save(String token) {
    HttpServletResponse response = WebObjectsHolder.getResponse();

    Cookie cookie = new Cookie(SecureConstants.TOKEN_NAME_OF_COOKIE, token);
    cookie.setMaxAge(getCfg().getTokenExpirySec());
    cookie.setPath("/");

    response.addCookie(cookie);
  }

  @Override
  public void remove() {
    HttpServletResponse response = WebObjectsHolder.getResponse();

    Cookie cookie = new Cookie(SecureConstants.TOKEN_NAME_OF_COOKIE, StringUtils.EMPTY);
    cookie.setMaxAge(0);
    cookie.setPath("/");
    response.addCookie(cookie);
  }

  @Override
  public String get() {
    HttpServletRequest request = WebObjectsHolder.getRequest();

    Cookie[] cookies = request.getCookies();
    if (cookies != null) {
      for (int i = 0; i < cookies.length; i++) {
        if (Objects.equals(SecureConstants.TOKEN_NAME_OF_COOKIE, cookies[i].getName())) {
          String token = cookies[i].getValue();
          if (StringUtils.isNotBlank(token)) {
            logger.debug("从Cookie中获取Token {}", token);
          }
          return token;
        }
      }
    }
    return null;
  }

}
