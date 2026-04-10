package com.andrey999r.staynova.infrastructure.adapters.out.jpa;

import com.andrey999r.staynova.domain.models.Role;
import com.andrey999r.staynova.domain.ports.out.pagination.Filter;
import com.andrey999r.staynova.domain.ports.out.pagination.Filtered;
import com.andrey999r.staynova.domain.ports.out.repositories.RoleRepository;
import com.andrey999r.staynova.domain.vo.role.RoleName;
import com.andrey999r.staynova.infrastructure.adapters.out.exceptions.role.RoleNotFoundException;
import com.andrey999r.staynova.infrastructure.adapters.out.jpa.entities.RoleEntity;
import com.andrey999r.staynova.infrastructure.adapters.out.jpa.interfaces.JpaRoleRepository;
import com.andrey999r.staynova.infrastructure.adapters.out.jpa.mappers.RoleMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class RoleJpaOutputAdapter implements RoleRepository {
  private final JpaRoleRepository jpaRoleRepository;
  private final RoleMapper roleMapper;

  @Override
  public Role findByRoleName(RoleName roleName) {
    log.debug("Finding role by name={}", roleName);
    RoleEntity roleEntity =
        jpaRoleRepository
            .findByRoleName(roleName.name())
            .orElseThrow(() -> new RoleNotFoundException("No role found with name " + roleName));
    log.debug("Role found for name={}", roleName);
    return roleMapper.toModel(roleEntity);
  }

  @Override
  public boolean existsByRoleName(RoleName roleName) {
    return jpaRoleRepository.findByRoleName(roleName.name()).isPresent();
  }

  @Override
  public Filtered<Role> findAll(Filter filter) {
    log.debug("Fetching all roleNames: page={} size={}", filter.page(), filter.size());
    Pageable pageable = PageRequest.of(filter.page(), filter.size());
    List<RoleEntity> roleEntities = jpaRoleRepository.findAll(pageable).getContent();
    List<Role> roleModels = roleEntities.stream().map(roleMapper::toModel).toList();
    log.debug("Found {} roleNames", roleModels.size());
    return new Filtered<>(roleModels, pageable.getPageNumber(), pageable.getPageSize());
  }

  @Override
  public void save(Role role) {
    log.info("Saving role: name={}", role.getRoleName());
    RoleEntity roleEntity = roleMapper.toEntity(role);
    jpaRoleRepository.save(roleEntity);
    log.info("Role saved: name={}", role.getRoleName());
  }
}
