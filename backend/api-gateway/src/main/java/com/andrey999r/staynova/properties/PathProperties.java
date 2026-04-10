package com.andrey999r.staynova.properties;

import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.paths")
@Getter
@Setter
public class PathProperties {

  private List<String> authenticatedPaths;
  private List<String> permitAllPaths;
}
