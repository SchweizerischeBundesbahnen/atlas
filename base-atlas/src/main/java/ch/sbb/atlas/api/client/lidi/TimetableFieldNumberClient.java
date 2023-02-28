package ch.sbb.atlas.api.client.lidi;

import ch.sbb.atlas.api.client.TokenPassingFeignClientConfig;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberApiV1;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "timetableFieldNumberClient", url = "${atlas.client.gateway.url}", path = "line-directory", configuration =
    TokenPassingFeignClientConfig.class)
public interface TimetableFieldNumberClient extends TimetableFieldNumberApiV1 {

}
