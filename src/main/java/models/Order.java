package models;

import java.util.List;

public class Order {
    private int id;
    private String client;
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
