package ch.sbb.atlas.configuration;

import static org.springframework.util.StringUtils.hasText;

import ch.sbb.atlas.model.exception.BadRequestException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class PagingConfig implements WebMvcConfigurer {

  @Autowired
  private SpringDataWebProperties properties;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(); // todo
    WebMvcConfigurer.super.addInterceptors(registry);
  }

  @Override
  public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
    PageableHandlerMethodArgumentResolver resolver = new PageableHandlerMethodArgumentResolver() {
      @Override
      public Pageable resolveArgument(MethodParameter methodParameter, ModelAndViewContainer mavContainer,
          NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {

        String pageSize = webRequest.getParameter(getParameterNameToUse(getSizeParameterName(), methodParameter));
        boolean pageSizeExceeded = false;
        try {
          pageSizeExceeded = hasText(pageSize) && Integer.parseInt(pageSize) > maxPageSize();
        } finally {
          if (pageSizeExceeded) {
            System.out.println("error");
            throw new BadRequestException("The page size is limited to " + maxPageSize());
          }
          return super.resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
        }
      }
    };

    resolver.setMaxPageSize(maxPageSize());
    resolvers.add(resolver);
    WebMvcConfigurer.super.addArgumentResolvers(resolvers);
  }

  private int maxPageSize() {
    return properties.getPageable().getMaxPageSize();
  }

}
