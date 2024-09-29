package org.node.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("visits")
public class Visit {
    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("restaurant_id")
    private String restaurantId;

    @Column("visited")
    private Boolean visited;

    // Конструктор без параметров (нужен для Spring)
    public Visit() {}

    // Конструктор для инициализации полей
    public Visit(Long userId, String restaurantId, Boolean visited) {
        this.userId = userId;
        this.restaurantId = restaurantId;
        this.visited = visited;
    }

    // Геттеры и сеттеры
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void setRestaurantId(String restaurantId) {
        this.restaurantId = restaurantId;
    }

    public Boolean getVisited() {
        return visited;
    }

    public void setVisited(Boolean visited) {
        this.visited = visited;
    }
}
