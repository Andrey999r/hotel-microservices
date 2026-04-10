package com.andrey999r.staynova.bl;

public interface TokenBlacklistService {

  void blacklistToken(String token, long expirationMls);
}
