package com.github.catstiger.websecure.token.impl;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.github.catstiger.common.util.Exceptions;
import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.cfg.AbstractSecurityConfigurable;
import com.github.catstiger.websecure.cfg.SecurityProperties;
import com.github.catstiger.websecure.token.TokenStorage;
import com.github.catstiger.websecure.web.SecurityObjectsModel;

public class RequestParameterTokenStorage extends AbstractSecurityConfigurable implements TokenStorage {
  private static Logger logger = LoggerFactory.getLogger(RequestParameterTokenStorage.class);
  
  public RequestParameterTokenStorage(SecurityProperties cfg) {
    super.setCfg(cfg);
  }
  
  @Override
  public void save(String token) {
    HttpServletResponse response = WebObjectsHolder.getResponse();
    
    SecurityObjectsModel model = new SecurityObjectsModel(token);
    try {
      response.getWriter().write(JSON.toJSONString(model));
    } catch (IOException e) {
      e.printStackTrace();
      throw Exceptions.unchecked(e);
    }
  }

  @Override
  public void remove() {
    logger.warn("不可删除，只能由客户端删除此信息");
  }

  @Override
  public String get() {
    HttpServletRequest request = WebObjectsHolder.getRequest();
    String token = request.getParameter(SecureConstants.TOKEN_NAME_OF_PARAMETER);
    if (StringUtils.isNotBlank(token)) {
      logger.debug("从Request中获取Token");
    }
    return token;
  }

}
