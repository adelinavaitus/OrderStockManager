package service;

import database.DatabaseManager;
import models.Order;
import models.OrderResponse;
import models.OrderStatus;
import models.Product;
import rabbitmq.OrderResponseProducer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderService {

    public void processOrder(Order order){
        saveOrderToDatabase(order);

        boolean isStockAvailable = checkStockForOrder(order);

        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId((order.getId()));

        if(isStockAvailable){
            orderResponse.setStatus(OrderStatus.RESERVED);
            updateStock(order);
        }
        else{
            orderResponse.setStatus(OrderStatus.INSUFFICIENT_STOCKS);
        }

        OrderResponseProducer.sendResponse(orderResponse);
    }

    private void saveOrderToDatabase(Order order){
        String insertOrderSQL = "INSERT INTO orders (id, name, client, status) VALUES (?, ?, ?, ?)";
        String insertOrderProductSQL = "INSERT INTO order_products (order_id, product_id, quantity) VALUES (?, ?, ?)";

        try (Connection dbConnection = DatabaseManager.getConnection();
             PreparedStatement orderStatement = dbConnection.prepareStatement(insertOrderSQL);
             PreparedStatement orderProductStatement = dbConnection.prepareStatement(insertOrderProductSQL)) {

            orderStatement.setInt(1, order.getId());
            orderStatement.setString(2, order.toString());
            orderStatement.setString(3, order.getClient());
            orderStatement.setString(4, (checkStockForOrder(order) ? OrderStatus.RESERVED : OrderStatus.INSUFFICIENT_STOCKS).toString());

            orderStatement.executeUpdate();

            for(Product product: order.getProducts()){
                orderProductStatement.setInt(1, order.getId());
                orderProductStatement.setInt(2, product.getId());
                orderProductStatement.setInt(3, product.getStock());

                orderProductStatement.executeUpdate();
            }

            System.out.println("Order and products saved to database. Order id:  " + order.getId());
        }catch (Exception e){
            System.out.println("Error saving order to database " + e.getMessage());
            e.printStackTrace();
        }

    }

    private boolean checkStockForOrder(Order order){
        try(Connection dbConnection = DatabaseManager.getConnection()){
            for(Product product: order.getProducts()){
                if(!isStockSufficient(dbConnection, product)){
                    return false;
                }
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private boolean isStockSufficient(Connection dbConnection, Product product){
        String sql = "SELECT stock FROM products WHERE id = ?";

        try(PreparedStatement preparedStatement = dbConnection.prepareStatement(sql)){
            preparedStatement.setInt(1, product.getId());
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next()){
                int availableStock = resultSet.getInt("stock");
                return availableStock >= product.getStock();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    private void updateStock(Order order){
        String sql = "UPDATE products SET stock = stock - ? WHERE id = ?";

        try(Connection dbConnection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement1 = dbConnection.prepareStatement(sql)){

            for(Product product: order.getProducts()){
                preparedStatement1.setInt(1, product.getStock());
                preparedStatement1.setInt(2, product.getId());
                preparedStatement1.executeUpdate();
            }
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
