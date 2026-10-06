package ch.sbb.scheduling.service;

import ch.sbb.atlas.api.mail.MailApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(name = "mailClient", url = "${atlas.client.gateway.url}", path = "/mail",
    configuration = MailFeignConfig.class)
public interface MailFeignClient extends MailApi {

}
