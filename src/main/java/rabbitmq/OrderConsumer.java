package rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import database.DatabaseManager;
import models.Order;
import models.OrderResponse;
import models.OrderStatus;
import models.Product;
import utils.ConfigLoader;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class OrderConsumer {

    private static final String QUEUE_NAME = ConfigLoader.getProperty("rabbitmq.queue.orders");

    public void startConsuming() {
        try (Connection connection = RabbitMQConnection.getConnection();
             Channel channel = connection.createChannel()) {


            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println("Started consuming from the queue...");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                try {
                    String message = new String(delivery.getBody(), "UTF-8");
                    System.out.println("Received message: " + message);
                    processOrder(message);
                } catch (Exception e) {
                    System.err.println("Error processing message: " + e.getMessage());
                    e.printStackTrace();
                }
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
            System.out.println("Waiting for messages...");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processOrder(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Order order = objectMapper.readValue(message, Order.class);
            System.out.println("Processing order: " + order);

            boolean isStockAvailable = checkStockForOrder(order);

            OrderResponse orderResponse = new OrderResponse();
            orderResponse.setOrderId(order.getId());
            if (isStockAvailable) {
                orderResponse.setStatus(OrderStatus.RESERVED);
            } else {
                orderResponse.setStatus(OrderStatus.INSUFFICIENT_STOCKS);
            }

            OrderResponseProducer.sendResponse(orderResponse);

        } catch (Exception e) {
            System.err.println("Error processing order: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public boolean checkStockForOrder(Order order) {
        try (java.sql.Connection databaseConnection = DatabaseManager.getConnection()) {
            for (Product product : order.getProducts()) {
                if (!isStockSufficient(databaseConnection, product)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isStockSufficient(java.sql.Connection connection, Product product) {
        String sqlQuery = "SELECT stock FROM products WHERE id = ?";

        try (PreparedStatement statement = connection.prepareStatement(sqlQuery)) {
            statement.setInt(1, product.getId());
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                int availableStock = resultSet.getInt("stock");
                return availableStock >= product.getStock();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
