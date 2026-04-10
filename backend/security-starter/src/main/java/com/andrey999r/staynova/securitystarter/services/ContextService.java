package com.andrey999r.staynova.securitystarter.services;

import java.util.List;

public interface ContextService {
  List<String> getUserRoleNames();

  String getUserLogin();
}
