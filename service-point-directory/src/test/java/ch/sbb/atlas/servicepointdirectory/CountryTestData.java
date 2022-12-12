package ch.sbb.atlas.servicepointdirectory;

import ch.sbb.atlas.servicepointdirectory.entity.UicCountry;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CountryTestData {

    public static UicCountry SWITZERLAND = UicCountry.builder()
            .isoCode("CH")
            .uicCode(85)
            .nameDe("Schweiz")
            .nameFr("Suisse")
            .nameEn("Switzerland")
            .nameIt("Svizzera")
            .build();
}
