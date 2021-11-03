package ch.sbb.timetable.field.number.versioning.annotation;

import ch.sbb.timetable.field.number.versioning.model.Versionable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specify that the annotated class is candidate to be versioned.
 * Note that an Object can be versioned only if implements
 * the interface {@link Versionable}
 * and the fields to be versioned are annotated with {@link AtlasVersionableProperty}
 *
 * e.g.
 * <pre>
 * {@code
 *    @AtlasVersionable
 *    public static class VersionableObject implements Versionable {
 *
 *    private LocalDate validFrom;
 *    private LocalDate validTo;
 *    private Long id;
 *
 *    private String name;
 *
 *    @AtlasVersionableProperty
 *    private String property;
 *
 *    @AtlasVersionableProperty(relationType = RelationType.ONE_TO_MANY, relationsFields = {"value"})
 *    private List<Relation> oneToManyRelation;
 *
 *      public static class Relation {
 *        private Long id;
 *        private String value;
 *      }
 *
 *    }
 * }
 * </pre>
 *
 * In the above example for the Object VersionableObject the properties <b>property</b>
 * and <b>oneToManyRelation</b> are marked to be versioned. The other properties are ignored
 * by the versioning.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface AtlasVersionable {

}
