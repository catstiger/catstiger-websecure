package com.github.catstiger.websecure.token.impl;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.github.catstiger.common.util.Exceptions;
import com.github.catstiger.websecure.SecureConstants;
import com.github.catstiger.websecure.cfg.AbstractSecurityConfigurable;
import com.github.catstiger.websecure.token.TokenStrategy;

@Component
public class JWTTokenStrategy extends AbstractSecurityConfigurable implements TokenStrategy {
  private static Logger logger = LoggerFactory.getLogger(JWTTokenStrategy.class);
  private static Algorithm algorithm;
  private static JWTVerifier verifier;
  private static Builder builder;
  
  static {
    try {
      algorithm = Algorithm.HMAC256(SecureConstants.TOKEN_SECURITY);
      verifier = JWT.require(algorithm).build();
      builder = JWT.create();
    } catch (Exception e) {
      e.printStackTrace();
      throw Exceptions.unchecked(e);
    }
  }
  
  @Override
  public String sign(Object rawObject) {
    Assert.notNull(rawObject, "The raw object must not be null.");
    
    return builder.withClaim(SecureConstants.JWT_SIGN_MAP_KEY, rawObject.toString())
        .withExpiresAt(DateTime.now().plusSeconds(getCfg().getTokenExpirySec()).toDate()).sign(algorithm);
    
  }

  @Override
  public Object verify(String token) {
    if (token == null) {
      logger.warn("没有提供TOKEN.");
      return null;
    }
    Object result = null;
    try {
      DecodedJWT jwt = verifier.verify(token);
      Claim claim = jwt.getClaim(SecureConstants.JWT_SIGN_MAP_KEY);
      result = claim.asString();
    } catch (Exception e) {
      logger.error("Token解析错误 {}", token);
      e.printStackTrace();
    } 
    return result;
  }

}
