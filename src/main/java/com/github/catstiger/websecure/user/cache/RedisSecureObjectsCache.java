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
import com.github.catstiger.websecure.authc.Authority;
import com.github.catstiger.websecure.authc.Permission;
import com.github.catstiger.websecure.authc.Principal;
import com.github.catstiger.websecure.cache.SecureObjectsCache;
import com.github.catstiger.websecure.cfg.SecurityProperties;

@Component
public class RedisSecureObjectsCache implements SecureObjectsCache {
  private static Logger logger = LoggerFactory.getLogger(RedisSecureObjectsCache.class);

  @Autowired
  private RedissonClient redissonClient;
  
  @Autowired
  private SecurityProperties securityProps;
  
  private static final Map<String, Eviction> evictions = new ConcurrentHashMap<>(4);
  
  @Override
  public <T extends Principal> void putPrincipal(T principal) {
    if (principal == null) {
      logger.warn("试图缓存null principal");
      return;
    }

    RMapCache<Object, Principal> cache = getPrincipalCache();
    Eviction eviction = this.getUserEviction();
    cache.put(principal.getName(), principal, eviction.ttl, eviction.ttlUnit, eviction.maxIdleTime, eviction.maxIdleUnit);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <T extends Principal> T getPrincipal(String name) {
    RMapCache<Object, Principal> cache = getPrincipalCache();
    return (T) cache.get(name);
  }

  @Override
  public void evictPrincipal(String name) {
    RMapCache<Object, Principal> cache = getPrincipalCache();
    cache.remove(name);
  }

  @Override
  public void clearPrincipal() {
    RMapCache<Object, Principal> cache = getPrincipalCache();
    cache.clear();
  }

  @Override
  public void putAuthoritiesOfPrincipal(String name, Collection<? extends Authority> authorities) {
    RMapCache<Object, Object> cache = getAuthoritiesOfPrincipalCache();

    if (CollectionUtils.isEmpty(authorities)) {
      if (cache.containsKey(name)) {
        cache.remove(name);
      }
    } else {
      Eviction eviction = this.getAuthoritiesOfPrincipalEviction();
      cache.put(name, authorities, eviction.ttl, eviction.ttlUnit, eviction.maxIdleTime, eviction.maxIdleUnit);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<? extends Authority> getAuthoritiesOfPrincipal(String name) {
    RMapCache<Object, Object> cache = this.getAuthoritiesOfPrincipalCache();

    if (cache.containsKey(name)) {
      return (Collection<? extends Authority>) cache.get(name);
    }
    
    return Collections.emptySet();
  }

  @Override
  public void evictAuthoritiesOfPrincipal(String name) {
    RMapCache<Object, Object> cache = this.getAuthoritiesOfPrincipalCache();
    cache.remove(name);
  }

  @Override
  public void clearAuthoritiesOfPrincipal() {
    RMapCache<Object, Object> cache = this.getAuthoritiesOfPrincipalCache();
    cache.clear();
  }

  @Override
  public void putPermissionsOfAuthority(String authorityName, Collection<Permission> permissions) {
    RMapCache<Object, Object> cache = getPermissionsOfAuthorityCache();
    if (CollectionUtils.isEmpty(permissions)) {
      if (cache.containsKey(authorityName)) {
        cache.remove(authorityName);
      }
    } else {
      Eviction eviction = this.getPermissionsOfAuthorityEviction();
      cache.put(authorityName, permissions, eviction.ttl, eviction.ttlUnit, eviction.maxIdleTime, eviction.maxIdleUnit);
    }
  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<Permission> getPermissionsOfAuthority(String authorityName) {
    RMapCache<Object, Object> cache = getPermissionsOfAuthorityCache();
    if (cache.containsKey(authorityName)) {
      return (Collection<Permission>) cache.get(authorityName);
    }
    return Collections.emptySet();
  }

  @Override
  public void evictPermissionsOfAuthority(String authorityName) {
    RMapCache<Object, Object> cache = getPermissionsOfAuthorityCache();
    cache.remove(authorityName);
  }

  @Override
  public void clearPermissionsOfAuthority() {
    RMapCache<Object, Object> cache = getPermissionsOfAuthorityCache();
    cache.clear();
  }

  @Override
  public void putPermissions(Collection<Permission> permissions) {
    RMapCache<Object, Object> cache = getPermissionsCache();
    if (CollectionUtils.isEmpty(permissions) && cache.containsKey(SecureConstants.CACHE_KEY_CONFIGURABLE_RESOURCES)) {
      cache.remove(SecureConstants.CACHE_KEY_CONFIGURABLE_RESOURCES);
    } else {
      Eviction eviction = this.getResourcesEviction();
      cache.put(SecureConstants.CACHE_KEY_CONFIGURABLE_RESOURCES, permissions, eviction.ttl, eviction.ttlUnit, eviction.maxIdleTime, eviction.maxIdleUnit);
    }

  }

  @SuppressWarnings("unchecked")
  @Override
  public Collection<Permission> getPermissions() {
    RMapCache<Object, Object> cache = getPermissionsCache();
    return (Collection<Permission>) cache.get(SecureConstants.CACHE_KEY_CONFIGURABLE_RESOURCES);
  }

  @Override
  public void clearPermissions() {
    RMapCache<Object, Object> cache = getPermissionsCache();
    cache.clear();
  }

  @Override
  public void clearAll() {
    getPermissionsOfAuthorityCache().clear();
    getPermissionsCache().clear();
    getAuthoritiesOfPrincipalCache().clear();
    getPrincipalCache().clear();
  }
  
  private RMapCache<Object, Principal> getPrincipalCache() {
    return redissonClient.getMapCache(SecureConstants.CACHE_NAME_PRINCIPAL_BY_NAME);
  }
  
  private RMapCache<Object, Object> getAuthoritiesOfPrincipalCache() {
    return redissonClient.getMapCache(SecureConstants.CACHE_NAME_AUTHORITY_OF_SUBJECT);
  }
  
  private RMapCache<Object, Object> getPermissionsOfAuthorityCache() {
    return redissonClient.getMapCache(SecureConstants.CACHE_NAME_PERMISSION_OF_AUTHORITY);
  }
  
  private RMapCache<Object, Object> getPermissionsCache() {
    return redissonClient.getMapCache(SecureConstants.CACHE_NAME_CONFIGURABLE_RESOURCES);
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

  private Eviction getAuthoritiesOfPrincipalEviction() {
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

  private Eviction getPermissionsOfAuthorityEviction() {
    if (!evictions.containsKey(SecureConstants.CACHE_NAME_PERMISSION_OF_AUTHORITY)) {
      evictions.put(SecureConstants.CACHE_NAME_PERMISSION_OF_AUTHORITY, newEviction(SecureConstants.CACHE_NAME_PERMISSION_OF_AUTHORITY));
    }
    return evictions.get(SecureConstants.CACHE_NAME_PERMISSION_OF_AUTHORITY);
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
