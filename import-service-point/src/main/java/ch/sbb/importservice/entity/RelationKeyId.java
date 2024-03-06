package ch.sbb.importservice.entity;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
@Setter
@ToString
public class RelationKeyId {
    private String sloid;
    private String rpSloid;
}
