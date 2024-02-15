package ch.sbb.exportservice.writer;

import static ch.sbb.exportservice.utils.StringUtils.NEW_LINE;
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
      pruneValue(values, propertyValue);
    }
    return values.toArray();
  }

  static void pruneValue(List<Object> values, Object propertyValue) {
    if(propertyValue != null){
      String valueToPrune = String.valueOf(propertyValue);
      if(valueToPrune.contains(NEW_LINE)){
        valueToPrune = StringUtils.removeNewLine(valueToPrune);
      }
      if(valueToPrune.contains(SEMICOLON)){
        valueToPrune = StringUtils.replaceSemiColonWithColon(valueToPrune);
      }
      values.add(valueToPrune.trim());
    }
    else {
      values.add(null);
    }
  }

}
