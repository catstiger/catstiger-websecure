package com.github.catstiger.websecure.user.service.impl;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.github.catstiger.common.sql.JdbcTemplateProxy;
import com.github.catstiger.common.sql.id.IdGen;
import com.github.catstiger.websecure.user.cache.RBACache;
import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.model.User;
import com.github.catstiger.websecure.user.service.RoleMenuService;
import com.github.catstiger.websecure.user.service.UserService;
import com.google.common.base.Splitter;

@Service
public class RoleMenuServiceImpl implements RoleMenuService {
  @Autowired
  private JdbcTemplateProxy jdbcTemplate;
  @Autowired
  private IdGen idGen;
  @Autowired
  private RBACache rbacache;
  //@Autowired
  //private SecurityJsService jsService;
  @Autowired
  private UserService userService;

  @Override
  @Transactional
  public void saveRoleMenu(Role role, String refViews, Long corpId) {
    Assert.notNull(role, "Role must not be null.");
    Assert.notNull(role.getId(), "Role id must not be null.");

    role = jdbcTemplate.get(Role.class, role.getId());
    Assert.notNull(role, "Role must not be null.");

    if (refViews == null) {
      refViews = StringUtils.EMPTY;
    }

    jdbcTemplate.update("delete from role_menu where corp_id=? and role_id = ?", corpId, role.getId()); // 删除原有的授权
    Long roleId = role.getId();
    Splitter.on(",").trimResults().split(refViews).forEach(rv -> {
      if (StringUtils.isNotBlank(rv)) {
        jdbcTemplate.update("INSERT INTO role_menu (id, role_id,ref_view, corp_id) VALUES (?,?,?,?)", idGen.nextId(), roleId, rv, corpId);
      }
    });
    
    String key = this.cacheKey(role, corpId);
    
    rbacache.evictViews(key);
    // 清空security js缓存, Comment Caused by: 暂不使用这种方式, By Sam
    //jsService.evictCacheByRole(key);
  }

  @Override
  @Transactional(readOnly = true)
  public Collection<String> getRoleViews(Role role, Long corpId) {
    Assert.notNull(role, "Role must not be null.");
    Assert.notNull(role.getId(), "Role id must not be null.");
    role = jdbcTemplate.get(Role.class, role.getId());
    Assert.notNull(role, "Role must not be null.");
    
    String key = this.cacheKey(role, corpId);

    Collection<String> views = rbacache.getViews(key);
    if (CollectionUtils.isEmpty(views)) {
      views = jdbcTemplate.queryForList("SELECT ref_view FROM role_menu WHERE corp_id=? and role_id=?", String.class, corpId, role.getId());
      rbacache.putViews(key, views);
    }

    return views;
  }

  @Override
  public Set<String> getUserViews(User user, Long corpId) {
    Collection<Role> roles = userService.getRolesByUser(user);

    Set<String> views = new TreeSet<>();
    for(Role role : roles) {
      Collection<String> viewsByRole = getRoleViews(role, corpId);
      views.addAll(viewsByRole);
    }
    
    return views;
  }
  
  private String cacheKey(Role role, Long corpId) {
    return new StringBuilder(role.getName()).append(":").append(corpId).toString();
  }

}
