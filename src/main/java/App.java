import files.StockFileProcessor;
import rabbitmq.OrderConsumer;

public class App {
    public static void main(String[] args) {
        System.out.println("Starting processing XML files...");
        StockFileProcessor.processStockFiles();
        System.out.println("Complete processing.");

        OrderConsumer consumer = new OrderConsumer();
        consumer.startConsuming();
    }
}
