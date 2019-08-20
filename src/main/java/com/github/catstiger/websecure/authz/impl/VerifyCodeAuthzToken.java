package com.github.catstiger.websecure.authz.impl;

@SuppressWarnings("serial")
public class VerifyCodeAuthzToken extends UserAuthzToken {
	private String verifyCode;
	private String mobile;

	public VerifyCodeAuthzToken() {
		super();
	}

	public String getVerifyCode() {
		return verifyCode;
	}

	public void setVerifyCode(String verifyCode) {
		this.verifyCode = verifyCode;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
}
