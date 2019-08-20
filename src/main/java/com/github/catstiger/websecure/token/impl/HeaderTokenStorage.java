package com.github.catstiger.websecure.token.impl;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.cfg.AbstractSecurityConfigurable;
import com.github.catstiger.websecure.cfg.SecurityProperties;
import com.github.catstiger.websecure.token.TokenStorage;

/**
 * 用于从Http header中取得Token
 * 
 * @author leesam
 *
 */
public class HeaderTokenStorage extends AbstractSecurityConfigurable implements TokenStorage {
  private static Logger logger = LoggerFactory.getLogger(HeaderTokenStorage.class);
  
  public HeaderTokenStorage(SecurityProperties cfg) {
    super.setCfg(cfg);
  }

  @Override
  public void save(String token) {
    // 这个不存在Save的问题，客户端可以存储在LocalStorage中
    logger.warn("不可以保存header中的token");
  }

  @Override
  public void remove() {
    // 这个不存在Remove的问题，客户端可以自行删除Token
    logger.warn("不可以删除Header中的Token");
  }

  @Override
  public String get() {
    HttpServletRequest request = WebObjectsHolder.getRequest();
    
    String token = request.getHeader(SecureConstants.TOKEN_NAME_OF_HEADER);
    if (StringUtils.isNotBlank(token)) {
      logger.debug("从Header中获取Token");
    }

    return token;
  }

}
