package com.github.catstiger.websecure.login.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.catstiger.websecure.login.LoginException;
import com.github.catstiger.websecure.login.listener.LoginSuccessListener;
import com.github.catstiger.websecure.subject.Subject;
import com.github.catstiger.websecure.web.SecurityObjectsModel;

/**
 * Ajax方式登录
 * @author leesam
 *
 */
@Service
public class AjaxLoginService extends AbstractWebLoginService {
  @Autowired
  private List<LoginSuccessListener> loginSuccessListeners;
  
  @Override
  protected List<LoginSuccessListener> getLoginSuccessListeners() {
    return loginSuccessListeners;
  }

  @Override
  protected Object doLogout() {
    SecurityObjectsModel model = new SecurityObjectsModel(StringUtils.EMPTY, true);
    return model;
  }

  @Override
  public Object doSuccessed(Subject subject, String token) {
    SecurityObjectsModel model = new SecurityObjectsModel(token);
    
    return model;
  }

  @Override
  public Object doFailed(LoginException e) {
    SecurityObjectsModel model = new SecurityObjectsModel(e.getMessage(), false);
    
    return model;
  }

}
