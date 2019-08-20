package com.github.catstiger.websecure.user.controller;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.github.catstiger.common.util.Exceptions;
import com.github.catstiger.common.web.controller.ModulesController;
import com.github.catstiger.websecure.annotation.SecureResource;
import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.model.User;
import com.github.catstiger.websecure.user.service.RoleMenuService;

@Controller
@RequestMapping("/role_menu")
public class RoleMenuController extends ModulesController {
  @Autowired
  private RoleMenuService roleMenuService;

  /**
   * 保存菜单权限
   */
  @SecureResource("保存菜单权限")
  @RequestMapping("/save_role_menu")
  @ResponseBody
  public Map<String, Object> saveRoleMenu(@RequestParam("roleId") Long roleId, @RequestParam("refViews") String refViews) {
    try {
      Role role = new Role();
      role.setId(roleId);
      roleMenuService.saveRoleMenu(role, refViews, getCorp().getId());
      return forExt(true);
    } catch (Exception e) {
      e.printStackTrace();
      return forExt("保存菜单权限失败！");
    }
  }

  /**
   * 返回一个角色可以访问的View
   */
  @RequestMapping("/role_views")
  @ResponseBody
  public Collection<String> roleViews(@RequestParam("roleId") Long roleId) {
    Role role = new Role();
    role.setId(roleId);

    return roleMenuService.getRoleViews(role, getCorp().getId());
  }
  
  /**
   * 根据当前用户，返回可访问的View
   */
  @RequestMapping("/user_views")
  @ResponseBody
  public Set<String> userViews() {
    User user = getUser();
    if (user == null) {
      throw Exceptions.unchecked("未登录");
    }
    
    return roleMenuService.getUserViews(user, getCorp().getId());
  }
}
