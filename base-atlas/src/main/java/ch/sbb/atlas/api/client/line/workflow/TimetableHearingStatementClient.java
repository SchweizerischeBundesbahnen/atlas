package ch.sbb.atlas.api.client.line.workflow;

import ch.sbb.atlas.api.client.TokenPassingFeignClientConfig;
import ch.sbb.atlas.api.timetable.hearing.TimetableHearingStatementApiInternal;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "timetableHearingStatementClient", url = "${atlas.client.gateway.url}", path = "line-directory",
    configuration =    TokenPassingFeignClientConfig.class)
public interface TimetableHearingStatementClient extends TimetableHearingStatementApiInternal {

}
