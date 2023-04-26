package ch.sbb.atlas.model;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;

class AtlasListUtilTest {

  @Test
  void shouldSplitIntoBatchSizeLists() {
    List<Integer> list = IntStream.rangeClosed(1, 95).boxed().toList();

    Collection<List<Integer>> result = AtlasListUtil.getPartitionedSublists(list, 20);

    assertThat(result).hasSize(5);

    Iterator<List<Integer>> iterator = result.iterator();
    assertThat(iterator.next()).hasSize(20);
    assertThat(iterator.next()).hasSize(20);
    assertThat(iterator.next()).hasSize(20);
    assertThat(iterator.next()).hasSize(20);
    assertThat(iterator.next()).hasSize(15);
  }
}