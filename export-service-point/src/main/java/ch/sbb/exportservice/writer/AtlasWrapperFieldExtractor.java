package ch.sbb.exportservice.writer;

import static ch.sbb.exportservice.utils.StringUtils.SEMICOLON;

import ch.sbb.exportservice.utils.StringUtils;
import java.util.ArrayList;
import java.util.List;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class AtlasWrapperFieldExtractor<T> extends BeanWrapperFieldExtractor<T> {

  private final String[] names;

  public AtlasWrapperFieldExtractor(String[] names) {
    this.names = names;
  }

  @Override
  public Object[] extract(T item) {
    List<Object> values = new ArrayList<>();
    BeanWrapper bw = new BeanWrapperImpl(item);
    for (String propertyName : this.names) {
      Object propertyValue = bw.getPropertyValue(propertyName);
      if(String.valueOf(propertyValue).contains(SEMICOLON)){
        String prunedValue = StringUtils.replaceSemiColonWithColon(String.valueOf(propertyValue));
        values.add(prunedValue);
      }else {
        values.add(propertyValue);
      }
    }
    return values.toArray();
  }

}
