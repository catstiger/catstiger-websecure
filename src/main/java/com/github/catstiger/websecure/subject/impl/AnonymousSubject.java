package com.github.catstiger.websecure.subject.impl;

import java.util.Collection;
import java.util.List;

import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.websecure.authc.FreeAccessService;
import com.github.catstiger.websecure.authc.Permission;
import com.github.catstiger.websecure.authc.Principal;
import com.github.catstiger.websecure.authc.impl.AnonymousPrincipal;
import com.github.catstiger.websecure.subject.Subject;

/**
 * 匿名Subject，只能访问自由资源
 * 
 * @author leesam
 *
 */
@SuppressWarnings("serial")
public final class AnonymousSubject implements Subject {

  private FreeAccessService freeAccessService;
  private static AnonymousSubject instance = null;

  /**
   * 返回AnonymousSubject的实例
   * @param freeAccessService 自由访问服务，匿名用户只能访问free资源
   * @return
   */
  public static Subject getInstance(FreeAccessService freeAccessService) {
    if (instance != null) {
      return instance;
    }
    instance = new AnonymousSubject(freeAccessService);
    return instance;
  }

  private AnonymousSubject(FreeAccessService freeAccessService) {
    this.freeAccessService = freeAccessService;
  }

  @Override
  public Principal getPrincipal() {
    return AnonymousPrincipal.INSTANCE;
  }

  @Override
  public boolean isPermitted(String permission) {
    return freeAccessService.isFree(permission);
  }

  @Override
  public boolean isPermited(Permission permission) {
    return freeAccessService.isFree(permission.getPermission());
  }

  @Override
  public boolean hasRole(String roleIdentifier) {
    return false;
  }

  @Override
  public boolean hasAnyRoles(List<String> roleIdentifiers) {
    return false;
  }

  @Override
  public boolean hasAllRoles(Collection<String> roleIdentifiers) {
    return false;
  }

  @Override
  public boolean isAuthenticated() {
    return false;
  }

  @Override
  public String getHost() {
    return WebObjectsHolder.getRequest().getRemoteHost();
  }

  @Override
  public boolean isRememberMe() {
    return false;
  }

}
