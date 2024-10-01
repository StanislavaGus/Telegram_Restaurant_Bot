package org.node.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table("visits")
public class Visit {
    @Id
    private Long id;
    private Long userId;
    private String restaurantId;
    private Boolean visited;

    public Visit() {}

    public Visit(Long userId, String restaurantId, Boolean visited) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.visited = visited;
    }
}
