package com.github.catstiger.websecure.user.model;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.catstiger.common.sql.BaseEntity;
import com.github.catstiger.common.sql.annotation.Index;
import com.github.catstiger.websecure.authc.Authority;
import com.github.catstiger.websecure.authc.Principal;

@Entity
@Table(name = "users")
public class User extends BaseEntity implements Principal {
  private static final long serialVersionUID = -7060358439282679847L;
  
  private String username;
	private String password;
	private String host;
	private Date registTime;
	private Date loginTime;
	private Boolean isEnabled;
	private Boolean isLocked;
	private Date lockTime;
	private Boolean isSys = false;
	private String mobile;
	private Long corpId;
	private Long operatorId;
	private String operator;
	private Date lastModified;
	private String realName;
	private Long deptId;
	private String deptName;
	/**
	 * 是否正在使用初始密码
	 */
	private Boolean useInitPass = false;

	/**
	 * 所具有的角色
	 */
	private Set<Role> roles = new HashSet<Role>(0);

	public User() {
	}

	public User(String username) {
		this.username = username;
	}

	public User(String username, String password) {
		this.username = username;
		this.password = password;
	}

	/**
	 * 根据用户名密码角色创建一个User对象
	 */
	public User(String username, String password, Set<Role> roles) {
		this.username = username;
		this.password = password;
		this.roles = roles;
	}

  @Column(length = 20)
  @Index(unique = true)
  public String getMobile() {
    return mobile;
  }

  public void setMobile(String mobile) {
    this.mobile = mobile;
  }


	@Column(length = 30, columnDefinition = "varchar(30) not null unique")
	@Index(unique = true)
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Column(length = 128)
	@JSONField(serialize = false)
	@JsonIgnore
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Column(length = 30)
	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Column(name = "regist_time")
	@JSONField(format = "yyyy-MM-dd")
	@JsonFormat(pattern = "yyyy-MM-dd")
	public Date getRegistTime() {
		return registTime;
	}

	public void setRegistTime(Date registTime) {
		this.registTime = registTime;
	}

	@Column(name = "login_time")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getLoginTime() {
		return loginTime;
	}

	public void setLoginTime(Date loginTime) {
		this.loginTime = loginTime;
	}

	@Column(name = "is_enabled", columnDefinition = "tinyint default 1")
	public Boolean getIsEnabled() {
		return isEnabled;
	}

	public void setIsEnabled(Boolean isEnabled) {
		this.isEnabled = isEnabled;
	}

	@ManyToMany(targetEntity = Role.class)
	@JsonIgnore
	@JSONField(serialize = false)
	public Set<Role> getRoles() {
		return this.roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	@Column(name = "is_sys", columnDefinition = "tinyint default 0")
	public Boolean getIsSys() {
		return isSys;
	}

	public void setIsSys(Boolean isSys) {
		this.isSys = isSys;
	}

	@Column(name = "is_locked", columnDefinition = "tinyint default 0")
	public Boolean getIsLocked() {
		return isLocked;
	}

	public void setIsLocked(Boolean isLocked) {
		this.isLocked = isLocked;
	}

	@Column(name = "lock_time")
	@JSONField(format = "yyyy-MM-dd HH:mm:ss")
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	public Date getLockTime() {
		return lockTime;
	}

	public void setLockTime(Date lockTime) {
		this.lockTime = lockTime;
	}

	@Column(name = "corp_id")
	@Index
  public Long getCorpId() {
    return corpId;
  }

  public void setCorpId(Long corpId) {
    this.corpId = corpId;
  }

  @Column(name = "operator_id")
  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }

  @Column(name = "operator")
  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  @JSONField(format = "yyyy-MM-dd HH:mm")
  @Column(name = "last_modified")
  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }
  

  @Column(name = "real_name", length = 30)
  public String getRealName() {
    return realName;
  }

  public void setRealName(String realName) {
    this.realName = realName;
  }
  
  @Column(name = "dept_id")
  @Index
  public Long getDeptId() {
    return deptId;
  }

  public void setDeptId(Long deptId) {
    this.deptId = deptId;
  }

  @Column(name = "dept_name", length = 30)
  public String getDeptName() {
    return deptName;
  }

  public void setDeptName(String deptName) {
    this.deptName = deptName;
  }
  

  @Column(name = "use_init_pass", columnDefinition = "tinyint default 0")
  public Boolean getUseInitPass() {
    return useInitPass;
  }

  public void setUseInitPass(Boolean useInitPass) {
    this.useInitPass = useInitPass;
  }
  
	@Override
	@Transient
	public String getName() {
		return username;
	}

	@Override
	@Transient
	public Set<Authority> getAuthorities() {
		Set<Role> userRoles = getRoles();
		if (userRoles == null) {
			return Collections.emptySet();
		}
		Set<Authority> authorities = new HashSet<>(userRoles.size());
		userRoles.forEach(r -> {
			authorities.add(new Role(r.getName()));
		});

		return authorities;
	}

	@Override
	public String toString() {
		return username;
	}

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((username == null) ? 0 : username.hashCode());
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
    User other = (User) obj;
    if (username == null) {
      if (other.username != null) {
        return false;
      }
    } else if (!username.equals(other.username)) {
      return false;
    }
    return true;
  }

}
