package ch.sbb.atlas.api.client.line.ttfn;

import ch.sbb.atlas.api.client.TokenPassingFeignClientConfig;
import ch.sbb.atlas.api.lidi.TimetableFieldNumberApiInternal;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "timetableFieldNumberApiInternalClient", url = "${atlas.client.gateway.url}", path = "line-directory",
    configuration = TokenPassingFeignClientConfig.class)
public interface TimetableFieldNumberApiInternalClient extends TimetableFieldNumberApiInternal {

}
