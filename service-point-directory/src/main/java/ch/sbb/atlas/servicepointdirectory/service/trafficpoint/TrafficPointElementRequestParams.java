package ch.sbb.atlas.servicepointdirectory.service.trafficpoint;

import ch.sbb.atlas.api.model.VersionedObjectDateRequestParams;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Singular;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class TrafficPointElementRequestParams extends VersionedObjectDateRequestParams {
    @Parameter(description = "Unique key for service points which is used in the customer information.")
    @Singular(ignoreNullCollections = true)
    private List<String> sloids = new ArrayList<>();

    @Parameter(description = "DiDok-Number formerly known as UIC-Code, combination of uicCountryCode and numberShort.")
    @Singular(ignoreNullCollections = true)
    private List<Integer> servicePointNumbers = new ArrayList<>();

    @Parameter(description = "")
    @Singular(ignoreNullCollections = true)
    private List<String> servicePointSloids = new ArrayList<>();

    @Parameter(description = "sboid")
    @Singular(ignoreNullCollections = true)
    private List<String> businessOrganisations = new ArrayList<>();

    @Parameter(description = "List of UIC Country codes. The UIC Country code applies to the country of the service point number")
    @Singular(ignoreNullCollections = true)
    private List<String> uicCountryCodes = new ArrayList<>();

    @Parameter(description =
        "Number of a service point which is provided by DiDok for Switzerland. It is part of the unique key for"
            + " service points.")
    @Singular(value = "numberShort", ignoreNullCollections = true)
    private List<Integer> servicePointNumberShort = new ArrayList<>();


    public List<ServicePointNumber> getServicePointNumbers() {
        return servicePointNumbers.stream().map(ServicePointNumber::ofNumberWithoutCheckDigit).toList();
    }

    //TODO: Does not work
//    public List<ServicePointNumber> getServicePointNumberShort() {
//            return servicePointNumbers.stream().map(servicePointNumber -> servicePointNumber.getNumberShort()).collect(Collectors.toList()).collect(Collectors.toList());
//    }


}
