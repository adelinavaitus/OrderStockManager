package rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import utils.ConfigLoader;

// Manages the connection to a RabbitMQ server using the connection factory
public class RabbitMQConnection {
    private static ConnectionFactory factory = new ConnectionFactory();

    // Static block to initialize the connection factory with configuration values
    static {
        factory.setHost(ConfigLoader.getProperty("rabbitmq.host"));
        factory.setPort(Integer.parseInt(ConfigLoader.getProperty("rabbitmq.port")));
        factory.setUsername(ConfigLoader.getProperty("rabbitmq.username"));
        factory.setPassword(ConfigLoader.getProperty("rabbitmq.password"));
    }

    // Provides a connection to RabbitMQ using the configured factory settings
    public static Connection getConnection() throws Exception {
        return factory.newConnection();
    }
}
