package com.github.catstiger.websecure.user.service;

import java.util.Collection;
import java.util.List;

import com.github.catstiger.websecure.authc.Permission;
import com.github.catstiger.websecure.authc.PrincipalService;
import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.model.User;

public interface UserService extends PrincipalService {
  /**
   * 根据用户名获取User实例，不论User的状态如何（是否可用、是否锁定）。
   * <p>
   * {@code #byNameAnyway(String)}不使用缓存，因此每次都可以取得最新的实例。
   * </p>
   * @param name username
   * @return User of the name, {@code null} if not exists;
   */
  User getAnyway(String username);
  
  /**
   * 根据Mobile查询用户信息，不论User的状态如何（是否可用、是否锁定）。
   * @return
   */
  User byMobile(String mobile);
  
  /**
   * 根据用户名，查询User信息，如果存在，并且可用（enable and unlocked）,则返回User实例，否则返回null
   */
  User byNameQuietly(String username);
  
  /**
   * 根据用户ID，查询User信息，如果存在, 无论是否可用, 返回User实例，否则返回null
   */
  User byId(Long id);
  
  /**
   * 注册一个新的用户,加密密码，使他处于可以登录的状态
   * @param user 包含{@code username}、{@code password}等初始信息
   * @return 已经注册成功的User对象。
   * @throws RuntimeException if any error occurs.
   */
  User register(User user);
  
  /**
   * 注册一个新的用户,加密密码，赋予权限，使他处于可以登录的状态
   * @param user 包含{@code username}、{@code password}等初始信息
   * @param roles 赋予的角色名称：{@code Role#getName()}}
   * @return 已经注册成功的User对象。
   * @throws RuntimeException if any error occurs.
   */
  User register(User user, List<String> roles);
  
  /**
   * 锁定一个用户，被锁定的用户，不可以登录，也不可做任何“匿名用户”不能做的操作。
   * @param username 被锁定用户的{@code username}
   */
  void lock(String username);
  
  /**
   * 解锁一个用户，解锁之后，用户可以正常登录，可以做被授权的操作或者访问授权的资源
   * @param username 被解锁的用户的{@code username}
   */
  void unlock(String username);
  
  /**
   * 将一个用户设置为“可用”状态，用户可以登录，也可操作任何授权的行为和访问资源
   * @param username 被Enable用户的{@code username}
   */
  void enabled(String username);
  
  /**
   * 将一个用户设置为“不可用”状态，用户不可登录，也不可操作任何授权的行为和访问资源；
   * <p/>
   * 与{@code #lock(String)}操作不同，disable的用户必须手动操作才可以enable，而lock的用户可以自动unlock
   * @param username 被disable户的{@code username}
   */
  void disabled(String username);
  
  /**
   * 为一个用户分配一个角色，角色分配之后，此用户可以立即操作角色所被赋权的操作或者资源
   * @param username 被授权的用户
   * @param roleName 被授予的角色名称（可以唯一标识一个角色）
   * @return 授权之后的用户
   */
  User grant(String username, String roleName);
  
  /**
   * 撤销一个角色的授权，撤销授权之后，效果立即显现。
   * @param username 被撤销授权的用户
   * @param roleName 撤销的角色名称（可以唯一标识一个角色）
   * @return 撤销授权之后的用户
   */
  User revoke(String username, String roleName);
  
  /**
   * 判断一个用户是否能够操作或者访问给定的permission.
   * @param user 给出用于判断的用户
   * @param permission 给出用于判断的permission
   * @return 如果当前用户可以访问给定的Permission, 返回{@code true},否则返回{@code false}
   */
  boolean isPermitted(User user, String permission);
  
  /**
   * 判断一个用户是否能够操作或者访问给定的permission.
   * @param user 给出用于判断的用户
   * @param permission 给出用于判断的permission
   * @return 如果当前用户可以访问给定的Permission, 返回{@code true},否则返回{@code false}
   */
  boolean isPermitted(User user, Permission permission);
  
  /**
   * 返回一个用户具有的授权（Role/Authority）
   * @return 返回给定用户所具有的角色，如果没有任何角色（匿名用户），返回{@code Collections#emptySet()}
   */
  Collection<Role> getRolesByUser(User user);
  
  /**
   * 如果一个用户具有某个授权(Role/Authority),返回{@code true}, 否则返回{@code false}
   * @param user 给定用户
   * @param role 给定角色
   * @return 如果一个用户具有某个授权(Role/Authority),返回{@code true}, 否则返回{@code false}
   */
  boolean hasRole(User user, Role role);
  
  /**
   * 判断一个用户是否具有所有给定的授权(Role/Authority)
   * @param user 给定用户
   * @param roles 用于判断的授权(Role/Authority)
   * @return 如果用户具有全部给定的角色,返回{@code true}, 否则返回{@code false}
   */
  boolean hasAllRoles(User user, Collection<Role> roles);
  
  /**
   * 判断用户是否具有任意一个给定的授权(Role/Authority)
   * @param user 给定用户
   * @param roles 用于判断的授权(Role/Authority)
   * @return 如果用户具有任意一个授权，返回{@code true},否则返回{@code false}
   */
  boolean hasAnyRole(User user, Collection<Role> roles);
  
  /**
   * 修改用户的密码
   * @param username 给出用户
   * @param password 输入的新的密码
   * @param originalPassword 输入的原始密码，如果原始密码与用户当前密码不同，则抛出RuntimeException
   * @param confirmPassword 重复输入的密码，必须与输入的新密码相同
   * 
   */
  void changePassword(String username, String password, String originalPassword, String confirmPassword);

  /**
   * 直接修改用户密码，不考虑旧密码和重复验证密码
   * @param username 用户名
   * @param password 密码
   */
  void changePassword(String username, String password);
}
