package models;

public class Product {

    private int id;
    private String name;
    private int stock;

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
