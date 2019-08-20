package com.github.catstiger.websecure.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.catstiger.websecure.login.impl.WebFormLoginService;

@Controller 
@RequestMapping("/web_form_login")
public class WebFormLoginController {
  @Autowired
  private WebFormLoginService webForLoginService;
  
  /**
   * 登录，并重定向到缺省页面或者被拒页面
   */
  @RequestMapping("/login") 
  @ResponseBody
  public void login() {
    webForLoginService.login();
  }
  
  /**
   * 退出登录
   */
  @RequestMapping("/logout") 
  @ResponseBody
  public void logout() {
    webForLoginService.logout();
  }
}
