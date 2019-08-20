package com.github.catstiger.websecure.subject;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import com.github.catstiger.websecure.authc.Permission;
import com.github.catstiger.websecure.authc.Principal;


/**
 * 一个{@code Subject}代表了一个单个用户的security操作。包括认证 (login/logout)，授权（访问控制）等。
 * @author samlee
 */
public interface Subject extends Serializable {
  /**
   * 返回本Subject代表的，应用程序范围内唯一的可以标识的principal，如果是匿名访问，或者没有登录，返回 {@code null} 
   * @return this Subject's application-specific unique identity.
   */
  Principal getPrincipal();
  
  /**
   * 如果Subject允许执行某个操作或者访问某个资源，返回 {@code true}
   * @param permission 用于表达一个Permission的String
   * @return 如果当前Subject授权访问permission，返回{@code true}，否则返回{@code false}
   * @see #isPermitted(Permission permission)
   */
  boolean isPermitted(String permission);
  
  /**
   * 如果Subject允许执行某个操作或者访问某个资源，返回 {@code true}
   * @param permission A Permission that is being checked.
   * @return 如果当前Subject授权访问permission，返回{@code true}，否则返回{@code false}
   */
  boolean isPermited(Permission permission);
  
  /**
   * 如果当前Subject拥有某个特定的role，返回 {@code true}, 否则返回 {@code false}
   *
   * @param roleIdentifier Role indentifier
   * @return 如果当前Subject拥有某个特定的role，返回 {@code true}, 否则返回 {@code false}
   */
  boolean hasRole(String roleIdentifier);

  /**
   * 如果当前Subject拥有任意一个给定的角色，返回 {@code true} ，否则返回{@code false} 
   * @param roleIdentifiers 用于检查的角色名称或者角色ID的列表
   * @return 如果当前Subject拥有任意一个给定的角色，返回 {@code true} ，否则返回{@code false} 
   */
  boolean hasAnyRoles(List<String> roleIdentifiers);
  
  /**
   * 如果当前Subject拥有所有给定的角色，返回 {@code true} ，否则返回{@code false} 
   * @param roleIdentifiers 用于检查的角色名称或者角色ID的列表
   * @return 如果当前Subject拥有所有给定的角色，返回 {@code true} ，否则返回{@code false} 
   */
  boolean hasAllRoles(Collection<String> roleIdentifiers);
  
  /**
   * 如果当前Subject/User已经认证成功，则返回{@code true}，否则返回{@code false}
   * <p/>
   * 
   * @return 如果当前Subject/User已经认证成功，则返回{@code true}，否则返回{@code false}
   */
  boolean isAuthenticated();
  
  /**
   * 返回发起请求的网络地址，相当于调用{@code HttpServletRequest#getRemoteHost()}
   */
  String getHost();
  
  /**
   * 是否是Remember Me登录，或者，是否是从Cookie中获取Token登录信息
   */
  boolean isRememberMe();
}
