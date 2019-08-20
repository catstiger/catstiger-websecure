package com.github.catstiger.websecure.user.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections4.CollectionUtils;
import org.redisson.api.RMapCache;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.authc.Permission;
import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.model.User;
import com.github.catstiger.websecure.web.SecurityJsService;
import com.github.catstiger.websecure.cfg.SecurityProperties;

@Component
public class RedisRBACache implements RBACache {
  private static Logger logger = LoggerFactory.getLogger(RedisRBACache.class);

  @Autowired
  private RedissonClient redissonClient;
  /**
   * 用于同步Evict Security JS
   */
  @Autowired
  private SecurityJsService securityJsService;
  
  @Autowired
  private SecurityProperties securityProps;

  private static final Map<String, Eviction> evictions = new ConcurrentHashMap<>(4);

  @Override
  public void putUser(User user) {
    if (user == null) {
      logger.warn("试图缓存null user");
      return;
    }

    RMapCache<Object, Object> cache = this.getUserCache();
    Eviction eviction = this.getUserEviction();
    cache.put(user.getUsername(), user, eviction.ttl, eviction.ttlUnit, eviction.maxIdleTime, eviction.maxIdleUnit);
  }

  @Override
  public User getUser(String username) {
    RMapCache<Object, Object> cache = this.getUserCache();
    return (User) cache.get(username);
  }

  @Override
  public void evictUser(String username) {
    RMapCache<Object, Object> cache = this.getUserCache();
    cache.remove(username);
    securityJsService.evictCache(username);
  }

  @Override
  public void clearUser() {
    RMapCache<Object, Object> cache = this.getUserCache();
    cache.clear();
    securityJsService.clearCache();
  }

  @Override
  public void putRolesOfUser(String username, Collection<Role> roles) {
    RMapCache<Object, Object> cache = this.getRolesOfUserCache();

    if (CollectionUtils.isEmpty(roles)) {
      if (cache.containsKey(username)) {
        cache.remove(username);
      }
    } else {
      Eviction eviction = this.getRolesOfUserEviction();
      cache.put(username, roles, eviction.ttl, eviction.ttlUnit, eviction.maxIdleTime, eviction.maxIdleUnit);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<Role> getRolesOfUser(String username) {
    RMapCache<Object, Object> cache = this.getRolesOfUserCache();

    if (cache.containsKey(username)) {
      return (Collection<Role>) cache.get(username);
    }
    return Collections.emptySet();
  }

  @Override
  public void evictRolesOfUser(String username) {
    RMapCache<Object, Object> cache = this.getRolesOfUserCache();
    cache.remove(username);
    securityJsService.evictCache(username);
  }

  @Override
  public void clearRolesOfUser() {
    RMapCache<Object, Object> cache = this.getRolesOfUserCache();
    cache.clear();
    securityJsService.clearCache();
  }

  @Override
  public void putResourcesOfRole(String roleName, Collection<Permission> resources) {
    RMapCache<Object, Object> cache = this.getResourcesOfRoleCache();

    if (CollectionUtils.isEmpty(resources)) {
      if (cache.containsKey(roleName)) {
        cache.remove(roleName);
      }
    } else {
      Eviction eviction = this.getResourcesOfRoleEviction();
      cache.put(roleName, resources, eviction.ttl, eviction.ttlUnit, eviction.maxIdleTime, eviction.maxIdleUnit);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<Permission> getResourcesOfRole(String roleName) {
    RMapCache<Object, Object> cache = this.getResourcesOfRoleCache();
    if (cache.containsKey(roleName)) {
      return (Collection<Permission>) cache.get(roleName);
    }
    return Collections.emptySet();
  }

  @Override
  public void evictResourcesOfRole(String roleName) {
    RMapCache<Object, Object> cache = this.getResourcesOfRoleCache();
    cache.remove(roleName);
    securityJsService.clearCache();
  }

  @Override
  public void clearResourcesOfRole() {
    RMapCache<Object, Object> cache = this.getResourcesOfRoleCache();
    cache.clear();
    securityJsService.clearCache();
  }

  @Override
  public void putResources(Collection<Permission> resources) {
    RMapCache<Object, Object> cache = this.getResourcesCache();
    if (CollectionUtils.isEmpty(resources) && cache.containsKey(SecureConstants.CACHE_KEY_CONFIGURABLE_RESOURCES)) {
      cache.remove(SecureConstants.CACHE_KEY_CONFIGURABLE_RESOURCES);
    } else {
      Eviction eviction = this.getResourcesEviction();
      cache.put(SecureConstants.CACHE_KEY_CONFIGURABLE_RESOURCES, resources, eviction.ttl, eviction.ttlUnit, eviction.maxIdleTime, eviction.maxIdleUnit);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<Permission> getResources() {
    RMapCache<Object, Object> cache = this.getResourcesCache();
    if (cache.containsKey(SecureConstants.CACHE_KEY_CONFIGURABLE_RESOURCES)) {
      return (Collection<Permission>) cache.get(SecureConstants.CACHE_KEY_CONFIGURABLE_RESOURCES);
    }
    return Collections.emptySet();
  }

  @Override
  public void clearResources() {
    RMapCache<Object, Object> cache = this.getResourcesCache();
    cache.clear();
    securityJsService.clearCache();
  }

  @Override
  public void clearAll() {
    logger.warn("清除全部RBAC缓存。");
    this.getResourcesCache().clear();
    this.getResourcesOfRoleCache().clear();
    this.getRolesOfUserCache().clear();
    this.getUserCache().clear();
    this.getViewsOfRowCache().clear();
    securityJsService.clearCache();
  }

  @Override
  public void putViews(String roleName, Collection<String> views) {
    RMapCache<Object, Object> cache = this.getViewsOfRowCache();
    if (CollectionUtils.isEmpty(views)) {
      if (cache.containsKey(roleName)) {
        cache.remove(roleName);
      }
    } else {
      Eviction eviction = this.getViewsOfRoleEviction();
      cache.put(roleName, views, eviction.ttl, eviction.ttlUnit, eviction.maxIdleTime, eviction.maxIdleUnit);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<String> getViews(String roleName) {
    RMapCache<Object, Object> cache = this.getViewsOfRowCache();
    if (cache.containsKey(roleName)) {
      return (Collection<String>) cache.get(roleName);
    }
    return Collections.emptySet();
  }

  @Override
  public void evictViews(String roleName) {
    RMapCache<Object, Object> cache = this.getViewsOfRowCache();
    cache.remove(roleName);
  }

  @Override
  public void clearViews() {
    RMapCache<Object, Object> cache = this.getViewsOfRowCache();
    cache.clear();
  }

  private RMapCache<Object, Object> getViewsOfRowCache() {
    return redissonClient.getMapCache(SecureConstants.CACHE_NAME_VIEWS_OF_ROLE);
  }

  private RMapCache<Object, Object> getUserCache() {
    return redissonClient.getMapCache(SecureConstants.CACHE_NAME_PRINCIPAL_BY_NAME);
  }

  private RMapCache<Object, Object> getResourcesOfRoleCache() {
    return redissonClient.getMapCache(SecureConstants.CACHE_NAME_PERMISSION_OF_AUTHORITY);
  }

  private RMapCache<Object, Object> getResourcesCache() {
    return redissonClient.getMapCache(SecureConstants.CACHE_NAME_CONFIGURABLE_RESOURCES);
  }

  private RMapCache<Object, Object> getRolesOfUserCache() {
    return redissonClient.getMapCache(SecureConstants.CACHE_NAME_AUTHORITY_OF_SUBJECT);
  }
  
  private Eviction newEviction(String name) {
    return new Eviction(name, securityProps.getCacheExpirySec(), securityProps.getCacheIdleSec());
  }

  private Eviction getUserEviction() {
    if (!evictions.containsKey(SecureConstants.CACHE_NAME_PRINCIPAL_BY_NAME)) {
      evictions.put(SecureConstants.CACHE_NAME_PRINCIPAL_BY_NAME, newEviction(SecureConstants.CACHE_NAME_PRINCIPAL_BY_NAME));
    }
    return evictions.get(SecureConstants.CACHE_NAME_PRINCIPAL_BY_NAME);
  }

  private Eviction getRolesOfUserEviction() {
    if (!evictions.containsKey(SecureConstants.CACHE_NAME_AUTHORITY_OF_SUBJECT)) {
      evictions.put(SecureConstants.CACHE_NAME_AUTHORITY_OF_SUBJECT, newEviction(SecureConstants.CACHE_NAME_AUTHORITY_OF_SUBJECT));
    }
    return evictions.get(SecureConstants.CACHE_NAME_AUTHORITY_OF_SUBJECT);
  }

  private Eviction getResourcesEviction() {
    if (!evictions.containsKey(SecureConstants.CACHE_NAME_CONFIGURABLE_RESOURCES)) {
      evictions.put(SecureConstants.CACHE_NAME_CONFIGURABLE_RESOURCES, newEviction(SecureConstants.CACHE_NAME_CONFIGURABLE_RESOURCES));
    }
    return evictions.get(SecureConstants.CACHE_NAME_CONFIGURABLE_RESOURCES);
  }

  private Eviction getResourcesOfRoleEviction() {
    if (!evictions.containsKey(SecureConstants.CACHE_NAME_PERMISSION_OF_AUTHORITY)) {
      evictions.put(SecureConstants.CACHE_NAME_PERMISSION_OF_AUTHORITY, newEviction(SecureConstants.CACHE_NAME_PERMISSION_OF_AUTHORITY));
    }
    return evictions.get(SecureConstants.CACHE_NAME_PERMISSION_OF_AUTHORITY);
  }

  private Eviction getViewsOfRoleEviction() {
    if (!evictions.containsKey(SecureConstants.CACHE_NAME_VIEWS_OF_ROLE)) {
      evictions.put(SecureConstants.CACHE_NAME_VIEWS_OF_ROLE, newEviction(SecureConstants.CACHE_NAME_VIEWS_OF_ROLE));
    }
    return evictions.get(SecureConstants.CACHE_NAME_VIEWS_OF_ROLE);
  }

  /**
   * 用于定义缓存过期时间和空闲时间
   *
   */
  private static final class Eviction {
    private long ttl;
    private TimeUnit ttlUnit = TimeUnit.SECONDS;
    private long maxIdleTime;
    private TimeUnit maxIdleUnit = TimeUnit.SECONDS;

    // 都使用统一的过期配置，可以根据应用情况调整
    private Eviction(String cacheName, long ttl, long maxIdleTime) {
      this.ttl = ttl;
      this.maxIdleTime = maxIdleTime;
    }
  }

}
