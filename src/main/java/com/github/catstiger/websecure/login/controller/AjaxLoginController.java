package com.github.catstiger.websecure.login.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.github.catstiger.common.web.controller.BaseController;
import com.github.catstiger.websecure.login.impl.AjaxLoginService;

@Controller
@RequestMapping("/ajax_login")
public class AjaxLoginController {
  @Autowired
  private AjaxLoginService ajaxLoginService;

  /**
   * 登录，并重定向到缺省页面或者被拒页面
   */
  @RequestMapping("/login")
  @ResponseBody
  public void login() {
    Object object = ajaxLoginService.login();
    if (object != null) { // Instance of SecurityObjectsModel
      BaseController.renderJson(JSON.toJSONString(object));
    }
  }

  /**
   * 退出登录
   */
  @RequestMapping("/logout")
  @ResponseBody
  public void logout() {
    ajaxLoginService.logout();
  }
}
