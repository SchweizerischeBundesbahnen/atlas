package ch.sbb.timetable.hearing.mail;

import ch.sbb.atlas.api.mail.MailApiInternal;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "mailClient", url = "${atlas.client.gateway.url}", path = "/mail",
    configuration = MailFeignConfig.class)
public interface MailFeignClient extends MailApiInternal {

}
