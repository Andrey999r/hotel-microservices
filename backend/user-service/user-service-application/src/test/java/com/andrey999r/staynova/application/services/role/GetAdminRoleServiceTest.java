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
@DisplayName("GetAdminRoleNameService")
class GetAdminRoleServiceTest {

  @Mock RoleTypeOutputPort roleTypeOutputPort;
  @InjectMocks GetAdminRoleNameService getAdminRoleService;

  @Test
  @DisplayName("Возвращает название роли администратора из порта")
  void execute_shouldReturnAdminRole() {
    // arrange
    RoleName expectedAdminRoleName = new RoleName("ROLE_ADMIN");
    Mockito.when(roleTypeOutputPort.getAdminRoleName()).thenReturn(expectedAdminRoleName);

    // act
    RoleName result = getAdminRoleService.execute(null);

    // assert
    assertEquals(expectedAdminRoleName, result);
    Mockito.verify(roleTypeOutputPort).getAdminRoleName();
  }
}
