package com.github.catstiger.websecure.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.PathMatcher;

import com.github.catstiger.common.sql.JdbcTemplateProxy;
import com.github.catstiger.common.sql.SQLReady;
import com.github.catstiger.common.sql.SQLRequest;
import com.github.catstiger.common.sql.id.IdGen;
import com.github.catstiger.websecure.cache.SecureObjectsCache;
import com.github.catstiger.websecure.user.model.Resource;
import com.github.catstiger.websecure.user.service.ResourceInitializer;
import com.github.catstiger.websecure.user.service.ResourceService;

@Service
public class ResourceServiceImpl implements ResourceService {
  @Autowired
  private JdbcTemplateProxy jdbcTemplate;
  @Autowired
  private PathMatcher pathMatcher;
  @Autowired
  private SecureObjectsCache secureObjectsCache;
  @Autowired
  private IdGen idGen;
  @Autowired
  private ResourceInitializer resourceInitializer;
  

  // 唯一索引，不必缓存
  @Override
  @Transactional(readOnly = true)
  public Resource byUrl(String url) {
    Assert.notNull(url, "The url must not be null.");
    SQLReady sqlReady = new SQLRequest(Resource.class).usingAlias(true).select().append(" where url=? ", url);
    return queryResource(sqlReady);
  }

  @Override
  @Transactional
  public Resource create(String url, String descn) {
    Assert.notNull(url, "The url must not be null.");

    Long c = jdbcTemplate.queryForObject("select count(*) from resources where url=?", Long.class, url);

    if (c > 0L) {
      throw new IllegalArgumentException("URL " + url + "已经存在");
    }
    Resource resource = new Resource(url);
    resource.setDescn(descn);
    resource.setId(idGen.nextId());
    SQLReady insert = new SQLRequest(resource).insert();
    jdbcTemplate.update(insert.getSql(), insert.getArgs());
    secureObjectsCache.clearPermissions();// 清空缓存

    return resource;
  }

  @Override
  @Transactional
  public void remove(String url) {
    Resource resource = byUrl(url);
    if (resource == null) {
      return;
    }
    jdbcTemplate.update("delete from roles_resources where resources_id=?", resource.getId());
    jdbcTemplate.update("delete from resources where id=?", resource.getId());

    // 清空缓存
    secureObjectsCache.clearPermissions();
    secureObjectsCache.clearPermissionsOfAuthority();
  }

  @Override
  public boolean implies(Resource resource, String url) {
    Assert.notNull(resource, "Resource must not be null");
    Assert.notNull(url, "URL must not be null");

    return pathMatcher.match(resource.getUrl(), url);
  }

  private Resource queryResource(SQLReady sqlReady) {
    return jdbcTemplate.queryForObject(sqlReady.getSql(), new BeanPropertyRowMapper<Resource>(Resource.class), sqlReady.getArgs());
  }

  @Override
  @Transactional
  public void build() {
    List<Resource> resources = resourceInitializer.build();
    for (Resource parent : resources) {
      Long id = saveOrUpdate(parent);
      for (Resource child : parent.getChildren()) {
        child.setParentId(id);
        saveOrUpdate(child);
      }
    }
  }

  /**
   * 新增（如果不存在）或者更新（如果存在）一个资源
   */
  @Transactional
  public Long saveOrUpdate(Resource resource) {
    Resource exists = jdbcTemplate.queryForObject("select id,url,descn,parent_id parentId from resources where url=?",
        new BeanPropertyRowMapper<Resource>(Resource.class), resource.getUrl());
    Long id;
    if (exists != null) { // 如果存在则修改
      id = exists.getId();
      if (resource.getParentId() == null) {
        jdbcTemplate.update("update resources set descn=?, parent_id = null where id=?", resource.getDescn(), id);
      } else {
        jdbcTemplate.update("update resources set descn=?, parent_id = ? where id=?", resource.getDescn(), resource.getParentId(), id);
      }
    } else { // 不存在则insert
      id = idGen.nextId();
      resource.setId(id);
      SQLReady insert = new SQLRequest(resource).insert();
      jdbcTemplate.update(insert.getSql(), insert.getArgs());
    }

    return id;
  }

}
