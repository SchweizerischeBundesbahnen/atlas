package ch.sbb.business.organisation.directory.service;

import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "bavClient", url = "${external.url.bav}")
interface TransportCompanyClient {
  @GetMapping(value = "/Pages/Export/Export.aspx?identifier=g8DwS5k4", produces = "text/csv")
  Response getTransportCompanies();
}
