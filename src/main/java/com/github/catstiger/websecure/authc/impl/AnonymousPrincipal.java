package com.github.catstiger.websecure.authc.impl;

import java.util.Collections;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.github.catstiger.websecure.authc.Authority;
import com.github.catstiger.websecure.authc.Principal;
import com.github.catstiger.websecure.user.model.User;

/**
 * 匿名用户
 * 
 * @author leesam
 *
 */
@SuppressWarnings("serial")
public final class AnonymousPrincipal extends User implements Principal {
	public static final String ANONYMOUS_NAME = StringUtils.EMPTY;

	public static final AnonymousPrincipal INSTANCE = new AnonymousPrincipal();

	private AnonymousPrincipal() {
		super();
	}

	private String name = ANONYMOUS_NAME;

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Set<Authority> getAuthorities() {
		return Collections.emptySet();
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
		AnonymousPrincipal other = (AnonymousPrincipal) obj;
		if (name == null) {
			if (other.name != null) {
				return false;
			}
		} else if (!name.equals(other.name)) {
			return false;
		}
		return true;
	}

}
