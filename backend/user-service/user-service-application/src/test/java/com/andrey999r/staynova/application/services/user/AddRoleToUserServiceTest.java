package com.andrey999r.staynova.application.services.user;

import com.andrey999r.staynova.domain.models.Role;
import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.in.commands.AddRoleToUserCommand;
import com.andrey999r.staynova.domain.ports.out.repositories.RoleRepository;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import com.andrey999r.staynova.domain.vo.user.Login;
import java.util.HashSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("AddRoleToUserService")
class AddRoleToUserServiceTest {

  @Mock UserRepository userRepository;
  @Mock RoleRepository roleRepository;
  @InjectMocks AddRoleToUserService addRoleToUserService;

  @Test
  @DisplayName("Добавляет роль пользователю и сохраняет его в репозитории")
  void execute_shouldAddRoleToUserAndSave() {
    // arrange
    Login login = new Login("johndoe");
    RoleName roleName = new RoleName("ROLE_ADMIN");
    AddRoleToUserCommand command = new AddRoleToUserCommand(login, roleName);

    Role adminRole = new Role();
    adminRole.setRoleName(roleName);

    User user = new User();
    user.setLogin(login);
    user.setRoles(new HashSet<>());

    Mockito.when(userRepository.findByLogin(login)).thenReturn(user);
    Mockito.when(roleRepository.findByRoleName(roleName)).thenReturn(adminRole);

    // act
    addRoleToUserService.execute(command);

    // assert
    Mockito.verify(roleRepository).findByRoleName(roleName);
    Mockito.verify(userRepository).save(user);
  }
}
