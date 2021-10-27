package ch.sbb.timetable.field.number.versioning.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class Property {

  private String key;

  private String value;

  private Entity oneToOne;

  private List<Entity> oneToMany;

  public boolean hasOneToManyRelation(){
    return this.oneToMany != null && !this.oneToMany.isEmpty();
  }

  public boolean hasOneToOneRelation(){
    return this.oneToOne != null;
  }

  public boolean isNotEmpty(){
    if(this.key != null){
      if(this.value != null){
        return true;
      }
      if(this.oneToOne != null){
        return true;
      }
      return this.oneToMany != null && !this.oneToMany.isEmpty();
    }
    return false;
  }
}
