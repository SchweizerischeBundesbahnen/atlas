package ch.sbb.atlas.api.prm.enumeration;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.stream.Stream;

@Schema(enumAsRef = true, example = "YES")
@Getter
@RequiredArgsConstructor
public enum ContactPointAttributeType {
    INFORMATION_DESK(0),
    TICKET_COUNTER(1);

    private final Integer rank;

    public static StandardAttributeType from(Integer rank) {
        Stream<StandardAttributeType> stream = Arrays.stream(StandardAttributeType.values());
        return stream.filter(standardAttributeType -> standardAttributeType.getRank().equals(rank))
                .findFirst().orElseThrow(() -> new IllegalArgumentException("You have to provide one of the following value: " + stream.map(
                        StandardAttributeType::getRank)));
    }
}
