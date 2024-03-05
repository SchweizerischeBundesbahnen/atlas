package ch.sbb.importservice.entity;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
@AllArgsConstructor
@Getter
@Setter
public class RelationKeyId {
    private String sloid;
    private String rpSloid;
}
