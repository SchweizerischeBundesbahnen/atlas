package ch.sbb.atlas.servicepointdirectory.service.servicepoint;

import ch.sbb.atlas.servicepoint.Country;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.repository.ServicePointNumberRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ServicePointNumberService {

  private final ServicePointNumberRepository servicePointNumberRepository;

  public int getNextAvailableServicePointId(Country country) {
    Integer deleteResult;
    Integer nextAvailableServicePointNumber;

    do {
      nextAvailableServicePointNumber =
          Objects.requireNonNull(servicePointNumberRepository.getNextAvailableServicePointNumber(country.name()),
              "nextAvailableServicePointNumber must not be null");
      deleteResult = servicePointNumberRepository.deleteAvailableNumber(nextAvailableServicePointNumber, country.name());
    } while (deleteResult != 1);

    return nextAvailableServicePointNumber;
  }

  public void deleteAvailableNumber(ServicePointNumber servicePointNumber, Country country) {
    servicePointNumberRepository.deleteAvailableNumber(servicePointNumber.getNumberShort(), country.name());
  }

}
