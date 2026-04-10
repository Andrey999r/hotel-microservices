package com.andrey999r.staynova.application.services.role;

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
@DisplayName("SaveRoleService")
class SaveRoleServiceTest {

  @Mock RoleRepository roleRepository;
  @InjectMocks SaveRoleService saveRoleService;

  @Test
  @DisplayName("Передаёт роль в репозиторий для сохранения")
  void execute_shouldSaveRole() {
    // arrange
    Role role = new Role();
    role.setId(new RoleId(UUID.randomUUID()));
    role.setRoleName(new RoleName("ROLE_ADMIN"));

    // act
    saveRoleService.execute(role);

    // assert
    Mockito.verify(roleRepository).save(role);
  }
}
