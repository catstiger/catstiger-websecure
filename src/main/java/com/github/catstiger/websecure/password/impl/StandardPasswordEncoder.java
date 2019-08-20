package com.github.catstiger.websecure.password.impl;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import com.github.catstiger.common.util.Hex;
import com.github.catstiger.websecure.password.PasswordEncoder;
import com.google.common.base.Charsets;

public class StandardPasswordEncoder implements PasswordEncoder {
  public static final String DEFAULT_ALGORITHM = "MD5";

  private MessageDigest messageDigest;
  private int iterations = 1;
  private String algorithm = DEFAULT_ALGORITHM;
  private String salt;

  public StandardPasswordEncoder() {
    createMessageDigest(DEFAULT_ALGORITHM);
  }

  @Override
  public String encode(CharSequence rawPassword) {
    return encode(rawPassword, salt);
  }
  
  private String encode(CharSequence rawPassword, String usedSalt) {
    byte[] bytes = digest(rawPassword, usedSalt);
    return new String(Hex.encode(bytes));
  }

  @Override
  public boolean matches(CharSequence rawPassword, String encodedPassword) {
    byte[] expected = Hex.decode(encodedPassword);
    byte[] actual = digest(rawPassword, salt);

    return matches(expected, actual);
  }

  private boolean matches(byte[] expected, byte[] actual) {
    if (expected.length != actual.length) {
      return false;
    }

    int result = 0;
    for (int i = 0; i < expected.length; i++) {
      result |= expected[i] ^ actual[i];
    }
    return result == 0;
  }

  private byte[] digest(CharSequence rawPassword, String usedSalt) {
    String raw = rawPassword.toString();
    if (usedSalt != null) {
      raw = new StringBuilder(rawPassword).append("@").append(usedSalt).toString();
    }
    return digest(raw.getBytes(Charsets.UTF_8));
  }

  private byte[] digest(byte[] value) {
    synchronized (messageDigest) {
      for (int i = 0; i < iterations; i++) {
        value = messageDigest.digest(value);
      }
      return value;
    }
  }

  public void setIterations(int iterations) {
    this.iterations = iterations;
  }

  public void setAlgorithm(String algorithm) {
    this.algorithm = algorithm;
    createMessageDigest(this.algorithm);
  }

  private void createMessageDigest(String alg) {
    try {
      messageDigest = MessageDigest.getInstance(alg);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("No such hashing algorithm", e);
    }
  }

  public void setSalt(String salt) {
    this.salt = salt;
  }

}
