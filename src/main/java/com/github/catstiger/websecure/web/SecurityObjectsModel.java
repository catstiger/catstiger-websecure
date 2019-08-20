package com.github.catstiger.websecure.web;

/**
 * 通常用于在Ajax方式登录的时候，向客户端render登录成功与否的信息。
 * <p/>
 * @author leesam
 *
 */
public class SecurityObjectsModel {
  private String token;
  private String msg;
  private Boolean successed = true;

  /**
   * 构造登录状态对象
   * @param msg 提示信息
   * @param successed 是否登录成功
   */
  public SecurityObjectsModel(String msg, Boolean successed) {
    this.token = null;
    this.msg = msg;
    this.successed = successed;
  }

  /**
   * 构造登录成功的状态对象，输入token
   */
  public SecurityObjectsModel(String token) {
    this.token = token;
    this.msg = null;
    this.successed = true;
  }
  
  /**
   * 构造完整的登录状态对象
   * @param token token
   * @param msg 提示信息
   * @param successed 是否成功
   */
  public SecurityObjectsModel(String token, String msg, Boolean successed) {
    this.token = token;
    this.msg = msg;
    this.successed = successed;
  }

  /**
   * 返回用户名加密之后的Token
   */
  public String getToken() {
    return token;
  }
  
  
  public void setToken(String token) {
    this.token = token;
  }
  
  /**
   * 返回登录错误的信息，如果{@link #getSuccessed()}为{@code true}, 返回null
   */
  public String getMsg() {
    return msg;
  }
  
  public void setMsg(String msg) {
    this.msg = msg;
  }
  
  /**
   * 用户认证是否成功
   */
  public Boolean getSuccessed() {
    return successed;
  }
  

  public void setSuccessed(Boolean successed) {
    this.successed = successed;
  }
}
