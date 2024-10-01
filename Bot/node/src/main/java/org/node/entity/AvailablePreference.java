package org.node.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("available_preferences")
public class AvailablePreference {

    @Id
    private Long id;
    private String preference;
}
