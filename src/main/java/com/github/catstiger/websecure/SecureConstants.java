package com.github.catstiger.websecure;

public final class SecureConstants {
  /**
   * Cookie或者Token加密的"Salt",用于JWTSinger
   */
  public static final String TOKEN_SECURITY = "__catstiger__";
  /**
   * JWT加密的Map对象的KEY
   */
  public static final String JWT_SIGN_MAP_KEY = "au_";
  
  /**
   * 根据Name查询{@code Principal}的Cache Name
   */
  public static final String CACHE_NAME_PRINCIPAL_BY_NAME = "PRINCIPAL_BY_NAME__";
  
  /**
   * 授权给某个Subject的Authoriy的缓存Name
   */
  public static final String CACHE_NAME_AUTHORITY_OF_SUBJECT = "AUTHORITY_OF_SUBJECT__";
  
  /**
   * 某个Auhtority可以访问的{@code Permission}的缓存NAME
   */
  public static final String CACHE_NAME_PERMISSION_OF_AUTHORITY = "PERMISSION_OF_AUTHORITY__";
  
  /**
   * 用于缓存所有配置的资源的CacheName
   */
  public static final String CACHE_NAME_CONFIGURABLE_RESOURCES = "CONFIGURABLE_RESOURCES__";
  
  /**
   * resources缓存Key
   */
  public static final String CACHE_KEY_CONFIGURABLE_RESOURCES = "configurable_resources";
  
  /**
   * 缓存名称，用于缓存角色和UI资源的对应关系
   */
  public static final String CACHE_NAME_VIEWS_OF_ROLE = "VIEWS_OF_ROLE";
  
  /**
   * Principal可以保存在{@code HttpSession}中，{@code SESSION_KEY_OF_PRINCIPAL}就是这个KEY值。
   */
  public static final String SESSION_KEY_OF_PRINCIPAL = "SESSION_KEY_OF_PRINCIPAL__";
  
  /**
   * Principal可以保存在Cookie中，{@code COOKIE_NAME_OF_PRINCIPAL}就是Cookie的名字
   */
  public static final String COOKIE_NAME_OF_PRINCIPAL = "COOKIE_NAME_OF_PRINCIPAL__";
  
  
  /**
   * 加密后的Token在Session、Cookie等保存的KEY
   */
  public static final String NAME_OF_HTTPSESSION = "TOKEN_IN_SESSION_";

  
  public static final String MSG_USER_NOT_FOUND = "用户名或密码错误";
  public static final String MSG_PASSWORD_MISTAKE = "用户名或密码错误";
  public static final String MSG_ACCESS_DENIED = "您的权限不足";
  public static final String MSG_ACCOUNT_EXPIRED = "您的账户已经过期";
  public static final String MSG_ACCOUNT_LOCKED = "您的账户被锁定，请联系管理员";
  public static final String MSG_ACCOUNT_DISABLED = "您的账户被禁用，请联系管理员";
  public static final String MSG_VERIFY_CODE_MISTAKE = "手机验证码错误";
  public static final String MSG_CAPTCHA_MISTAKE = "验证码错误";
  
  /**
   * Token在Http Request HEADER中的名字
   */
  public static final String TOKEN_NAME_OF_HEADER = "authorization";
  
  /**
   * Token在Http Request的参数名字
   */
  public static final String TOKEN_NAME_OF_PARAMETER = "sec_";
  
  /**
   * Token在Cookie中的名字
   */
  public static final String TOKEN_NAME_OF_COOKIE = "__authorization__";
  
  /**
   * Form表单传递的HttpServletRequest的参数名称
   */
  public static final String PARAMETER_USERNAME = "username";
  
  /**
   * Form表单传递的HttpServletRequest的参数名称
   */
  public static final String PARAMETER_PASSWORD = "password";
  
  public static final String PARAMETER_VERIFY_CODE = "vcode";
  
  public static final String PARAMETER_CAPTCHA = "captcha";
  
  /**
   * RememberMe请求的参数名称
   */
  public static final String PARAMETER_REMEMBERME = "rememberMe";
  /**
   * 手机号参数名
   */
  public static final String PARAMETER_MOBILE = "mobile";

  //业务相关
  
  /**
   * 缺省密码
   */
  public static final String DEFAULT_PWD = "123456";

  private SecureConstants() {}

}
