package ch.sbb.atlas.amazon.service;

/**
 * Value object representing the length of a file in bytes. A content length can never be negative; this is enforced at
 * construction time so that an invalid length cannot silently propagate into an HTTP {@code Content-Length} header.
 */
public record ContentLength(long value) {

  public ContentLength {
    if (value < 0) {
      throw new IllegalArgumentException("Content length must not be negative but was " + value);
    }
  }

  public static ContentLength of(long value) {
    return new ContentLength(value);
  }

}
