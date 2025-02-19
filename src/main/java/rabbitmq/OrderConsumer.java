package rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import models.Order;

import service.OrderService;
import utils.ConfigLoader;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

// Consumes messages from RabbitMQ queue and processes orders asynchronously
public class OrderConsumer {

    // The name of the RabbitMQ queue for orders
    private static final String QUEUE_NAME = ConfigLoader.getProperty("rabbitmq.queue.orders");

    // Service to process the order data
    private static final OrderService orderService = new OrderService();

    // Executor service to handle multiple orders concurrently with a fixed thread pool
    private static final ExecutorService executorService = Executors.newFixedThreadPool(5);

    // Starts consuming messages from the RabbitMQ queue
    public void startConsuming() {
        try (Connection connection = RabbitMQConnection.getConnection();
             Channel channel = connection.createChannel()) {

            // Declares the queue to ensure it exists
            channel.queueDeclare(QUEUE_NAME, false, false, false, null);
            System.out.println("Started consuming from the queue...");

            // Callback to handle messages when they are delivered to the consumer
            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");

                // Submits the message processing task to the executor service
                executorService.submit(() -> processOrder(message));
            };

            // Starts consuming messages from the queue
            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {});

            // Keeps the consumer running indefinitely
            while (true) {
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Processes the order by deserializing the message and passing it to the order service
    private void processOrder(String message) {
        try {
            // Converts the message into an Order object
            ObjectMapper objectMapper = new ObjectMapper();
            Order order = objectMapper.readValue(message, Order.class);

            // Passes the order to the service for processing
            orderService.processOrder(order);
        } catch (Exception e) {
            // Catches any errors during order processing and prints an error message
            System.err.println("Error processing order: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
