package rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import models.Order;

import service.OrderService;
import utils.ConfigLoader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class OrderConsumer {

    private static final String QUEUE_NAME = ConfigLoader.getProperty("rabbitmq.queue.orders");
    private static final OrderService orderService = new OrderService();

    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

    public void startConsuming() {
        try (Connection connection = RabbitMQConnection.getConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println("Started consuming from the queue...");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");

                executorService.submit(() -> processOrder(message));
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});

            while (true) {
                Thread.sleep(1000);
            }
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
