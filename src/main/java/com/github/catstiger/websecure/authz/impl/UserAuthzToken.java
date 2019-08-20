package com.github.catstiger.websecure.authz.impl;

import java.io.Serializable;

import com.github.catstiger.websecure.authz.AuthzToken;

@SuppressWarnings("serial")
public class UserAuthzToken implements AuthzToken, Serializable {
  private String username;
  private String password;
  private Boolean rememberMe;
  private String host;

  public UserAuthzToken() {

  }

  public UserAuthzToken(String username, String password) {
    this.username = username;
    this.password = password;
  }

  /**
   * 
   * @param rememberMe 是否Remember ME
   */
  public UserAuthzToken(String username, String password, Boolean rememberMe) {
    this.username = username;
    this.password = password;
    this.rememberMe = rememberMe;
  }

  /**
   * 
   * @param host 登录地址
   */
  public UserAuthzToken(String username, String password, Boolean rememberMe, String host) {
    this.username = username;
    this.password = password;
    this.rememberMe = rememberMe;
    this.host = host;
  }

  @Override
  public String getPrincipal() {
    return getUsername();
  }

  @Override
  public String getCredentials() {
    return getPassword();
  }

  @Override
  public String getHost() {
    return host;
  }

  @Override
  public boolean isRememberMe() {
    return rememberMe;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public Boolean getRememberMe() {
    return rememberMe;
  }

  public void setRememberMe(Boolean rememberMe) {
    this.rememberMe = rememberMe;
  }

  public void setHost(String host) {
    this.host = host;
  }

  @Override
  public String toString() {
    return "UserAuthzToken [username=" + username + ", password=" + password + ", rememberMe=" + rememberMe + ", host=" + host + "]";
  }

}
