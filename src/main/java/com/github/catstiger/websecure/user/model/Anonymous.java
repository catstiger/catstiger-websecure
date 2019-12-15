package com.github.catstiger.websecure.user.model;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

import com.github.catstiger.websecure.authc.Authority;
import com.github.catstiger.websecure.authc.Principal;

/**
 * Anonimous代表了一个匿名用户，由于是“匿名的”
 * 
 * @author samlee
 *
 */
public final class Anonymous implements Principal, Serializable {
  private static final long serialVersionUID = 1910901081853086142L;

  public static final String ANONYMOUS_NAME = "@ANONYMOUS_USER_";

	public static final Anonymous INSTANCE = new Anonymous();

	private Anonymous() {
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
		Anonymous other = (Anonymous) obj;
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
		return "Anonymous [name=" + name + "]";
	}

}
