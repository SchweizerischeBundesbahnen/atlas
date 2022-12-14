package ch.sbb.atlas.servicepointdirectory.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

/**
 * 0 = Haltekante
 * 1 = Haltestellenbereich
 */
@Schema(enumAsRef = true)
@Getter
@RequiredArgsConstructor
public enum TrafficPointElementType {

    BOARDING_PLATFORM(0),
    BOARDING_AREA(1),

    ;

    private final int value;

    public static TrafficPointElementType fromValue(int value) {
        return Stream.of(TrafficPointElementType.values()).filter(i -> i.getValue() == value).findFirst().orElseThrow();
    }
}
