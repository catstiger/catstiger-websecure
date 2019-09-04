package com.github.catstiger.websecure.cfg;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;

/*

 */
@Component
@ConfigurationProperties(prefix = "app.security", ignoreUnknownFields = true)
public class SecurityProperties {
  private String salt;
  private String token;
  private Integer cacheExpirySec;
  private Integer cacheIdleSec;

  private Integer tokenExpirySec;
  
  private Boolean permitFreeResources = true;
  private Boolean permitSuperAdmin = true;
  private String userAdmin = "admin";
  private String roleAdmin = "administrator";

  private String defaultUrl;
  private String loginForm;
  
  private boolean recordLogin = false;
  private boolean syncLogin = false;
  
  private List<String> loginUrls;
  private List<String> staticResources;
  private List<String> freeResources;

  public String getSalt() {
    return salt;
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

  /**
   * @deprecated Use {@code com.github.catstiger.websecure.SecureConstants#TOKEN_SECURITY}
   */
  @Deprecated
  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public Integer getCacheExpirySec() {
    return cacheExpirySec;
  }

  public void setCacheExpirySec(Integer cacheExpirySec) {
    this.cacheExpirySec = cacheExpirySec;
  }

  public Integer getCacheIdleSec() {
    return cacheIdleSec;
  }

  public void setCacheIdleSec(Integer cacheIdleSec) {
    this.cacheIdleSec = cacheIdleSec;
  }

  public Integer getTokenExpirySec() {
    return tokenExpirySec;
  }

  public void setTokenExpirySec(Integer tokenExpirySec) {
    this.tokenExpirySec = tokenExpirySec;
  }
  
  public Boolean getPermitFreeResources() {
    return permitFreeResources;
  }

  public void setPermitFreeResources(Boolean permitFreeResources) {
    this.permitFreeResources = permitFreeResources;
  }
  
  public Boolean getPermitSuperAdmin() {
    return permitSuperAdmin;
  }

  public void setPermitSuperAdmin(Boolean permitSuperAdmin) {
    this.permitSuperAdmin = permitSuperAdmin;
  }


  public String getUserAdmin() {
    return userAdmin;
  }

  public void setUserAdmin(String userAdmin) {
    this.userAdmin = userAdmin;
  }

  public String getRoleAdmin() {
    return roleAdmin;
  }

  public void setRoleAdmin(String roleAdmin) {
    this.roleAdmin = roleAdmin;
  }

  public String getDefaultUrl() {
    return defaultUrl;
  }

  public void setDefaultUrl(String defaultUrl) {
    this.defaultUrl = defaultUrl;
  }

  public String getLoginForm() {
    return loginForm;
  }

  public void setLoginForm(String loginForm) {
    this.loginForm = loginForm;
  }

  public boolean isRecordLogin() {
    return recordLogin;
  }

  public void setRecordLogin(boolean recordLogin) {
    this.recordLogin = recordLogin;
  }

  public boolean isSyncLogin() {
    return syncLogin;
  }

  public void setSyncLogin(boolean syncLogin) {
    this.syncLogin = syncLogin;
  }
  
  public List<String> getLoginUrls() {
    return loginUrls;
  }

  public void setLoginUrls(List<String> loginUrls) {
    this.loginUrls = loginUrls;
  }

  public List<String> getStaticResources() {
    return staticResources;
  }

  public void setStaticResources(List<String> staticResources) {
    this.staticResources = staticResources;
  }

  public List<String> getFreeResources() {
    return freeResources;
  }

  public void setFreeResources(List<String> freeResources) {
    this.freeResources = freeResources;
  }

  @Override
  public String toString() {
    return JSON.toJSONString(this, SerializerFeature.PrettyFormat, SerializerFeature.WriteNullStringAsEmpty,
      SerializerFeature.WriteNullBooleanAsFalse, SerializerFeature.WriteNullNumberAsZero);
  }

}
