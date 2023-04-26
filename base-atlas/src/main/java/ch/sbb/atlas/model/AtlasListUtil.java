package ch.sbb.atlas.model;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AtlasListUtil {

  public static <T> Collection<List<T>> getPartitionedSublists(List<T> list, int sublistMaxsize) {
    final AtomicInteger counter = new AtomicInteger(0);
    return list.stream()
        .collect(Collectors.groupingBy(s -> counter.getAndIncrement() / sublistMaxsize)).values();
  }
}
