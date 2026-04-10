package com.andrey999r.staynova.infrastructure.adapters.in.web.restcontrollers;

import com.andrey999r.staynova.domain.ports.in.commands.UpdateProfileInputPort;
import com.andrey999r.staynova.domain.ports.in.commands.UploadPhotoInputPort;
import com.andrey999r.staynova.domain.ports.in.queries.GetProfileInfoInputPort;
import com.andrey999r.staynova.domain.ports.in.results.ProfileInfoResult;
import com.andrey999r.staynova.infrastructure.adapters.in.web.dto.ProfileInfoResponseDto;
import com.andrey999r.staynova.infrastructure.adapters.in.web.dto.UpdateProfileRequestDto;
import com.andrey999r.staynova.infrastructure.adapters.in.web.mappers.ProfileMapper;
import com.andrey999r.staynova.infrastructure.adapters.in.web.mappers.UploadPhotoCommandMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/user")
public class UserController {

  private final GetProfileInfoInputPort getProfileInfoUseCase;
  private final UpdateProfileInputPort updateProfileUseCase;
  private final UploadPhotoInputPort uploadPhotoUseCase;
  private final ProfileMapper profileMapper;

  @GetMapping("/profile")
  public ResponseEntity<ProfileInfoResponseDto> getProfile() {
    log.info("GET /user/profile");
    ProfileInfoResult result = getProfileInfoUseCase.execute(null);
    log.info("GET /user/profile → login={}", result.login());
    return ResponseEntity.ok(profileMapper.toResponseDto(result));
  }

  @PutMapping("/profile")
  public ResponseEntity<ProfileInfoResponseDto> updateProfile(
      @RequestBody UpdateProfileRequestDto request) {
    log.info("PUT /user/profile email={}", request.email());
    ProfileInfoResult result = updateProfileUseCase.execute(profileMapper.toCommand(request));
    log.info("PUT /user/profile → updated for login={}", result.login());
    return ResponseEntity.ok(profileMapper.toResponseDto(result));
  }

  @PostMapping(path = "/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<Void> uploadPhoto(@RequestPart("file") MultipartFile file) {
    log.info("POST /user/photo file='{}'", file.getOriginalFilename());
    uploadPhotoUseCase.execute(UploadPhotoCommandMapper.toCommand(file));
    log.info("POST /user/photo → uploaded");
    return ResponseEntity.noContent().build();
  }
}
