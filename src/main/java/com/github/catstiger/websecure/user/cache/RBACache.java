package com.github.catstiger.websecure.user.cache;

import java.util.Collection;
import java.util.Collections;

import com.github.catstiger.websecure.authc.Permission;
import com.github.catstiger.websecure.user.model.Resource;
import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.model.User;

/**
 * User\Role\Resource的缓存接口
 * @author samlee
 *
 */
public interface RBACache {

  /**
   * 向缓存中写入一个User对象（not null）,Key为{@link User#getUsername()}
   * @param user 要写入缓存的User对象
   */
  void putUser(User user);
  
  /**
   * 从缓存中根据username取得一个User对象，如果没有，返回{@code null}
   * @param username the cache key is {@link User#getUsername()}
   * @return 如果缓存中存在此对象,返回User的实例，否则返回{@code null}
   */
  User getUser(String username);
  
  /**
   * 根据指定的username, 清除缓存中的User对象
   * @param username 缓存Key，{@link User#getUsername()}
   */
  void evictUser(String username);
  
  /**
   * 清除缓存中所有的User对象
   */
  void clearUser();
  
  /**
   * 缓存某个User所有授权的角色{@link Role}
   * @param username 缓存Key，{@link User#getUsername()}
   * @param roles User所具有的角色，如果为{@code null}或者empty，则删除缓存中的数据
   */
  void putRolesOfUser(String username, Collection<Role> roles);
  
  /**
   * 根据指定的用户名，返回该用户所具有的角色，如果没有，返回{@link Collections#emptySet()}
   * @param username 给出用户名
   * @return 返回该用户所具有的角色，如果没有，返回{@link Collections#emptySet()}
   */
  Collection<Role> getRolesOfUser(String username);
  
  /**
   * 清除缓存中某个用户所具有的角色
   * @param username 用户名
   */
  void evictRolesOfUser(String username);
  
  /**
   * 清除缓存中所有的用户的角色
   */
  void clearRolesOfUser();
  
  /**
   * 将某个角色{@link Role}所被授权的访问的{@link Resource}装入缓存
   * @param roleName 角色名称，缓存Key
   * @param resources 角色被授权的资源，如果为{@code null}或者empty，则清除此角色的资源
   */
  void putResourcesOfRole(String roleName, Collection<Permission> resources);
  
  /**
   * 返回缓存中角色所具有的资源，如果没有返回{@link java.util.Collections#emptySet()}
   * @param roleName 角色名称，缓存Key
   * @return 缓存中角色所具有的资源，如果没有返回{@link java.util.Collections#emptySet()}
   */
  Collection<Permission> getResourcesOfRole(String roleName);
  
  /**
   * 清除缓存中某个角色所被授权的访问的{@link Resource}
   * @param roleName 角色名称，缓存Key
   */
  void evictResourcesOfRole(String roleName);
  
  /**
   * 清除缓存中所有角色的资源
   */
  void clearResourcesOfRole();
  
  /**
   * 缓存所有已定义的资源，需要将资源对应的角色一并缓存
   * @param resources 系统中所有已经定义的资源以及资源对应的角色
   */
  void putResources(Collection<Permission> resources);
  
  /**
   * 返回系统中所有已定义的资源
   */
  Collection<Permission> getResources();
  
  /**
   * 缓存角色和UI资源的对应关系，通常用于菜单权限设置
   * @param roleName 角色名称
   * @param views UI资源，ID，JS名称等
   */
  void putViews(String roleName, Collection<String> views);
  
  /**
   * 获取缓存中角色可以访问的UI资源
   * @param roleName 角色名称
   * @return UI资源
   */
  Collection<String> getViews(String roleName);
  
  /**
   * 清除缓存中角色对应的UI资源
   */
  void evictViews(String roleName);
  
  /**
   * 清除缓存中所有角色对应的UI资源
   */
  void clearViews();
  
  /**
   * 清除所有缓存中的资源
   */
  void clearResources();
  
  /**
   * 清除所有缓存中的，关于安全的对象
   */
  void clearAll();
}
