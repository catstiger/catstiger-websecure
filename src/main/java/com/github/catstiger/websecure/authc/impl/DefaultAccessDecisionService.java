package com.github.catstiger.websecure.authc.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.PathMatcher;

import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.authc.AccessDecisionService;
import com.github.catstiger.websecure.authc.AccessDeniedException;
import com.github.catstiger.websecure.authc.Authority;
import com.github.catstiger.websecure.authc.FreeAccessService;
import com.github.catstiger.websecure.authc.Permission;
import com.github.catstiger.websecure.authc.Principal;
import com.github.catstiger.websecure.cache.SecureObjectsCache;
import com.github.catstiger.websecure.user.model.Resource;
import com.github.catstiger.websecure.user.model.Role;

@Service
public class DefaultAccessDecisionService implements AccessDecisionService {
  private static Logger logger = LoggerFactory.getLogger(DefaultAccessDecisionService.class);

  @Autowired
  private JdbcTemplate jdbcTemplate;
  @Autowired
  private PathMatcher pathMatcher;
  @Autowired
  private FreeAccessService freeAccessService;
  @Autowired
  private SecureObjectsCache rbacache;

  @Override
  public void decide(Principal principal, String securedResource, Collection<Permission> configurablePermissions)
      throws AccessDeniedException {
    if (CollectionUtils.isEmpty(configurablePermissions)) {
      logger.debug("没有配置任何资源，统统放行。");
      return;
    }
    if (freeAccessService.isFree(securedResource) || freeAccessService.permitSuper(principal) || freeAccessService.isStatic(securedResource)) {
      return;
    }
    
    for (Iterator<Permission> itr = configurablePermissions.iterator(); itr.hasNext();) {
      Permission permission = itr.next();
      if (pathMatcher.match(permission.getPermission(), securedResource)) {
        if (permission.getAuthorities().isEmpty()) {  //如果资源未曾授权给任何用户，则放行
          return;
        }
        if (CollectionUtils.containsAny(permission.getAuthorities(), principal.getAuthorities())) {
          logger.debug("用户 {} 的角色 {} 匹配 {}", principal.getName(), principal.getAuthorities(),
              permission.getPermission());
          return;
        }
      }
    }
    logger.debug("用户{},无权访问{}", principal.getName(), securedResource);
    throw new AccessDeniedException(SecureConstants.MSG_ACCESS_DENIED);
  }

  @Override
  @Transactional(readOnly = true)
  public Collection<Permission> loadConfigurablePermissions() {
    Collection<Permission> permissions = rbacache.getPermissions();

    if (CollectionUtils.isNotEmpty(permissions)) {
      return permissions;
    }
    List<Resource> resources = jdbcTemplate.query("select id,descn,url from resources order by url desc", 
        new BeanPropertyRowMapper<Resource>(Resource.class));
    //将Resource转换为Permission
    List<Permission> perms = new ArrayList<Permission>(resources.size());
    resources.forEach(eachResource -> {
      Resource resource = new Resource(eachResource.getUrl());
      resource.setId(eachResource.getId());
      //可访问此资源的角色/Authority
      resource.setRoles(rolesOfResource(resource.getId()));
      resource.setAuthorities(authoritiesByResource(resource.getId()));
      
      perms.add(resource);
    });
    rbacache.putPermissions(perms);

    return perms;
  }
  
  private Set<Role> rolesOfResource(Long resId) {
    List<Role> results = jdbcTemplate.query("SELECT r.id id,r.name name,r.descn descn "
        + " FROM roles r INNER JOIN roles_resources rr ON(rr.roles_id=r.id) WHERE rr.resources_id=?;",
        new BeanPropertyRowMapper<Role>(Role.class), resId);
    Set<Role> roles = new HashSet<>(results.size());
    results.forEach(each -> {
      roles.add(each);
    });
    return roles;
  }
  
  private Set<Authority> authoritiesByResource(Long resId) {
    Set<Role> roles = this.rolesOfResource(resId);
    Set<Authority> authorities = new HashSet<>(roles.size());
    roles.forEach(r -> {
      authorities.add(new Role(r.getName()));
    });
    return authorities;
  }

}
