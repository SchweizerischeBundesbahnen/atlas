package ch.sbb.atlas.model;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ResponseCodeDescription {

    public static final String ENTITY_ALREADY_UPDATED = "Entity has already been updated (etagVersion out of date)";

    public static final String VERSIONING_NOT_IMPLEMENTED = "Versioning scenario not implemented";

    public static final String NO_ENTITIES_WERE_MODIFIED = "No entities were modified after versioning execution";
}