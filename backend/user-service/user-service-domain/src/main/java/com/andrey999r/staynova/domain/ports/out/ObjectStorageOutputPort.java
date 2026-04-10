package com.andrey999r.staynova.domain.ports.out;

import com.andrey999r.staynova.domain.vo.user.Login;

public interface ObjectStorageOutputPort {
  String getDefaultPicturePath();

  void delete(String path);

  String uploadPhoto(byte[] data, Login login);
}
