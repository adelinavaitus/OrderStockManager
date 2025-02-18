package rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import models.Order;

import service.OrderService;
import utils.ConfigLoader;

public class OrderConsumer {

    private static final String QUEUE_NAME = ConfigLoader.getProperty("rabbitmq.queue.orders");
    private static final OrderService orderService = new OrderService();

    public void startConsuming() {
        try (Connection connection = RabbitMQConnection.getConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println("Started consuming from the queue...");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                processOrder(message);
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void processOrder(String message) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Order order = objectMapper.readValue(message, Order.class);
            orderService.processOrder(order);
        } catch (Exception e) {
            System.err.println("Error processing order: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
