package ch.sbb.atlas.kafka;

import static org.springframework.beans.factory.config.YamlProcessor.MatchStatus.ABSTAIN;
import static org.springframework.beans.factory.config.YamlProcessor.MatchStatus.FOUND;
import static org.springframework.beans.factory.config.YamlProcessor.MatchStatus.NOT_FOUND;

import java.util.Objects;
import java.util.Optional;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.core.io.support.EncodedResource;
import org.springframework.core.io.support.PropertySourceFactory;
import org.springframework.util.ObjectUtils;

@Slf4j
public class YamlPropertySourceFactory implements PropertySourceFactory {

  @Override
  public PropertySource<?> createPropertySource(String name, EncodedResource encodedResource) {
    String activeProfile = Optional.ofNullable(
        Optional.ofNullable(System.getenv("SPRING_PROFILES_ACTIVE"))
                .orElse(System.getProperty("spring.profiles.active"))).orElse("local");

    log.info("Atlas Kafka: Loading {} with profile={}", encodedResource.getResource().getFilename(),
        activeProfile);

    YamlPropertiesFactoryBean yamlFactory = new YamlPropertiesFactoryBean();
    yamlFactory.setDocumentMatchers(properties -> {
      String profileProperty = properties.getProperty("spring.profiles");

      if (ObjectUtils.isEmpty(profileProperty)) {
        return ABSTAIN;
      }

      return profileProperty.contains(activeProfile) ? FOUND : NOT_FOUND;
    });
    yamlFactory.setResources(encodedResource.getResource());

    Properties properties = Objects.requireNonNull(yamlFactory.getObject());

    return new PropertiesPropertySource(
        Objects.requireNonNull(encodedResource.getResource().getFilename()), properties);
  }
}
