package com.andrey999r.staynova.application.services.role;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.andrey999r.staynova.domain.models.Role;
import com.andrey999r.staynova.domain.ports.out.pagination.Filter;
import com.andrey999r.staynova.domain.ports.out.pagination.Filtered;
import com.andrey999r.staynova.domain.ports.out.repositories.RoleRepository;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindAllRolesService")
class FindAllRolesServiceTest {

  @Mock RoleRepository roleRepository;
  @InjectMocks FindAllRolesService findAllRolesService;

  @Test
  @DisplayName("Возвращает список ролей из репозитория по фильтру")
  void execute_shouldReturnRolesFromRepository() {
    // arrange
    Role adminRole = new Role();
    adminRole.setRoleName(new RoleName("ROLE_ADMIN"));
    Role userRole = new Role();
    userRole.setRoleName(new RoleName("ROLE_USER"));
    Filter filter = new Filter(0, 10);
    Mockito.when(roleRepository.findAll(filter))
        .thenReturn(new Filtered<>(List.of(adminRole, userRole), 0, 10));

    // act
    List<Role> result = findAllRolesService.execute(filter);

    // assert
    assertEquals(2, result.size());
    assertEquals(adminRole.getRoleName(), result.getFirst().getRoleName());
    Mockito.verify(roleRepository).findAll(filter);
  }

  @Test
  @DisplayName("Возвращает пустой список, если ролей нет")
  void execute_shouldReturnEmptyList_whenNoRoles() {
    // arrange
    Filter filter = new Filter(0, 10);
    Mockito.when(roleRepository.findAll(filter)).thenReturn(new Filtered<>(List.of(), 0, 10));

    // act
    List<Role> result = findAllRolesService.execute(filter);

    // assert
    assertTrue(result.isEmpty());
    Mockito.verify(roleRepository).findAll(filter);
  }
}
