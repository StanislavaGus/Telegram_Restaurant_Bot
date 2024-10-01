package org.node.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("acceptable_allergies")
public class AcceptableAllergy {

    @Id
    private Long id;
    private String allergy;
}
