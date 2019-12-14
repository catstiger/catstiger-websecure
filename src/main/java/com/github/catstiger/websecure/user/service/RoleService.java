package com.github.catstiger.websecure.user.service;

import java.util.Collection;

import com.github.catstiger.websecure.authc.Permission;
import com.github.catstiger.websecure.user.model.Role;

public interface RoleService {
  /**
   * 根据Role Name得到Role对象，如果不存在，返回{@code null}
   * @param name Name of the role, 如果为{@code null}, 抛出IllegalArgumentException
   * @return Instance of Role, it has a id.
   */
  Role byName(String name);

  /**
   * 创建一个新的角色
   * @param name 角色名称，必须全局唯一
   * @param descn 角色描述
   * @param isSys 是否系统角色，如果是，则不能修改名称，不能删除角色
   * @return 创建的角色实例
   */
  Role create(String name, String descn, Boolean isSys, Long corpId);
  
  /**
   * 返回角色可以访问/操作的所有Permission，如果没有，返回{@code Collections#emptySet()}
   * <p/>
   * 返回的Permisson必须是经过排序的，例如:/users/detail/**要排在/users/**前面。
   * @param role 给定一个角色
   * @return 返回角色可以访问/操作的所有Permission，如果没有，返回{@code Collections#emptySet()}
   */
  Collection<Permission> getPermissionsOfRole(Role role);
  
  /**
   * 如果一个角色具有某个Permission，返回{@code true},否则返回{@code false}
   * @param role 给定一个角色
   * @param permission 用于判断的Permission的String表达方式
   * @return 如果一个角色具有某个Permission，返回{@code true},否则返回{@code false}
   */
  boolean hasPermission(Role role, String permission);
  
  /**
   * 如果一个角色具有任意一个给定的Permission，返回{@code true},否则返回{@code false}
   * @param role 给定一个角色
   * @param permissions 用于判断的Permissions的String表达方式
   * @return 如果一个角色具有任意一个给定的Permission，返回{@code true},否则返回{@code false}
   */
  boolean hasAnyPermission(Role role, Collection<Permission> permissions);
  
  /**
   * 如果一个角色具有全部给定的Permissions,返回{@code true},否则返回{@code false}
   * @param role 给定一个角色
   * @param permissions 用于判断的Permissions的String表达方式
   * @return 如果一个角色具有全部给定的Permissions,返回{@code true},否则返回{@code false}
   */
  boolean hasAllPermissions(Role role, Collection<Permission> permissions);
 
  /**
   * 将一个Permission授权给指定的角色
   * @param roleName 角色的名称
   * @param permission Permission的字符串表达
   */
  void grant(String roleName, String permission);
  
  /**
   * 批量授权，会覆盖原来的授权
   * @param roleName 角色名称
   * @param permissions URL代表的permission，如果为null或者空数组, 则忽略；
   */
  void grant(String roleName, String[] permissions);
  
  /**
   * 撤销一个角色的某个Permission授权
   * @param roleName 角色的名称
   * @param permission Permission的字符串表达
   */
  void revoke(String roleName, String permission);
}
