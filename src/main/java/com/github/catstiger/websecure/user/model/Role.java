package com.github.catstiger.websecure.user.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.catstiger.common.sql.BaseEntity;
import com.github.catstiger.websecure.authc.Authority;
import com.github.catstiger.websecure.authc.Permission;

@SuppressWarnings("serial")
@Entity
@Table(name = "roles")
public class Role extends BaseEntity implements Authority {
	private String name;
	private String descn;
	private Boolean isSys = false;
	/**
	 * 此角色能够访问的资源
	 */
	private Set<Resource> resources = new HashSet<Resource>(0);
	/**
	 * 具有此角色的用户
	 */
	private Set<User> users = new HashSet<User>(0);

	public Role() {

	}

	public Role(String name) {
		this.name = name;
	}

	@Override
	@Column(length = 60, columnDefinition = "varchar(60) not null unique")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	@Transient
	public Set<Permission> getPermissions() {
		Set<Resource> permitedResources = getResources();
		if (permitedResources == null) {
			return Collections.emptySet();
		}
		Set<Permission> perms = new HashSet<>(permitedResources.size());
		permitedResources.forEach(r -> {
			perms.add(r);
		});

		return perms;
	}

	public String getDescn() {
		return descn;
	}

	public void setDescn(String descn) {
		this.descn = descn;
	}

	@ManyToMany(targetEntity = Resource.class)
	@JsonIgnore
	@JSONField(serialize = false)
	public Set<Resource> getResources() {
		return resources;
	}

	/**
	 * @param resources the resources to set
	 */
	public void setResources(Set<Resource> resources) {
		this.resources = resources;
	}

	@JsonIgnore
	@JSONField(serialize = false)
	@ManyToMany(cascade = {}, fetch = FetchType.LAZY, mappedBy = "roles", targetEntity = User.class)
	public Set<User> getUsers() {
		return this.users;
	}

	public void setUsers(Set<User> users) {
		this.users = users;
	}

	@Column(name = "is_sys", columnDefinition = "tinyint default 0")
	public Boolean getIsSys() {
		return isSys;
	}

	public void setIsSys(Boolean isSys) {
		this.isSys = isSys;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
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
		Role other = (Role) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

}
