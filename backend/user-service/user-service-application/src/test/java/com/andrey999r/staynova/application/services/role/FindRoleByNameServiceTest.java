package com.andrey999r.staynova.application.services.role;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.andrey999r.staynova.domain.models.Role;
import com.andrey999r.staynova.domain.ports.out.repositories.RoleRepository;
import com.andrey999r.staynova.domain.vo.role.RoleId;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindRoleByNameService")
class FindRoleByNameServiceTest {

  @Mock RoleRepository roleRepository;
  @InjectMocks FindRoleByNameService findRoleByNameService;

  @Test
  @DisplayName("Возвращает роль по имени, если она найдена в репозитории")
  void execute_shouldReturnRole_whenFound() {
    // arrange
    Role expectedRole = new Role();
    expectedRole.setId(new RoleId(UUID.randomUUID()));
    expectedRole.setRoleName(new RoleName("ROLE_ADMIN"));
    Mockito.when(roleRepository.findByRoleName(expectedRole.getRoleName()))
        .thenReturn(expectedRole);

    // act
    Role result = findRoleByNameService.execute(expectedRole.getRoleName());

    // assert
    assertEquals(expectedRole.getId(), result.getId());
    assertEquals(expectedRole.getRoleName(), result.getRoleName());
    Mockito.verify(roleRepository).findByRoleName(expectedRole.getRoleName());
  }
}
