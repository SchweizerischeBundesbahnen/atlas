package ch.sbb.atlas.servicepointdirectory;

import ch.sbb.atlas.servicepoint.ServicePointNumber;
import ch.sbb.atlas.servicepointdirectory.entity.ServicePointVersion;
import lombok.experimental.UtilityClass;

import java.time.LocalDate;

@UtilityClass
public class ServicePointVersionsTimelineTestData {

    public static ServicePointVersion getAargauServicePointVersionModel0() {
        return ServicePointVersion.builder()
                .number(ServicePointNumber.ofNumberWithoutCheckDigit(8034510))
                .sloid("ch:1:sloid:18771")
                .designationLong("designation long 1")
                .designationOfficial("Aargau Strasse")
                .validFrom(LocalDate.of(2008, 12, 11))
                .validTo(LocalDate.of(2009, 8, 10))
                .build();
    }

    public static ServicePointVersion getAargauServicePointVersionModel1() {
        return ServicePointVersion.builder()
                .number(ServicePointNumber.ofNumberWithoutCheckDigit(8034510))
                .sloid("ch:1:sloid:18771")
                .designationLong("designation long 1")
                .designationOfficial("Aargau Strasse")
                .validFrom(LocalDate.of(2010, 12, 11))
                .validTo(LocalDate.of(2011, 8, 10))
                .build();
    }

    public static ServicePointVersion getAargauServicePointVersionModel2() {
        return ServicePointVersion.builder()
                .number(ServicePointNumber.ofNumberWithoutCheckDigit(8034510))
                .sloid("ch:1:sloid:18771")
                .designationLong("designation long 1")
                .designationOfficial("Aargau Strasse")
                .validFrom(LocalDate.of(2011, 8, 11))
                .validTo(LocalDate.of(2012, 8, 10))
                .build();
    }

    public static ServicePointVersion getAargauServicePointVersionModel3() {
        return ServicePointVersion.builder()
                .number(ServicePointNumber.ofNumberWithoutCheckDigit(8034510))
                .sloid("ch:1:sloid:18771")
                .designationLong("designation long 1")
                .designationOfficial("Aargau Strasse")
                .validFrom(LocalDate.of(2012, 8, 11))
                .validTo(LocalDate.of(2013, 8, 10))
                .build();
    }

    public static ServicePointVersion getAargauServicePointVersionModel4() {
        return ServicePointVersion.builder()
                .number(ServicePointNumber.ofNumberWithoutCheckDigit(8034510))
                .sloid("ch:1:sloid:18771")
                .designationLong("designation long 1")
                .designationOfficial("Aargau Strasse")
                .validFrom(LocalDate.of(2013, 8, 14))
                .validTo(LocalDate.of(2014, 8, 14))
                .build();
    }

    public static ServicePointVersion getAargauServicePointVersionModel5() {
        return ServicePointVersion.builder()
                .number(ServicePointNumber.ofNumberWithoutCheckDigit(8034510))
                .sloid("ch:1:sloid:18771")
                .designationLong("designation long 1")
                .designationOfficial("Aargau Strasse")
                .validFrom(LocalDate.of(2014, 8, 18))
                .validTo(LocalDate.of(2015, 8, 18))
                .build();
    }

    public static ServicePointVersion getAargauServicePointVersionModel6() {
        return ServicePointVersion.builder()
                .number(ServicePointNumber.ofNumberWithoutCheckDigit(8034510))
                .sloid("ch:1:sloid:18771")
                .designationLong("designation long 1")
                .designationOfficial("Aargau Strasse")
                .validFrom(LocalDate.of(2015, 8, 20))
                .validTo(LocalDate.of(2016, 8, 20))
                .build();
    }

    public static ServicePointVersion getAargauServicePointVersionModel7() {
        return ServicePointVersion.builder()
                .number(ServicePointNumber.ofNumberWithoutCheckDigit(8034510))
                .sloid("ch:1:sloid:18771")
                .designationLong("designation long 1")
                .designationOfficial("Aargau Strasse")
                .validFrom(LocalDate.of(2016, 8, 21))
                .validTo(LocalDate.of(2017, 8, 20))
                .build();
    }

    public static ServicePointVersion getAargauServicePointVersionModel8() {
        return ServicePointVersion.builder()
                .number(ServicePointNumber.ofNumberWithoutCheckDigit(8034510))
                .sloid("ch:1:sloid:18771")
                .designationLong("designation long 1")
                .designationOfficial("Aargau Strasse")
                .validFrom(LocalDate.of(2017, 8, 21))
                .validTo(LocalDate.of(2018, 8, 20))
                .build();
    }

    public static ServicePointVersion getAargauServicePointVersionModel9() {
        return ServicePointVersion.builder()
                .number(ServicePointNumber.ofNumberWithoutCheckDigit(8034510))
                .sloid("ch:1:sloid:18771")
                .designationLong("designation long 1")
                .designationOfficial("Aargau Strasse")
                .validFrom(LocalDate.of(2018, 8, 25))
                .validTo(LocalDate.of(2019, 8, 20))
                .build();
    }

    public static ServicePointVersion getAargauServicePointVersionModel10() {
        return ServicePointVersion.builder()
                .number(ServicePointNumber.ofNumberWithoutCheckDigit(8034510))
                .sloid("ch:1:sloid:18771")
                .designationLong("designation long 1")
                .designationOfficial("Aargau Strasse")
                .validFrom(LocalDate.of(2019, 8, 25))
                .validTo(LocalDate.of(2020, 8, 20))
                .build();
    }



}