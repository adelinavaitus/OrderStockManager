package rabbitmq;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import utils.ConfigLoader;

public class RabbitMQConnection {
    private static ConnectionFactory factory = new ConnectionFactory();

    static {
        factory.setHost(ConfigLoader.getProperty("rabbitmq.host"));
        factory.setPort(Integer.parseInt(ConfigLoader.getProperty("rabbitmq.port")));
        factory.setUsername(ConfigLoader.getProperty("rabbitmq.username"));
        factory.setPassword(ConfigLoader.getProperty("rabbitmq.password"));
    }

    public static Connection getConnection() throws Exception {
        return factory.newConnection();
    }
}
