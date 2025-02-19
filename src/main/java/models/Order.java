package models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;

import java.util.List;

public class Order {

    @JsonProperty("order_id")
    private int id;

    @JsonProperty("client_name")
    private String client;

    @JsonProperty("items")
    private List<Product> products;

    public Order() {}
    public Order(int id, String client, List<Product> products){
        this.id = id;
        this.client = client;
        this.products = products;
    }

    public int getId(){
        return this.id;
    }

    public String getClient(){
        return this.client;
    }

    public List<Product> getProducts(){
        return this.products;
    }
}
