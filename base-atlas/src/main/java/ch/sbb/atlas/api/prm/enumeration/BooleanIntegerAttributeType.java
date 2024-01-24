package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

@Schema(enumAsRef = true, example = "YES")
@Getter
@RequiredArgsConstructor
public enum BooleanIntegerAttributeType {
    FALSE(0),
    TRUE(1);

    private final Integer rank;

    public static Boolean of(Integer value) {
        if (value == null) {
            return null;
        }
        return Stream.of(BooleanIntegerAttributeType.values())
                .filter(i -> i.getRank().equals(value))
                .findFirst()
                .map(i -> i == TRUE)
                .orElseThrow(() -> new IllegalArgumentException("Invalid value for Boolean conversion: " + value));
    }
}
