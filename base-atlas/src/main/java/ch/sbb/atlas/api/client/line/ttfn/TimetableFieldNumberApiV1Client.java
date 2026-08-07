package ch.sbb.atlas.api.client.line.ttfn;

import ch.sbb.atlas.api.client.TokenPassingFeignClientConfig;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberApiV1;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "timetableFieldNumberApiV1Client", url = "${atlas.client.gateway.url}", path = "line-directory",
    configuration = TokenPassingFeignClientConfig.class)
public interface TimetableFieldNumberApiV1Client extends TimetableFieldNumberApiV1 {

}
