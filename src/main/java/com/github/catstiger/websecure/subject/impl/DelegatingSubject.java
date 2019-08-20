package com.github.catstiger.websecure.subject.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.catstiger.common.web.WebObjectsHolder;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.authc.Permission;
import com.github.catstiger.websecure.authc.Principal;
import com.github.catstiger.websecure.authz.AuthenticationService;
import com.github.catstiger.websecure.authz.AuthzToken;
import com.github.catstiger.websecure.authz.impl.AnonymousAuthzToken;
import com.github.catstiger.websecure.login.LoginException;
import com.github.catstiger.websecure.subject.Subject;
import com.github.catstiger.websecure.user.model.Role;
import com.github.catstiger.websecure.user.model.User;
import com.github.catstiger.websecure.user.service.UserService;

@SuppressWarnings("serial")
public class DelegatingSubject implements Subject {
	private static Logger logger = LoggerFactory.getLogger(DelegatingSubject.class);

	private UserService userService;
	private AuthenticationService tokenProvider;
	private AuthzToken authzToken;
	private String host;
	private User user;
	private boolean isAuthenticated = false;
	private boolean isRememberMe = false;

	/**
	 * 根据给定的UserService 和 AuthzToken，创建一个DelegatingSubject，获取当前请求的 Principal信息
	 */
	public DelegatingSubject(UserService userService, AuthzToken authzToken) {
		this.userService = userService;
		this.authzToken = authzToken;
		if (authzToken == null || AnonymousAuthzToken.isAnonymous(authzToken)) {
			throw new LoginException(SecureConstants.MSG_USER_NOT_FOUND);
		}
		this.host = this.authzToken.getHost();
		this.isRememberMe = this.authzToken.isRememberMe();
		logger.debug("Create Subject {}", this.authzToken);
		this.getPrincipal(); // We have to call this to setup user
	}

	@Override
	public Principal getPrincipal() {
		if (user == null) {
			try {
				user = (User) getUserService().byName(authzToken.getPrincipal());
			} catch (Exception e) {
				throw new LoginException(e.getMessage());
			}
		}

		return user;
	}

	@Override
	public boolean isPermitted(String permission) {
		return userService.isPermitted(user, permission);
	}

	@Override
	public boolean isPermited(Permission permission) {
		return userService.isPermitted(user, permission);
	}

	@Override
	public boolean hasRole(String roleIdentifier) {
		return userService.hasRole(user, new Role(roleIdentifier));
	}

	@Override
	public boolean hasAnyRoles(List<String> roleIdentifiers) {
		if (CollectionUtils.isEmpty(roleIdentifiers)) {
			logger.warn("没有提供任何可供参考的角色");
			return true;
		}
		List<Role> roles = new ArrayList<>(roleIdentifiers.size());
		roleIdentifiers.forEach(r -> {
			roles.add(new Role(r));
		});
		return userService.hasAnyRole(user, roles);
	}

	@Override
	public boolean hasAllRoles(Collection<String> roleIdentifiers) {
		if (CollectionUtils.isEmpty(roleIdentifiers)) {
			logger.warn("没有提供任何可供参考的角色");
			return true;
		}
		List<Role> roles = new ArrayList<>(roleIdentifiers.size());
		roleIdentifiers.forEach(r -> {
			roles.add(new Role(r));
		});
		return userService.hasAllRoles(user, roles);
	}

	@Override
	public boolean isAuthenticated() {
		return this.isAuthenticated;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public AuthenticationService getTokenProvider() {
		return tokenProvider;
	}

	public void setTokenProvider(AuthenticationService tokenProvider) {
		this.tokenProvider = tokenProvider;
	}

	public void setAuthenticated(boolean authenticated) {
		this.isAuthenticated = authenticated;
	}

	@Override
	public String getHost() {
		return (this.host == null) ? WebObjectsHolder.getRequest().getRemoteHost() : this.host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	@Override
	public boolean isRememberMe() {
		return isRememberMe;
	}

	public void setRememberMe(boolean rememberMe) {
		this.isRememberMe = rememberMe;
	}

}
