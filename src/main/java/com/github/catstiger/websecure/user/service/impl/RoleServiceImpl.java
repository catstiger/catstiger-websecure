package com.github.catstiger.websecure.user.service.impl;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.github.catstiger.common.sql.JdbcTemplateProxy;
import com.github.catstiger.common.sql.SQLReady;
import com.github.catstiger.common.sql.SQLRequest;
import com.github.catstiger.common.sql.id.IdGen;
import com.github.catstiger.common.util.Exceptions;
import com.github.catstiger.websecure.authc.Permission;
import com.github.catstiger.websecure.cache.SecureObjectsCache;
import com.github.catstiger.websecure.user.model.Resource;
import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.service.ResourceService;
import com.github.catstiger.websecure.user.service.RoleService;
import com.github.catstiger.websecure.user.service.RoleUtil;

@Service
public class RoleServiceImpl implements RoleService {
  @Autowired
  private JdbcTemplateProxy jdbcTemplate;
  @Autowired
  private ResourceService resourceService;
  @Autowired
  private SecureObjectsCache secureObjectsCache;
  @Autowired
  private IdGen idGen;
 

  
  @Override
  @Transactional(readOnly = true)
  public Role byName(String name) {
    Assert.notNull(name, "Role name must not be null.");
    SQLReady sqlReady = new SQLRequest(Role.class).usingAlias(true).select().append(" WHERE name=?", name);
    return queryRole(sqlReady);
  }

  @Override
  @Transactional
  public Role create(String name, String descn, Boolean isSys, Long corpId) {
    Assert.isTrue(StringUtils.isNotBlank(name), "角色名称不可为空。");
    
    Role role = new Role(name);
    role.setCorpId(corpId);
    
    String wrapName = RoleUtil.wrapName(role); // wrap name, 确保角色名称全局唯一
    Long c = jdbcTemplate.queryForObject("select count(*) from roles where name=?", Long.class, wrapName);

    if (c > 0L) {
      throw new IllegalArgumentException("角色名称" + name + "已经存在");
    }
    
    role.setName(wrapName);
    role.setId(idGen.nextId());
    role.setDescn(descn);
    role.setIsSys(isSys);
    

    SQLReady sqlReady = new SQLRequest(Role.class).entity(role).insert();
    jdbcTemplate.update(sqlReady.getSql(), sqlReady.getArgs());
    
    secureObjectsCache.clearPermissions(); // 清除全部资源缓存
    return role;
  }

  @Transactional
  public Role update(Long id, String name, String descn, Long corpId) {
    Assert.notNull(id, "角色ID不可为空");
    Assert.isTrue(StringUtils.isNotBlank(name), "角色名称不可为空。");
    Assert.notNull(corpId, "corpId不可为空");
    
    String wrapName = RoleUtil.wrapName(name, corpId);

    SQLReady sqlReady = new SQLRequest(Role.class).usingAlias(true).select().append(" WHERE id=?", id);
    Role role = queryRole(sqlReady);

    if (role == null) {
      throw new IllegalArgumentException("角色" + name + "不存在");
    }

    if (role.getIsSys()) {
      jdbcTemplate.update("update roles set descn=?, where id=?", descn, role.getId()); // 系统角色不能修改名称
    } else {
      Long c = jdbcTemplate.queryForObject("select count(*) from roles where name=? and id<>? ", Long.class, wrapName, role.getId());
      if (c > 0) {
        throw new IllegalArgumentException("角色" + name + "已经存在");
      }
      jdbcTemplate.update("update roles set descn=?, name=? where id=?", descn, wrapName, role.getId());
    }
    
    secureObjectsCache.clearPermissionsOfAuthority();
    secureObjectsCache.clearAuthoritiesOfPrincipal();
    
    role.setDescn(descn);
    role.setName(name);
    return role;
  }

  /**
   * 会导致缓存被完全清空！
   */
  @Transactional
  public void remove(String wrappedRoleName) {
    Role role = this.byName(wrappedRoleName);
    if (role == null) {
      return;
    }
    // 如果是系统角色则不删除
    if (role.getIsSys() != null && role.getIsSys()) {
      throw Exceptions.unchecked("不能删除系统角色！");
    }

    jdbcTemplate.update("delete from users_roles where roles_id=?", role.getId());
    jdbcTemplate.update("delete from roles_resources where roles_id=?", role.getId());
    jdbcTemplate.update("delete from roles where id=?", role.getId());

    // 删除角色，必须清空所有缓存！
    secureObjectsCache.clearAll();
  }

  @Override
  @Transactional(readOnly = true)
  public Collection<Permission> getPermissionsOfRole(Role role) {
    Assert.notNull(role, "Role must not be null.");
    Assert.notNull(role.getName(), "Role name must not be null.");
    Assert.notNull(role.getCorpId(), "CorpID不能为空");

    String wrapName = RoleUtil.wrapName(role);
    
    Collection<Permission> perms = secureObjectsCache.getPermissionsOfAuthority(wrapName);
    if (CollectionUtils.isNotEmpty(perms)) {
      return perms;
    }

    role = byName(wrapName);
    Assert.notNull(role, "角色不存在" + role.getName());

    List<Resource> resources = resourcesOfRole(role.getId());
    Set<Permission> permissions = new LinkedHashSet<>(resources.size()); // resources是经过排序的，因此这里可以用LinkedHashSet
    resources.forEach(r -> {
      Resource res = new Resource(r.getUrl());
      permissions.add(res);
    });
    secureObjectsCache.putPermissionsOfAuthority(wrapName, permissions); // 缓存角色的授权
    return permissions;
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasPermission(Role role, String permission) {
    Collection<Permission> permissions = getPermissionsOfRole(role);

    return permissions.contains(new Resource(permission));
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasAnyPermission(Role role, Collection<Permission> permissions) {
    Collection<Permission> perms = getPermissionsOfRole(role);
    return CollectionUtils.containsAny(perms, permissions);
  }

  @Override
  @Transactional(readOnly = true)
  public boolean hasAllPermissions(Role role, Collection<Permission> permissions) {
    Collection<Permission> perms = getPermissionsOfRole(role);
    return perms.containsAll(permissions);
  }

  @Override
  @Transactional
  public void grant(String roleName, String permission) {
    
    Role role = byName(roleName);
    Resource resource = resourceService.byUrl(permission);
    Long c = jdbcTemplate.queryForObject("select count(*) from roles_resources where roles_id=? and resources_id=?", Long.class, role.getId(),
        resource.getId());
    if (c == 0) {
      jdbcTemplate.update("insert into roles_resources(roles_id,resources_id) values (?,?)", role.getId(), resource.getId());

      secureObjectsCache.clearPermissions();
      secureObjectsCache.clearPermissionsOfAuthority();
    }
  }

  @Override
  @Transactional
  public void grant(String roleName, String[] permissions) {
    Role role = byName(roleName);
    jdbcTemplate.update("delete from roles_resources where roles_id=?", role.getId()); // 删除原有的授权

    if (permissions == null || permissions.length == 0) {
      return;
    }

    for (String permission : permissions) {
      Resource resource = resourceService.byUrl(permission);
      jdbcTemplate.update("insert into roles_resources(roles_id, resources_id) value (?,?)", role.getId(), resource.getId());
    }
    secureObjectsCache.clearPermissions();
    secureObjectsCache.clearPermissionsOfAuthority();
  }

  @Override
  @Transactional
  public void revoke(String roleName, String permission) {
    Role role = byName(roleName);
    Resource resource = resourceService.byUrl(permission);

    Long c = jdbcTemplate.queryForObject("select count(*) from roles_resources where roles_id=? and resources_id=?", Long.class, role.getId(),
        resource.getId());
    if (c > 0) {
      jdbcTemplate.update("delete from roles_resources where roles_id=? and resources_id=?", role.getId(), resource.getId());

      secureObjectsCache.clearPermissions();
      secureObjectsCache.clearPermissionsOfAuthority();
    }
  }

  private Role queryRole(SQLReady sqlReady) {
    return jdbcTemplate.queryForObject(sqlReady.getSql(), new BeanPropertyRowMapper<Role>(Role.class), sqlReady.getArgs());
  }

  /**
   * 返回角色被授权访问的{@link Resource}
   * 
   * @param roleId 角色ID
   * @return List of Resource or empty if no resources be granted to the role.
   */
  private List<Resource> resourcesOfRole(Long roleId) {
    return jdbcTemplate.query(
        "SELECT r.id id,r.url url,r.descn descn "
            + " FROM resources r INNER JOIN roles_resources rr ON(rr.resources_id=r.id) WHERE rr.roles_id=? ORDER BY r.url DESC", // 排序确保匹配最多的在最前面
        new BeanPropertyRowMapper<Resource>(Resource.class), roleId);
  }

}
