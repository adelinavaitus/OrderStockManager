package rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import models.OrderResponse;
import utils.ConfigLoader;

// Produces messages to send order response data to a RabbitMQ queue
public class OrderResponseProducer {

    // The name of the queue for sending order responses
    private static final String RESPONSE_QUEUE = ConfigLoader.getProperty("rabbitmq.queue.orders_response");

    // Sends the order response to the RabbitMQ queue
    public static void sendResponse(OrderResponse orderResponse) {
        try (Connection connection = RabbitMQConnection.getConnection();
             Channel channel = connection.createChannel()) {

            // Declares the queue in case it doesn't exist
            channel.queueDeclare(RESPONSE_QUEUE, false, false, false, null);

            // Converts the order response object into a JSON string
            ObjectMapper objectMapper = new ObjectMapper();
            String message = objectMapper.writeValueAsString(orderResponse);

            // Sends the response message to the queue
            channel.basicPublish("", RESPONSE_QUEUE, null, message.getBytes());
            System.out.println("Sent response: " + message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
