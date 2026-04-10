package com.andrey999r.staynova.application.services.role;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.andrey999r.staynova.domain.ports.out.RoleTypeOutputPort;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("GetUserNameRoleService")
class GetUserRoleServiceTest {

  @Mock RoleTypeOutputPort roleTypeOutputPort;
  @InjectMocks GetUserNameRoleService getUserRoleService;

  @Test
  @DisplayName("Возвращает название роли обычного пользователя из порта")
  void execute_shouldReturnUserRole() {
    // arrange
    RoleName expectedUserRoleName = new RoleName("ROLE_USER");
    Mockito.when(roleTypeOutputPort.getUserRoleName()).thenReturn(expectedUserRoleName);

    // act
    RoleName result = getUserRoleService.execute(null);

    // assert
    assertEquals(expectedUserRoleName, result);
    Mockito.verify(roleTypeOutputPort).getUserRoleName();
  }
}
