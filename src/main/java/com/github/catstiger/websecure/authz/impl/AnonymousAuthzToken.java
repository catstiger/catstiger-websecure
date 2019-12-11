package com.github.catstiger.websecure.authz.impl;

import com.github.catstiger.websecure.authz.AuthzToken;

@SuppressWarnings("serial")
public final class AnonymousAuthzToken implements AuthzToken {
	private String host;

	public AnonymousAuthzToken(String host) {
		this.host = host;
	}

	@Override
	public String getPrincipal() {
		throw new UnsupportedOperationException("匿名访问不支持此操作");
	}

	@Override
	public String getCredentials() {
		throw new UnsupportedOperationException("匿名访问不支持此操作");
	}

	@Override
	public String getHost() {
		return host;
	}

	/**
	 * 判断一个Token是否匿名，只要是AnonymousAuthzToken的实例，就返回{@code true}, 否则返回{@code false}
	 */
	public static boolean isAnonymous(Object o) {
		if (o == null) {
			return false;
		}

		return (o.getClass() == AnonymousAuthzToken.class);
	}

	@Override
	public boolean isRememberMe() {
		throw new UnsupportedOperationException("匿名访问不支持此操作");
	}


  @Override
  public String getMobile() {
    throw new UnsupportedOperationException("匿名访问不支持此操作");
  }

  @Override
  public String getVerifyCode() {
    throw new UnsupportedOperationException("匿名访问不支持此操作");
  }
  
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((host == null) ? 0 : host.hashCode());
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
		AnonymousAuthzToken other = (AnonymousAuthzToken) obj;
		if (host == null) {
			if (other.host != null) {
				return false;
			}
		} else if (!host.equals(other.host)) {
			return false;
		}
		return true;
	}

  @Override
  public String getCaptcha() {
    throw new UnsupportedOperationException("匿名访问不支持此操作");
  }

  @Override
  public boolean hasVerifyCode() {
    throw new UnsupportedOperationException("匿名访问不支持此操作");
  }

  @Override
  public boolean hasCaptcha() {
    throw new UnsupportedOperationException("匿名访问不支持此操作");
  }


}
