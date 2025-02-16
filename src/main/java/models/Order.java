package models;

import java.util.List;

public class Order {
    private int id;
    private String client;
    private List<Product> products;
    private OrderStatus status;

    public Order(int id, String client, List<Product> products, OrderStatus status){
        this.id = id;
        this.client = client;
        this.products = products;
        this.status = status;
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

    public OrderStatus getStatus(){
        return this.status;
    }
}
