package rabbitmq;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import models.OrderResponse;
import utils.ConfigLoader;

public class OrderResponseProducer {

    private static final String RESPONSE_QUEUE = ConfigLoader.getProperty("rabbitmq.queue.orders_response");

    public static void sendResponse(OrderResponse orderResponse) {
        try (Connection connection = RabbitMQConnection.getConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(RESPONSE_QUEUE, false, false, false, null);

            ObjectMapper objectMapper = new ObjectMapper();
            String message = objectMapper.writeValueAsString(orderResponse);

            channel.basicPublish("", RESPONSE_QUEUE, null, message.getBytes());
            System.out.println("Sent response: " + message);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
