package ch.sbb.atlas.servicepointdirectory.model;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ServicePointSearchResult {

    private ServicePointNumber number;

    private String designationOfficial;

}
