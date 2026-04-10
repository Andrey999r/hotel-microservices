package com.andrey999r.staynova.application.services.role;

import com.andrey999r.staynova.domain.models.Role;
import com.andrey999r.staynova.domain.ports.in.queries.FindAllRolesInputPort;
import com.andrey999r.staynova.domain.ports.out.pagination.Filter;
import com.andrey999r.staynova.domain.ports.out.pagination.Filtered;
import com.andrey999r.staynova.domain.ports.out.repositories.RoleRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
public class FindAllRolesService implements FindAllRolesInputPort {
  private final RoleRepository roleRepository;

  @Override
  public List<Role> execute(Filter filter) {
    log.debug("Fetching all roleNames: page={} size={}", filter.page(), filter.size());
    Filtered<Role> filteredRoles = roleRepository.findAll(filter);
    log.debug("Found {} roleNames", filteredRoles.content().size());
    return filteredRoles.content();
  }
}
