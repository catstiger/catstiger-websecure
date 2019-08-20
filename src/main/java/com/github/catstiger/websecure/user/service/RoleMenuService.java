package com.github.catstiger.websecure.user.service;

import java.util.Collection;
import java.util.Set;

import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.model.User;

public interface RoleMenuService {
  /**
   * 保存/新增角色和UI元素的对应关系
   * @param role 角色，主要是ID
   * @param refViews UI元素，可以是一个菜单的ID或者一个JS文件等
   * @param corpId, 所属企业ID
   */
  void saveRoleMenu(Role role, String refViews, Long corpId);
  
  /**
   * 返回一个角色可以访问的UI元素
   * @param role 给定角色
   * @param corpId 所属企业ID
   */
  Collection<String> getRoleViews(Role role, Long corpId);
  
  /**
   * 返回一个用户可以访问的UI元素
   * @param user 用户
   * @param corpId 企业ID
   * @return
   */
  Set<String> getUserViews(User user, Long corpId);
}
