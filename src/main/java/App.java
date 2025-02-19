import files.StockFileProcessor;
import rabbitmq.OrderConsumer;

// Main application class that initiates the processing of stock files and starts the order consumer
public class App {
    public static void main(String[] args) {
        System.out.println("Starting processing XML files...");
        StockFileProcessor.processStockFiles(); // Processes the stock files (parsing and updating stock in the database)
        System.out.println("Complete processing.");

        // Creates an instance of OrderConsumer to start consuming orders from RabbitMQ
        OrderConsumer consumer = new OrderConsumer();
        consumer.startConsuming();
    }
}
