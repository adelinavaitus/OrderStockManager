package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.xml.bind.annotation.XmlElement;

public class Product {

    @XmlElement(name = "product_id")
    @JsonProperty("product_id")
    private int id;

    private String name;

    @XmlElement(name = "quantity")
    @JsonProperty("quantity")
    private int stock;

    public Product() {}
    public Product(int id, String name, int stock){
        this.id = id;
        this.name = name;
        this.stock = stock;
    }

    public int getId() {
        return this.id;
    }

    public String getName() {
        return this.name;
    }

    public int getStock(){
        return this.stock;
    }
}
