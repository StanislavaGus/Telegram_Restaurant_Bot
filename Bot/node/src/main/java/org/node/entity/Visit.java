package org.node.entity;

public class Visit {
    private String restaurantId;
    private Boolean visited;

    public Visit(String restaurantId, Boolean visited) {
        this.restaurantId = restaurantId;
        this.visited = visited;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public Boolean getVisited() {
        return visited;
    }
}
