package com.github.catstiger.websecure.web;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.common.web.WebUtil;
import com.github.catstiger.common.web.controller.BaseController;
import com.github.catstiger.common.web.controller.ModulesController;

@Controller
@RequestMapping("/security_js")
public class SecurityJsController extends ModulesController {
  @Autowired
  private SecurityJsService securityJsService;

  /**
   * 渲染一段js脚本，用于在js中判断角色、权限、获取登录信息等
   */
  @RequestMapping("/js")
  @ResponseBody
  public void js() {
    HttpServletResponse response = WebObjectsHolder.getResponse();
    WebUtil.setNoCacheHeader(response); // 不可缓存
    if (response != null) {
      response.setContentType("application/x-javascript");
      response.setCharacterEncoding("UTF-8");
    }

    BaseController.renderText(securityJsService.js(true, getCorp().getId()));
  }
}
