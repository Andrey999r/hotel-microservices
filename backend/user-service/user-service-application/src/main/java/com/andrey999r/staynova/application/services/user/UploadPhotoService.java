package com.andrey999r.staynova.application.services.user;

import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.in.commands.UploadPhotoCommand;
import com.andrey999r.staynova.domain.ports.in.commands.UploadPhotoInputPort;
import com.andrey999r.staynova.domain.ports.out.AuthOutputPort;
import com.andrey999r.staynova.domain.ports.out.ObjectStorageOutputPort;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.domain.vo.user.PicturePath;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class UploadPhotoService implements UploadPhotoInputPort {

  private final UserRepository userRepositoryPort;
  private final AuthOutputPort authOutputPort;
  private final ObjectStorageOutputPort objectStorageOutputPort;

  @Override
  public void execute(UploadPhotoCommand command) {
    Login login = authOutputPort.getUserLogin();
    log.info("UploadPhotoService execute login={}, filename={}", login, command.fileName());
    User user = userRepositoryPort.findByLogin(login);
    String avatarUrl = objectStorageOutputPort.uploadPhoto(command.data(), login);
    PicturePath picturePath = new PicturePath(avatarUrl);
    user.setPicturePath(picturePath);
    userRepositoryPort.save(user);
    log.info("Photo uploaded successfully for user: {}, url={}", login, avatarUrl);
  }
}
