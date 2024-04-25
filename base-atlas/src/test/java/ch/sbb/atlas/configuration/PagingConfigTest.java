package ch.sbb.atlas.configuration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import ch.sbb.atlas.configuration.PagingConfig.CustomPageableArgumentResolver;
import ch.sbb.atlas.model.exception.BadRequestException;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.autoconfigure.data.web.SpringDataWebProperties;
import org.springframework.data.domain.Pageable;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

class PagingConfigTest {

  @Test
  void shouldThrowBadRequestExceptionWhenPageSizeGreaterThanMax() {
    // given
    SpringDataWebProperties properties = Mockito.mock(SpringDataWebProperties.class);
    Mockito.when(properties.getPageable()).thenReturn(new SpringDataWebProperties.Pageable());
    CustomPageableArgumentResolver customPageableArgumentResolver = new PagingConfig(
        properties).new CustomPageableArgumentResolver();
    NativeWebRequest webRequest = Mockito.mock(NativeWebRequest.class);
    Mockito.when(webRequest.getParameter(Mockito.anyString())).thenReturn("5000");

    // when & then
    assertThrows(BadRequestException.class, () -> customPageableArgumentResolver.resolveArgument(
        null, null, webRequest, null));
  }

  @Test
  void shouldReturnPageableIfPageSizeValid() {
    // given
    SpringDataWebProperties properties = Mockito.mock(SpringDataWebProperties.class);
    Mockito.when(properties.getPageable()).thenReturn(new SpringDataWebProperties.Pageable());
    CustomPageableArgumentResolver customPageableArgumentResolver = new PagingConfig(
        properties).new CustomPageableArgumentResolver();
    CustomPageableArgumentResolver customPageableArgumentResolverSpy = Mockito.spy(customPageableArgumentResolver);
    NativeWebRequest webRequest = Mockito.mock(NativeWebRequest.class);
    Mockito.when(webRequest.getParameter(Mockito.anyString())).thenReturn("1000");
    Pageable expectedPageable = Mockito.mock(Pageable.class);
    Mockito.doReturn(expectedPageable).when(customPageableArgumentResolverSpy)
        .doResolveArgument(null, null, webRequest, null);

    // when
    Pageable result = customPageableArgumentResolverSpy
        .resolveArgument(null, null, webRequest, null);

    // then
    assertEquals(expectedPageable, result);
  }

  @Test
  void shouldAddResolverToArgumentHandlerList() {
    // given
    SpringDataWebProperties.Pageable propertiesPageable = new SpringDataWebProperties.Pageable();
    propertiesPageable.setMaxPageSize(5000);
    SpringDataWebProperties properties = Mockito.mock(SpringDataWebProperties.class);
    Mockito.when(properties.getPageable()).thenReturn(propertiesPageable);
    PagingConfig pagingConfig = new PagingConfig(properties);
    PagingConfig pagingConfigSpy = Mockito.spy(pagingConfig);
    CustomPageableArgumentResolver customPageableArgumentResolverMock = Mockito.mock(CustomPageableArgumentResolver.class);
    Mockito.doReturn(customPageableArgumentResolverMock).when(pagingConfigSpy).getCustomPageableArgumentResolver();

    // when
    List<HandlerMethodArgumentResolver> resolvers = new ArrayList();
    pagingConfigSpy.addArgumentResolvers(resolvers);

    // then
    assertThat(resolvers).hasSize(1);
    assertEquals(customPageableArgumentResolverMock, resolvers.get(0));
    Mockito.verify(customPageableArgumentResolverMock, Mockito.times(1)).setMaxPageSize(5000);
  }
}
