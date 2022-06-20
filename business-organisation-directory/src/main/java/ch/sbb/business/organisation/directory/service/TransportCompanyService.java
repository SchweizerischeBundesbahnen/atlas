package ch.sbb.business.organisation.directory.service;

import ch.sbb.business.organisation.directory.controller.TransportCompanySearchRestrictions;
import ch.sbb.business.organisation.directory.entity.TransportCompany;
import ch.sbb.business.organisation.directory.repository.TransportCompanyRepository;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvParser.Feature;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import feign.Response.Body;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Transactional
@Slf4j
public class TransportCompanyService {

  private final TransportCompanyClient transportCompanyClient;
  private final TransportCompanyRepository transportCompanyRepository;

  public void saveTransportCompaniesFromBav() {
    List<TransportCompanyCsvModel> transportCompaniesFromBav = getTransportCompaniesFromBav();
    saveTransportCompanies(transportCompaniesFromBav);
  }

  void saveTransportCompanies(List<TransportCompanyCsvModel> companies) {
    List<TransportCompany> transportCompanies = companies.stream()
                                                         .map(this::toEntity)
                                                         .collect(Collectors.toList());
    transportCompanyRepository.saveAll(transportCompanies);
  }

  private TransportCompany toEntity(TransportCompanyCsvModel csvModel) {
    return TransportCompany.builder()
                           .id(csvModel.getId())
                           .number(csvModel.getNumber())
                           .abbreviation(csvModel.getAbbreviation())
                           .description(csvModel.getDescription())
                           .businessRegisterName(csvModel.getBusinessRegisterName())
                           .transportCompanyStatus(csvModel.getTransportCompanyStatus())
                           .businessRegisterNumber(csvModel.getBusinessRegisterNumber())
                           .enterpriseId(csvModel.getEnterpriseId())
                           .ricsCode(csvModel.getRicsCode())
                           .businessOrganisationNumbers(csvModel.getBusinessOrganisationNumbers())
                           .comment(csvModel.getComment())
                           .build();
  }

  List<TransportCompanyCsvModel> getTransportCompaniesFromBav() {
    try (Body body = transportCompanyClient.getTransportCompanies().body();
        InputStream inputStream = body.asInputStream()) {
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

  public Page<TransportCompany> findAll(
      TransportCompanySearchRestrictions searchRestrictions) {
    return transportCompanyRepository.findAll(searchRestrictions.getSpecification(),
        searchRestrictions.getPageable());
  }
}
