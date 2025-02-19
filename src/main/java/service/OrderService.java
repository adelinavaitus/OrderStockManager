package service;

import database.DatabaseManager;
import models.Order;
import models.OrderResponse;
import models.OrderStatus;
import models.Product;
import rabbitmq.OrderResponseProducer;

import java.sql.*;

// Service responsible for processing orders, including saving, stock checking, and updating
public class OrderService {

    // Processes the given order, checking stock and saving the order to the database
    public void processOrder(Order order){
        saveOrderToDatabase(order);

        boolean isStockAvailable = checkStockForOrder(order);

        // Creates an order response object
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setOrderId((order.getId()));

        // Sets the order status based on stock availability
        if(isStockAvailable){
            orderResponse.setStatus(OrderStatus.RESERVED);
            updateStock(order);
        }
        else{
            orderResponse.setStatus(OrderStatus.INSUFFICIENT_STOCKS);
        }

        // Sends the order response message to the RabbitMQ queue
        OrderResponseProducer.sendResponse(orderResponse);
    }

    // Saves the order and its products to the database
    private void saveOrderToDatabase(Order order){
        String insertOrderSQL = "INSERT INTO orders ( name, client, status) VALUES (?, ?, ?)";
        String insertOrderProductSQL = "INSERT INTO order_products (order_id, product_id, quantity) VALUES (?, ?, ?)";

        try (Connection dbConnection = DatabaseManager.getConnection();
             PreparedStatement orderStatement = dbConnection.prepareStatement(insertOrderSQL, Statement.RETURN_GENERATED_KEYS);
             PreparedStatement orderProductStatement = dbConnection.prepareStatement(insertOrderProductSQL)) {

            // Inserts the order details
            orderStatement.setString(1, order.toString());
            orderStatement.setString(2, order.getClient());
            orderStatement.setString(3, (checkStockForOrder(order) ? OrderStatus.RESERVED : OrderStatus.INSUFFICIENT_STOCKS).toString());

            // Executes the order insert
            orderStatement.executeUpdate();

            // Retrieves the generated order ID
            ResultSet generatedKeys = orderStatement.getGeneratedKeys();
            if(generatedKeys.next()){
                order.setId(generatedKeys.getInt(1));
            } else {
                throw new SQLException("Creating order failed, no ID obtained");
            }

            // Inserts each product in the order in the order_products table
            for(Product product: order.getProducts()){
                orderProductStatement.setInt(1, order.getId());
                orderProductStatement.setInt(2, product.getId());
                orderProductStatement.setInt(3, product.getStock());

                // Executes the product insert
                orderProductStatement.executeUpdate();
            }

            System.out.println("Order and products saved to database. Order id:  " + order.getId());
        }catch (Exception e){
            System.out.println("Error saving order to database " + e.getMessage());
            e.printStackTrace();
        }

    }

    // Checks if stock is sufficient for all products in the order
    private boolean checkStockForOrder(Order order){
        try(Connection dbConnection = DatabaseManager.getConnection()){
            for(Product product: order.getProducts()){
                if(!isStockSufficient(dbConnection, product)){
                    return false;    // Returns false if any product has insufficient stock
                }
            }
            return true;    // Returns true if all products have sufficient stock
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;    // Returns false if an error occurs
    }

    // Checks if stock is sufficient for a single product in the database
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

    //Updates the stock in the database by deducting the quantity of products in the order
    private void updateStock(Order order){
        String sql = "UPDATE products SET stock = stock - ? WHERE id = ?";

        try(Connection dbConnection = DatabaseManager.getConnection();
            PreparedStatement preparedStatement1 = dbConnection.prepareStatement(sql)){

            // Updates stock for each product in the order
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
