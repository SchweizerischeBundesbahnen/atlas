package ch.sbb.timetable.hearing.client;

import ch.sbb.atlas.api.user.administration.UserAdministrationApiV1;
import ch.sbb.timetable.hearing.config.AtlasAdminFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "userAdministrationAdminClient", url = "${atlas.client.gateway.url}", path = "user-administration",
    configuration = AtlasAdminFeignConfig.class)
public interface UserAdministrationAdminClient extends UserAdministrationApiV1 {

}
