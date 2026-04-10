package com.andrey999r.staynova.infrastructure.adapters.out.jpa;

import com.andrey999r.staynova.domain.models.User;
import com.andrey999r.staynova.domain.ports.out.pagination.Filter;
import com.andrey999r.staynova.domain.ports.out.pagination.Filtered;
import com.andrey999r.staynova.domain.ports.out.repositories.UserRepository;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import com.andrey999r.staynova.domain.vo.user.Login;
import com.andrey999r.staynova.infrastructure.adapters.out.exceptions.user.UserNotFoundException;
import com.andrey999r.staynova.infrastructure.adapters.out.jpa.entities.UserEntity;
import com.andrey999r.staynova.infrastructure.adapters.out.jpa.interfaces.JpaUserRepository;
import com.andrey999r.staynova.infrastructure.adapters.out.jpa.mappers.UserMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserJpaOutputAdapter implements UserRepository {
  private final JpaUserRepository jpaUserRepository;
  private final UserMapper userMapper;

  @Override
  public User findByLogin(Login login) {
    log.debug("Finding user by login={}", login);
    UserEntity userEntity =
        jpaUserRepository
            .findByLogin(login.login())
            .orElseThrow(
                () -> {
                  log.warn("User not found: login={}", login);
                  return new UserNotFoundException(login.login());
                });
    return userMapper.toModel(userEntity);
  }

  @Override
  public boolean existsByLogin(Login login) {
    log.debug("Checking existence of user: login={}", login);
    boolean exists = jpaUserRepository.findByLogin(login.login()).isPresent();
    log.debug("User exists={} for login={}", exists, login);
    return exists;
  }

  @Override
  public Filtered<User> findAllByRoleName(Filter filter, RoleName roleName) {
    log.debug("Fetching users by name={} page={} size={}", roleName, filter.page(), filter.size());
    Pageable pageable = PageRequest.of(filter.page(), filter.size());
    List<UserEntity> users = jpaUserRepository.findAllByRoleName(roleName.name(), pageable);
    List<User> userModels = users.stream().map(userMapper::toModel).toList();
    log.debug("Found {} users with name={}", userModels.size(), roleName);
    return new Filtered<>(userModels, pageable.getPageNumber(), pageable.getPageSize());
  }

  @Override
  public void save(User user) {
    log.info("Saving user: login={}", user.getLogin());
    UserEntity userEntity = userMapper.toEntity(user);
    jpaUserRepository.save(userEntity);
    log.info("User saved: login={}", user.getLogin());
  }
}
