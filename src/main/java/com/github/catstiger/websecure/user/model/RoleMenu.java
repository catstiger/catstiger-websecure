package com.github.catstiger.websecure.user.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.github.catstiger.common.sql.BaseEntity;
import com.github.catstiger.common.sql.annotation.Index;

/**
 * ExtJs菜单授权，在实际的应用中，控制菜单的可见性比控制接口URL资源的可访问性要更加实用
 * 
 * @author samlee
 *
 */
@Entity
@Table(name = "role_menu")
public class RoleMenu extends BaseEntity {
  private Long roleId;
  private String refView;
  private Long corpId;

  @Column(name = "role_id")
  @Index
  public Long getRoleId() {
    return roleId;
  }

  public void setRoleId(Long roleId) {
    this.roleId = roleId;
  }

  @Column(name = "ref_view", length = 160)
  public String getRefView() {
    return refView;
  }

  public void setRefView(String refView) {
    this.refView = refView;
  }

  @Column(name = "corp_id")
  @Index
  public Long getCorpId() {
    return corpId;
  }

  public void setCorpId(Long corpId) {
    this.corpId = corpId;
  }
}
