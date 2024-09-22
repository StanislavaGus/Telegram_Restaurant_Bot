package org.example.services;

public class SearchParameters {
    private String query;
    private String allergy;
    private String near;            // например: Санкт-Петербург
    private Boolean openNow;        // true или false
    private String sort;            // DISTANCE, RELEVANCE и т.д.
    private Integer minPrice;       // минимальная цена
    private Double area;            // площадь поиска
    private Integer maxPrice;       // максимальная цена
    private Integer limit;          // 0 ... 100 000
    private Integer categories;     // категории
    private Double latitude;        // широта
    private Double longitude;       // долгота

    // Конструктор с инициализацией значений по умолчанию
    public SearchParameters() {
        this.query = "";
        this.allergy = "";
        this.near = "";
        this.openNow = false;
        this.sort = "RELEVANCE";
        this.minPrice = 1;
        this.area = 500.0;
        this.maxPrice = 4;
        this.limit = 5;
    }

    // Геттеры и сеттеры для всех полей
    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getAllergy() {
        return allergy;
    }

    public void setAllergy(String allergy) {
        this.allergy = allergy;
    }

    public String getNear() {
        return near;
    }

    public void setNear(String near) {
        this.near = near;
    }

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Integer getMinPrice() {
        return minPrice;
    }

    public void setMinPrice(Integer minPrice) {
        this.minPrice = minPrice;
    }

    public Double getArea() {
        return area;
    }

    public void setArea(Double area) {
        this.area = area;
    }

    public Integer getMaxPrice() {
        return maxPrice;
    }

    public void setMaxPrice(Integer maxPrice) {
        this.maxPrice = maxPrice;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getCategories() {
        return categories;
    }

    public void setCategories(Integer categories) {
        this.categories = categories;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    // Метод для формирования строки с параметрами GET-запроса
    public String toQueryString() {
        return "query=" + query + "&near=" + near + "&open_now=" + openNow + "&sort=" + sort +
                "&min_price=" + minPrice + "&area=" + area + "&max_price=" + maxPrice +
                "&limit=" + limit + "&categories=" + categories + "&ll=" + latitude + "," + longitude;
    }
}
