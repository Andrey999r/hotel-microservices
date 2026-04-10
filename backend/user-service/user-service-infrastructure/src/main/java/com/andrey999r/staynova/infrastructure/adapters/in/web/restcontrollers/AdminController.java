package com.andrey999r.staynova.infrastructure.adapters.in.web.restcontrollers;

import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.in.commands.AddRoleToUserInputPort;
import com.andrey999r.staynova.domain.ports.in.commands.SaveRoleInputPort;
import com.andrey999r.staynova.domain.ports.in.queries.FindAllRolesInputPort;
import com.andrey999r.staynova.domain.ports.in.queries.FindUserByLoginInputPort;
import com.andrey999r.staynova.domain.ports.out.pagination.Filter;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.infrastructure.adapters.in.web.dto.AddRoleToUserRequestDto;
import com.andrey999r.staynova.infrastructure.adapters.in.web.mappers.AdminWebMapper;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

  private final FindAllRolesInputPort findAllRolesInputPort;
  private final FindUserByLoginInputPort findUserByLoginInputPort;
  private final AddRoleToUserInputPort addRoleToUserInputPort;
  private final SaveRoleInputPort saveRoleInputPort;
  private final AdminWebMapper adminWebMapper;

  @GetMapping
  public ResponseEntity<List<String>> getAllRoleNames(
      @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
    log.info("GET /admin/roleNames page={} size={}", page, size);
    List<String> roleNames =
        findAllRolesInputPort.execute(new Filter(page, size)).stream()
            .map(adminWebMapper::mapRoleName)
            .toList();
    log.info("GET /admin/roleNames → {} roleNames", roleNames.size());
    return ResponseEntity.ok(roleNames);
  }

  @GetMapping("/users")
  public ResponseEntity<List<String>> getUserRoleNames(@RequestParam @NotNull String login) {
    log.info("GET /admin/roleNames/users?login={}", login);
    User user = findUserByLoginInputPort.execute(new Login(login));
    List<String> roleNames = user.getRoles().stream()
        .map(adminWebMapper::mapRoleName)
        .toList();
    log.info("GET /admin/roleNames/users?login={} → {} roleNames", login, roleNames.size());
    return ResponseEntity.ok(roleNames);
  }

  @PutMapping("/users")
  public ResponseEntity<Void> addRoleToUser(@RequestBody @NotNull AddRoleToUserRequestDto request) {
    log.info("PUT /admin/roleNames/users login={} role={}", request.login(), request.roleName());
    addRoleToUserInputPort.execute(adminWebMapper.toCommand(request));
    log.info("PUT /admin/roleNames/users → done");
    return ResponseEntity.noContent().build();
  }

  @PostMapping
  public ResponseEntity<Void> addRole(@RequestBody String roleName) {
    log.info("POST /admin/roleNames role={}", roleName);
    saveRoleInputPort.execute(adminWebMapper.toRole(roleName));
    log.info("POST /admin/roleNames → created");
    return ResponseEntity.status(201).build();
  }
}
