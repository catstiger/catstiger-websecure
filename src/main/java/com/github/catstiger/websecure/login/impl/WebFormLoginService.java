package com.github.catstiger.websecure.login.impl;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.catstiger.common.util.Exceptions;
import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.login.LoginException;
import com.github.catstiger.websecure.login.listener.LoginSuccessListener;
import com.github.catstiger.websecure.subject.Subject;
import com.github.catstiger.websecure.web.RequestKeeper;
import com.github.catstiger.websecure.web.RequestModel;
import com.github.catstiger.websecure.web.impl.HttpSessionRequestKeeper;
import com.google.common.base.Charsets;

@Service
public class WebFormLoginService extends AbstractWebLoginService {
  
  @Autowired
  private List<LoginSuccessListener> loginSuccessListeners;
  
  @Override
  public Object doSuccessed(Subject subject, String token) {
    //处理重定向
    HttpServletRequest request = WebObjectsHolder.getRequest();
    HttpServletResponse response = WebObjectsHolder.getResponse();
    
    RequestKeeper requestKeeper = new HttpSessionRequestKeeper();
    RequestModel requestModel = requestKeeper.get(request, response);
    
    String url = config.getDefaultUrl(); //缺省URL
    if (requestModel != null) {
      url = requestModel.getRequestUrl(); //保存的URL，上次访问不成功的地址
      requestKeeper.remove(request, response);
    }
    try {
      redirectService.sendRedirect(request, response, url);
    } catch (IOException e) {
      e.printStackTrace();
      throw Exceptions.unchecked(e);
    }
    
    return null;
  }

  @Override
  public Object doFailed(LoginException e) {
    String url = config.getLoginForm(); //登录失败，要重定向到登录页面
    HttpServletRequest request = WebObjectsHolder.getRequest();
    HttpServletResponse response = WebObjectsHolder.getResponse();
    
    logger.info("认证失败 {}", e.getMessage());
    
    try {
      String msg = e.getMessage();
      if (msg == null) {
        msg = SecureConstants.MSG_PASSWORD_MISTAKE;
      }
      String msgBase64 = new String(Base64.encodeBase64(msg.getBytes(Charsets.UTF_8)));
      redirectService.sendRedirect(request, response, url + "?msg=" + msgBase64);
    } catch (IOException ex) {
      e.printStackTrace();
      throw Exceptions.unchecked(e);
    }
    
    return null;
  }
  
  @Override
  protected Object doLogout() {
    HttpServletRequest request = WebObjectsHolder.getRequest();
    HttpServletResponse response = WebObjectsHolder.getResponse();
   
    String url = config.getDefaultUrl(); //缺省URL
    try {
      redirectService.sendRedirect(request, response, url);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return true;
  }
  
  @Override
  public List<LoginSuccessListener> getLoginSuccessListeners() {
    return loginSuccessListeners;
  }
  
  public void setLoginSuccessListeners(List<LoginSuccessListener> loginSuccessListeners) {
    this.loginSuccessListeners = loginSuccessListeners;
  }

}
