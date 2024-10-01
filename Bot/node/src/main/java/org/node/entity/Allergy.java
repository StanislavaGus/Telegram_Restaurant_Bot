package org.node.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("user_allergies")
public class Allergy {

    @Id
    private Long id;
    private Long userId;
    private String allergy;
}
