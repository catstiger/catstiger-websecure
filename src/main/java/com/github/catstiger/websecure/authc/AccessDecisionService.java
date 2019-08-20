package com.github.catstiger.websecure.authc;

import java.util.Collection;


public interface AccessDecisionService {
  /**
   * 根据参数，判断一个资源是否可以被给定的{@code Principal}访问
   * @param principal 调用此方法的{@code Principal}，不可为{@code null}
   * @param securedResource 试图访问的资源
   * @param configurablePermissions 所有被管理的Permission/URL/Resource
   *
   * @throws AccessDeniedException 如果访问被拒，或者权限不足
   */
  void decide(Principal principal, String securedResource, Collection<Permission> configurablePermissions) throws AccessDeniedException;
  
  /**
   * 加载所有配置的Permission/URL/Resource
   */
  Collection<Permission> loadConfigurablePermissions();
}
