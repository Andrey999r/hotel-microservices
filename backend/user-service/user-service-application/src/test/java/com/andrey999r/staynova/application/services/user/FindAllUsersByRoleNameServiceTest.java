package com.andrey999r.staynova.application.services.user;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.in.queries.FindAllUsersByRoleNameQuery;
import com.andrey999r.staynova.domain.ports.out.pagination.Filter;
import com.andrey999r.staynova.domain.ports.out.pagination.Filtered;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import com.andrey999r.staynova.domain.vo.user.Login;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("FindAllUsersByRoleNameService")
class FindAllUsersByRoleNameServiceTest {

  @Mock UserRepository userRepository;
  @InjectMocks FindAllUsersByRoleNameService findAllUsersByRoleNameService;

  @Test
  @DisplayName("Возвращает список пользователей с заданной ролью")
  void execute_shouldReturnUsersForGivenRole() {
    // arrange
    RoleName adminRoleName = new RoleName("ROLE_ADMIN");
    Filter filter = new Filter(0, 10);
    FindAllUsersByRoleNameQuery query = new FindAllUsersByRoleNameQuery(filter, adminRoleName);

    User john = new User();
    john.setLogin(new Login("johndoe"));

    Mockito.when(userRepository.findAllByRoleName(filter, adminRoleName))
        .thenReturn(new Filtered<>(List.of(john), 0, 10));

    // act
    List<User> result = findAllUsersByRoleNameService.execute(query);

    // assert
    assertEquals(1, result.size());
    assertEquals(new Login("johndoe"), result.get(0).getLogin());
    Mockito.verify(userRepository).findAllByRoleName(filter, adminRoleName);
  }

  @Test
  @DisplayName("Возвращает пустой список, если пользователей с такой ролью нет")
  void execute_shouldReturnEmptyList_whenNoUsersWithRole() {
    // arrange
    RoleName superAdminRoleName = new RoleName("ROLE_SUPERADMIN");
    Filter filter = new Filter(0, 10);
    FindAllUsersByRoleNameQuery query = new FindAllUsersByRoleNameQuery(filter, superAdminRoleName);

    Mockito.when(userRepository.findAllByRoleName(filter, superAdminRoleName))
        .thenReturn(new Filtered<>(List.of(), 0, 10));

    // act
    List<User> result = findAllUsersByRoleNameService.execute(query);

    // assert
    assertTrue(result.isEmpty());
    Mockito.verify(userRepository).findAllByRoleName(filter, superAdminRoleName);
  }
}
