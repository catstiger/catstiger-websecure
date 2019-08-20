package com.github.catstiger.websecure.user.model;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.collections4.CollectionUtils;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.catstiger.common.sql.BaseEntity;
import com.github.catstiger.websecure.authc.Authority;
import com.github.catstiger.websecure.authc.Permission;

/**
 * {@code Resource}对象代表了一个或者多个可以访问的操作或者资源，一个或者多个角色(Role/Authority)可以访问这个/这些资源。
 * 
 * @author leesam
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "resources")
public class Resource extends BaseEntity implements Permission, Comparable<Resource> {
  private String descn;
  private String url;
  private Long parentId;
  private boolean checked = false;
  /**
   * 父资源，通常是一个模块，例如/senior/*表示一个模块， /senior/index.htm表示模块里面的一个资源
   */
  private Resource parent;

  private List<Resource> children = new ArrayList<Resource>(0);
  /**
   * 能够访问此资源的角色
   */
  private Set<Role> roles = new HashSet<Role>(0);
  @Transient
  private Set<Authority> authorities = new HashSet<>(0);

  public Resource() {

  }

  public Resource(String url) {
    this.url = url;
  }

  @Column(name = "descn", length = 100)
  public String getDescn() {
    return descn;
  }

  public void setDescn(String descn) {
    this.descn = descn;
  }

  @Column(name = "url", length = 120, columnDefinition = "varchar(120) not null unique")
  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  @Column(name = "parent_id")
  public Long getParentId() {
    return parentId;
  }

  public void setParentId(Long parentId) {
    this.parentId = parentId;
  }

  @JsonIgnore
  @Transient
  @JSONField(serialize = false)
  public Resource getParent() {
    return parent;
  }

  public void setParent(Resource parent) {
    this.parent = parent;
  }

  @JsonIgnore
  @Transient
  @JSONField(serialize = false)
  public List<Resource> getChildren() {
    return children;
  }

  public void setChildren(List<Resource> children) {
    this.children = children;
  }

  /**
   * @return the roles
   */
  @JsonIgnore
  @JSONField(deserialize = false, serialize = false)
  @ManyToMany(targetEntity = Role.class, mappedBy = "resources")
  public Set<Role> getRoles() {
    return roles;
  }

  /**
   * @param roles the roles to set
   */
  public void setRoles(Set<Role> roles) {
    this.roles = roles;
  }

  @Override
  public Set<Authority> getAuthorities() {
    if (CollectionUtils.isEmpty(authorities)) {
      authorities = new HashSet<>(getRoles().size());
      getRoles().forEach(r -> {
        authorities.add(new Role(r.getName()));
      });
    }

    return authorities;
  }

  public void setAuthorities(Set<Authority> authorities) {
    this.authorities = authorities;
  }

  @Override
  @Transient
  @JsonIgnore
  @JSONField(deserialize = false, serialize = false)
  public String getPermission() {
    return url;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((url == null) ? 0 : url.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    Resource other = (Resource) obj;
    if (url == null) {
      if (other.url != null) {
        return false;
      }
    } else if (!url.equals(other.url)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    return url;
  }

  @Override
  public int compareTo(Resource o) {
    String other = null;
    if (o != null) {
      other = o.getUrl();
    }
    return -this.url.compareTo(other);
  }

  /**
   * 标记被选中的资源
   * 
   * @return the checked
   */
  @Transient
  public Boolean getChecked() {
    return checked;
  }

  /**
   * @param checked the checked to set
   */
  public void setChecked(Boolean checked) {
    this.checked = checked;
  }

}
