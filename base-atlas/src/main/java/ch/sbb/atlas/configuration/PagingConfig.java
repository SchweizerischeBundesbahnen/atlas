package ch.sbb.atlas.configuration;

import ch.sbb.atlas.model.exception.BadRequestException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class PagingConfig implements WebMvcConfigurer {

  private final SpringDataWebProperties properties;

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    CustomPageableArgumentResolver customPageableArgumentResolver = getCustomPageableArgumentResolver();
    customPageableArgumentResolver.setMaxPageSize(maxPageSize());
    resolvers.add(customPageableArgumentResolver);
  }

  CustomPageableArgumentResolver getCustomPageableArgumentResolver() {
    return new CustomPageableArgumentResolver();
  }

  private int maxPageSize() {
    return properties.getPageable().getMaxPageSize();
  }

  class CustomPageableArgumentResolver extends PageableHandlerMethodArgumentResolver {

    @Override
    public Pageable resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer,
        NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
      String sizeParameter = webRequest.getParameter(getParameterNameToUse(getSizeParameterName(), methodParameter));
      if (isPageSizeValid(sizeParameter)) {
        throw new BadRequestException("The page size is limited to " + maxPageSize());
      } else {
        return doResolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
      }
    }

    private boolean isPageSizeValid(String sizeParameter) {
      Optional<Integer> pageSize = getPageSize(sizeParameter);
      return pageSize.isPresent() && pageSize.get() > maxPageSize();
    }

    private Optional<Integer> getPageSize(String pageSize) {
      try {
        return Optional.of(Integer.parseInt(pageSize));
      } catch (NumberFormatException e) {
        return Optional.empty();
      }
    }

    Pageable doResolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
        WebDataBinderFactory binderFactory) {
      return super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
    }

  }

}
