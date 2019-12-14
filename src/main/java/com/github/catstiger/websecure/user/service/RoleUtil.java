package com.github.catstiger.websecure.user.service;

import org.springframework.util.Assert;

import com.github.catstiger.websecure.user.model.Role;

public final class RoleUtil {
  public static final String ROLE_NAME_SPLITTER = "@";
  /**
   * 对角色名称进行编码，以确保角色的唯一性
   * @param role Instance of Role
   * @return decorated roles
   */
  public static String wrapName(Role role) {
    Assert.notNull(role, "role must not be null");
    Assert.notNull(role.getName(), "Role name must not be null");
    Assert.notNull(role.getCorpId(), "Corp id of the role must not be null");
    
    return wrapName(role.getName(), role.getCorpId());
  }
  
  /**
   * Wrap 角色名称：roleName@wrapper
   */
  public static String wrapName(String roleName, Object wrapper) {
    Assert.hasLength(roleName, "");
    
    if (roleName.indexOf(wrapper.toString()) >=0 ) {
      return roleName;
    }
    
    return roleName + ROLE_NAME_SPLITTER + wrapper.toString();
  }
  
  /**
   * 恢复角色名称，与{@code #wrapName(Role)} 相反
   * @return 角色名称
   */
  public static String unwrapName(Role role) {
    Assert.notNull(role, "role must not be null");
    Assert.notNull(role.getName(), "Role name must not be null");
    Assert.notNull(role.getCorpId(), "Corp id of the role must not be null");
    
    return unwrapName(role.getName());
  }
  
  /**
   * 恢复角色名称， 与{@code #wrapName(String, Object)} 相反
   */
  public static String unwrapName(String roleName) {
    int index = roleName.indexOf(ROLE_NAME_SPLITTER);
    if (index >= 0) {
      return roleName.substring(0, roleName.lastIndexOf(ROLE_NAME_SPLITTER));
    }
    return roleName;
  }
  
  public static void main(String[] args) {
    
  }
}
