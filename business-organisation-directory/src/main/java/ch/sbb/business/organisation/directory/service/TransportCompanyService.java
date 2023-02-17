package ch.sbb.business.organisation.directory.service;

import ch.sbb.atlas.kafka.model.mail.MailNotification;
import ch.sbb.atlas.kafka.model.mail.MailType;
import ch.sbb.business.organisation.directory.controller.TransportCompanySearchRestrictions;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.entity.TransportCompany.Fields;
import ch.sbb.business.organisation.directory.repository.TransportCompanyRepository;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import feign.Response.Body;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class TransportCompanyService {

  private final TransportCompanyClient transportCompanyClient;
  private final TransportCompanyRepository transportCompanyRepository;
  private final MailClient mailClient;

  @Value("${mail.receiver.tu-relations-report}")
  private List<String> relationsReportAddresses;

  public Page<TransportCompany> getTransportCompanies(
      TransportCompanySearchRestrictions searchRestrictions) {
    return transportCompanyRepository.findAll(searchRestrictions.getSpecification(),
        searchRestrictions.getPageable());
  }

  public Optional<TransportCompany> findById(Long id) {
    return transportCompanyRepository.findById(id);
  }

  public boolean existsById(Long id) {
    return transportCompanyRepository.existsById(id);
  }

  @Async
  public void saveTransportCompaniesFromBav() {
    log.info("Starting async load");
    List<TransportCompanyCsvModel> transportCompaniesFromBav = getTransportCompaniesFromBav();

    saveTransportCompanies(transportCompaniesFromBav);
    log.info("{} Transport Companies saved asynchronously", transportCompaniesFromBav.size());

    validateRelationsAndNotifyBusiness();
  }

  void saveTransportCompanies(List<TransportCompanyCsvModel> companies) {
    List<TransportCompany> transportCompanies = companies.stream()
                                                         .map(TransportCompanyCsvModel::toEntity)
                                                         .toList();
    transportCompanyRepository.saveAll(transportCompanies);
  }

  List<TransportCompanyCsvModel> getTransportCompaniesFromBav() {
    try (Body body = transportCompanyClient.getTransportCompanies()
                                           .body(); InputStream inputStream = body.asInputStream()) {
      return parseTransportCompanies(inputStream);
    } catch (IOException e) {
      throw new IllegalStateException("Could not read response from BAV correctly", e);
    }
  }

  static List<TransportCompanyCsvModel> parseTransportCompanies(InputStream inputStream)
      throws IOException {
    CsvMapper mapper = new CsvMapper().enable(Feature.EMPTY_STRING_AS_NULL);
    CsvSchema csvSchema = CsvSchema.emptySchema()
                                   .withHeader()
                                   .withColumnSeparator(';')
                                   .withEscapeChar('\\');

    MappingIterator<TransportCompanyCsvModel> mappingIterator = mapper.readerFor(
        TransportCompanyCsvModel.class).with(csvSchema).readValues(inputStream);
    List<TransportCompanyCsvModel> transportCompanies = mappingIterator.readAll();
    log.info("Parsed {} transportCompanies", transportCompanies.size());
    return transportCompanies;
  }

  private void validateRelationsAndNotifyBusiness() {
    List<TransportCompany> transportCompaniesWithInvalidRelations = transportCompanyRepository.findTransportCompaniesWithInvalidRelations();
    if (!transportCompaniesWithInvalidRelations.isEmpty()) {
      log.warn("TransportCompany with numbers={} have invalid relations",
          transportCompaniesWithInvalidRelations.stream()
                                                .map(TransportCompany::getNumber)
                                                .toList());

      mailClient.produceMailNotification(MailNotification.builder()
                                                         .to(relationsReportAddresses)
                                                         .templateProperties(buildProperties(
                                                             transportCompaniesWithInvalidRelations))
                                                         .mailType(MailType.TU_IMPORT)
                                                         .build());
    }
  }

  private List<Map<String, Object>> buildProperties(
      List<TransportCompany> transportCompaniesWithInvalidRelations) {
    return transportCompaniesWithInvalidRelations.stream()
                                                 .map(transportCompany -> {
                                                   Map<String, Object> object = new HashMap<>();
                                                   object.put(Fields.number,
                                                       transportCompany.getNumber());
                                                   object.put(Fields.abbreviation,
                                                       transportCompany.getAbbreviation());
                                                   object.put(Fields.businessRegisterName,
                                                       transportCompany.getBusinessRegisterName());
                                                   object.put(Fields.transportCompanyStatus,
                                                       transportCompany.getTransportCompanyStatus());
                                                   return object;
                                                 }).toList();
  }

}
