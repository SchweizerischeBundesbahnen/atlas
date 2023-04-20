package ch.sbb.atlas.searching.specification;

import jakarta.persistence.criteria.Path;
import jakarta.persistence.criteria.Root;
import lombok.experimental.UtilityClass;

@UtilityClass
public class NestedPath {

  static <T> Path<T> get(Root<T> root, String deepPath) {
    Path<T> path = root;
    for (String part : deepPath.split("\\.")) {
      path = path.get(part);
    }
    return path;
  }
}
