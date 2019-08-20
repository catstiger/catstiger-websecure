package com.github.catstiger.websecure.authc.impl;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.PathMatcher;

import com.github.catstiger.websecure.authc.Authority;
import com.github.catstiger.websecure.authc.FreeAccessService;
import com.github.catstiger.websecure.authc.Principal;
import com.github.catstiger.websecure.cfg.SecurityProperties;

@Service
public class FreeAccessServiceImpl implements FreeAccessService {
  private static Logger logger = LoggerFactory.getLogger(FreeAccessServiceImpl.class);
  /**
   * 缓存静态资源，不必每次都判断了
   */
  private static final Map<String, String> staticResources = new ConcurrentHashMap<>();
  /**
   * 缓存自由资源，不必每次都判断了
   */
  private static final Map<String, String> freeResourcesCache = new ConcurrentHashMap<>();

  @Autowired
  private SecurityProperties config;
  @Autowired
  private PathMatcher pathMatcher;

  @Override
  public boolean isFree(String permission) {
    if (freeResourcesCache.containsKey(permission)) {
      return true;
    }

    // loginForm
    if (config.getLoginForm() != null) {
      if (pathMatcher.match(config.getLoginForm() + "**", permission)) {
        freeResourcesCache.put(permission, config.getLoginForm() + "**");
        return true;
      }
    }

    // 判断是否是访问login处理URL
    List<String> loginUrls = config.getLoginUrls();
    if (CollectionUtils.isNotEmpty(loginUrls)) {
      for (String url : loginUrls) {
        if (pathMatcher.match(url, permission)) {
          logger.debug("login url访问 {}", url);
          freeResourcesCache.put(permission, url);
          return true;
        }
      }
    }

    // 判断定义在Configuration中的资源，通常是静态资源
    List<String> patterns = config.getFreeResources();
    if (CollectionUtils.isNotEmpty(patterns)) {
      for (String pattern : patterns) {
        if (pathMatcher.match(pattern, permission)) {
          logger.debug("自由资源访问 {}", pattern);
          freeResourcesCache.put(permission, pattern);
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public boolean isStatic(String uri) {
    if (StringUtils.isBlank(uri)) {
      return false;
    }
    if (staticResources.containsKey(uri)) {
      return true;
    }

    List<String> staticRes = config.getStaticResources();
    if (CollectionUtils.isNotEmpty(staticRes)) {
      for (String pattern : staticRes) {
        if (pathMatcher.match(pattern, uri)) {
          staticResources.put(uri, pattern);
          return true;
        }
      }
    }

    return false;
  }

  @Override
  public boolean permitSuper(Principal principal) {
    // 判断超级用户
    if (config.getPermitSuperAdmin() != null && config.getPermitSuperAdmin()) {
      for (Authority auth : principal.getAuthorities()) {
        if (StringUtils.equals(config.getRoleAdmin(), auth.getName())) {
          return true;
        }
      }
    }
    return false;
  }

}
