package com.github.catstiger.websecure.authc;

public interface FreeAccessService {
  /**
   * 判断一个许可，是否可以被任何人访问，包括匿名用户, 自由资源也是被权限系统控制的资源，会创建相应的{@link Subject}对象
   * @param permission 许可，通常是一个URL资源
   * @return
   */
  boolean isFree(String permission);
  
  /**
   * 判断一个资源是否是静态资源，静态资源系统直接放行，不会执行访问控制逻辑，不会创建{@link Subject}对象
   * @param uri 当前URI
   * @return
   */
  boolean isStatic(String uri);
  
  /**
   * 如果系统允许超级用户（具有角色{@link com.github.catstiger.websecure.cfg.SecurityConfig#getRoleAdmin()}}）访问任何资源,返回{@code true}
   * @param principal 当前登录的Principal/User
   * @return 如果当前用户是超级用户，并且系统允许超级用户访问任何资源，返回{@code true}, 否则返回{@code false}
   */
  boolean permitSuper(Principal principal);
}
