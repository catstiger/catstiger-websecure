package com.github.catstiger.websecure.cache;

import java.util.Collection;
import java.util.Collections;

import com.github.catstiger.websecure.authc.Authority;
import com.github.catstiger.websecure.authc.Permission;
import com.github.catstiger.websecure.authc.Principal;
import com.github.catstiger.websecure.user.model.Resource;
import com.github.catstiger.websecure.user.model.Role;

public interface SecureObjectsCache {

  /**
   * 向缓存中写入一个Principal对象（not null）,Key为{@link Principal#getName()}
   * @param user 要写入缓存的User对象
   */
  <T extends Principal> void putPrincipal(T principal);
  
  /**
   * 从缓存中根据{@link Principal#getName()}取得一个{@link Principal}对象，如果没有，返回{@code null}
   * @param name the cache key is {@link Principal#getName()}
   * @return 如果缓存中存在此对象,返回{@link Principal}的实例，否则返回{@code null}
   */
  <T extends Principal> T getPrincipal(String name);
  
  /**
   * 根据指定的name, 清除缓存中的User对象
   * @param name 缓存Key，{@link Principal#getName()}
   */
  void evictPrincipal(String name);
  
  /**
   * 清除缓存中所有的{@link Principal}对象
   */
  void clearPrincipal();
  
  /**
   * 缓存某个{@link Principal}所有的授权{@link Authority}
   * @param name 缓存Key，{@link Principal#getName()}
   * @param authorities {@link Principal}所具有的角色，如果为{@code null}或者empty，则删除缓存中的数据
   */
  void putAuthoritiesOfPrincipal(String name, Collection<? extends Authority> authorities);
  
  /**
   * 根据指定的{@link Principal#getName()}，返回该Principal所具有的Authority，如果没有，返回{@link Collections#emptySet()}
   * @param name 给出用户名
   * @return 返回该用户所具有的角色，如果没有，返回{@link Collections#emptySet()}
   */
  Collection<? extends Authority> getAuthoritiesOfPrincipal(String name);
  
  /**
   * 清除缓存中某个Principal所具有的Authority
   * @param name {@link Principal#getName()}
   */
  void evictAuthoritiesOfPrincipal(String name);
  
  /**
   * 清除缓存中所有的Principal所具有的Authority
   */
  void clearAuthoritiesOfPrincipal();
  
  /**
   * 将某个角色{@link Role}所被授权的访问的{@link Resource}装入缓存
   * @param authorityName Authority名称，缓存Key
   * @param resources 角色被授权的资源，如果为{@code null}或者empty，则清除此角色的资源
   */
  void putPermissionsOfAuthority(String authorityName, Collection<Permission> permissions);
  
  /**
   * 返回缓存中角色所具有的资源，如果没有返回{@link java.util.Collections#emptySet()}
   * @param authorityName Authority名称，缓存Key
   * @return 缓存中角色所具有的资源，如果没有返回{@link java.util.Collections#emptySet()}
   */
  Collection<Permission> getPermissionsOfAuthority(String authorityName);
  
  /**
   * 清除缓存中某个角色所被授权的访问的{@link Permission}
   * @param authorityName Authority名称，缓存Key
   */
  void evictPermissionsOfAuthority(String authorityName);
  
  /**
   * 清除缓存中所有Authority的Permission
   */
  void clearPermissionsOfAuthority();
  
  /**
   * 缓存所有已定义的Permission，需要将资源对应的Authority一并缓存
   * @param permissions 系统中所有已经定义的Permission以及资源对应的Authority
   */
  void putPermissions(Collection<Permission> permissions);
  
  /**
   * 返回系统中所有已定义的资源
   */
  Collection<Permission> getPermissions();
  
  /**
   * 清除所有缓存中的Permission
   */
  void clearPermissions();
  
  /**
   * 清除所有缓存中的，关于安全的对象
   */
  void clearAll();
}
