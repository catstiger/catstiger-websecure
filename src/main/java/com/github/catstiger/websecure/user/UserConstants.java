package com.github.catstiger.websecure.user;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.model.User;

public final class UserConstants {
	/**
	 * 系统管理员角色
	 */
	public static final String ROLE_NAME_ADMIN = "administrator";

	public static final String ADMIN_USER = "admin";

	private static Role sysRole(String roleName, String descn) {
		Role role = new Role();
		role.setName(roleName);
		role.setDescn(descn);
		role.setIsSys(true);
		return role;
	}

	private static User sysUser(String username, String mobile, String password, Role... roles) {
		User user = new User(username, password);
		user.setPassword(password);
		user.setMobile(mobile);
		user.setIsEnabled(true);
		user.setIsLocked(false);
		user.setIsSys(true);
		user.setRegistTime(DateTime.now().toDate());

		Set<Role> rs = user.getRoles();
		if (roles != null) {
			for (Role role : roles) {
				rs.add(role);
			}
		}

		return user;
	}

	public static final Role ROLE_ADMIN = sysRole(ROLE_NAME_ADMIN, "系统管理员角色");
	/**
	 * 系统角色列表
	 */
	public static final List<Role> SYS_ROLES = new ArrayList<Role>();
	public static final List<User> SYS_USERS = new ArrayList<User>();



	static {
		SYS_ROLES.add(ROLE_ADMIN);
		SYS_USERS.add(sysUser(ADMIN_USER, "", "manager", SYS_ROLES.toArray(new Role[] {})));
	}

	private UserConstants() {
	}
}
