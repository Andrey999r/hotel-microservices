package com.andrey999r.staynova.rooms.bl;

import com.andrey999r.staynova.rooms.api.dto.RoomDto;
import com.andrey999r.staynova.rooms.dal.entities.RoomEntity;
import java.util.List;
import org.springframework.web.multipart.MultipartFile;

public interface RoomAdminService {

  RoomEntity createRoom(RoomDto roomDto);

  void createMany(List<RoomDto> roomDtos);

  void deleteById(long roomId);

  void uploadPhoto(MultipartFile file, long roomId);

  void uploadPhoto(List<MultipartFile> files, long roomId);

  void deletePhoto(long roomId, long photoId);
}
