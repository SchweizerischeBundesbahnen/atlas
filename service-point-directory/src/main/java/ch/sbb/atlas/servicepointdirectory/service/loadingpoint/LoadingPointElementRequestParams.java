package ch.sbb.atlas.servicepointdirectory.service.loadingpoint;

import ch.sbb.atlas.api.model.VersionedObjectDateRequestParams;
import ch.sbb.atlas.servicepoint.ServicePointNumber;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@ToString
public class LoadingPointElementRequestParams extends VersionedObjectDateRequestParams {

    @Parameter(description = "Number", example = "345 or 545 or 323445")
    @Singular(ignoreNullCollections = true)
    private List<Integer> numbers = new ArrayList<>();

    @Parameter(description = "Unique key for service points which is used in the customer information.")
    @Singular(ignoreNullCollections = true)
    private List<String> servicePointSloids = new ArrayList<>();

    @Parameter(description = "List of UIC Country codes. The UIC Country code applies to the country of the service point number")
    @Singular(ignoreNullCollections = true)
    private List<Integer> servicePointUicCountryCodes = new ArrayList<>();

    @Parameter(description ="Number of a service point which is provided by DiDok for Switzerland. It is part of the unique key for"
            + " service points.")
    @Singular(ignoreNullCollections = true)
    private List<Integer> servicePointNumbersShorts = new ArrayList<>();

    @Parameter(description = "DiDok-Number of the ServicePoint formerly known as UIC-Code, combination of uicCountryCode and numberShort.")
    @Singular(ignoreNullCollections = true)
    private List<Integer> servicePointNumbers = new ArrayList<>();

    @Parameter(description = "Swiss Business Organisation ID (SBOID).")
    @Singular(ignoreNullCollections = true)
    private List<String> sboids = new ArrayList<>();

    public List<ServicePointNumber> getServicePointNumbers() {
        return servicePointNumbers.stream().map(ServicePointNumber::ofNumberWithoutCheckDigit).toList();
    }

}
