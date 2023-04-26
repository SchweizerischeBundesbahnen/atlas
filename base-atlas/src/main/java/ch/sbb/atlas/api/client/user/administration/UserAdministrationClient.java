package ch.sbb.atlas.api.client.user.administration;

import ch.sbb.atlas.api.client.TokenPassingFeignClientConfig;
import ch.sbb.atlas.api.user.administration.UserAdministrationApiV1;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "userAdministrationClient", url = "${atlas.client.gateway.url}", path = "user-administration",
    configuration = TokenPassingFeignClientConfig.class)
public interface UserAdministrationClient extends UserAdministrationApiV1 {

}
