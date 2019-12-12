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
  private Boolean success = true;

  /**
   * 构造登录状态对象
   * @param msg 提示信息
   * @param success 是否登录成功
   */
  public SecurityObjectsModel(String msg, Boolean success) {
    this.token = null;
    this.msg = msg;
    this.success = success;
  }

  /**
   * 构造登录成功的状态对象，输入token
   */
  public SecurityObjectsModel(String token) {
    this.token = token;
    this.msg = null;
    this.success = true;
  }
  
  /**
   * 构造完整的登录状态对象
   * @param token token
   * @param msg 提示信息
   * @param success 是否成功
   */
  public SecurityObjectsModel(String token, String msg, Boolean success) {
    this.token = token;
    this.msg = msg;
    this.success = success;
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
   * 返回登录错误的信息，如果{@link #getSuccess()}为{@code true}, 返回null
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
  public Boolean getSuccess() {
    return success;
  }
  

  public void setSuccess(Boolean successed) {
    this.success = successed;
  }
}
