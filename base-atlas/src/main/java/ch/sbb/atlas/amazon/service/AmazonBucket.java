package ch.sbb.atlas.amazon.service;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AmazonBucket {
    EXPORT("export-files"),
    HEARING_DOCUMENT("hearing-documents");
    private final String property;
    public static AmazonBucket fromProperty(String value) {
        return Arrays.stream(values()).filter(i -> i.getProperty().equals(value)).findFirst().orElseThrow();
    }
}
