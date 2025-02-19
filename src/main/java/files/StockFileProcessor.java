package files;

import database.DatabaseManager;
import models.Product;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utils.ConfigLoader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class StockFileProcessor {

    // Processes all XML stock files found in the input directory
    public static void processStockFiles() {
        String inputPath = ConfigLoader.getProperty("stocks.input.path");
        String processedPath = ConfigLoader.getProperty("stocks.processed.path");

        File inputDir = new File(inputPath);
        File[] files = inputDir.listFiles((dir, name) -> name.endsWith(".xml"));

        // Checks if there are any XML files to process
        if (files == null || files.length == 0) {
            System.out.println("There are no XML files to process.");
            return;
        }

        // Iterates through each XML file, parsing and updating the database
        for (File file : files) {
            List<Product> products = parseStockFile(file);
            if (!products.isEmpty()) {
                updateStockInDatabase(products);
                moveProcessedFile(file, processedPath);
            }
        }
    }

    // Parses an XML stock file and extracts product data
    private static List<Product> parseStockFile(File file) {
        List<Product> products = new ArrayList<>();

        try{
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document doc = builder.parse(file);
            doc.getDocumentElement().normalize();

            NodeList productNodes = doc.getElementsByTagName("stock");

            // Extracts product details from each stock entry
            for (int i = 0; i < productNodes.getLength(); i++) {
                Element element = (Element) productNodes.item(i);
                int id = Integer.parseInt(element.getElementsByTagName("product_id").item(0).getTextContent());

                // It is unclear from the documentation whether the product name will be present in the XML.
                // If <name> is missing or empty, fallback to using product_id as the name.
                Node nameNode = element.getElementsByTagName("name").item(0);
                String name = (nameNode != null && nameNode.getTextContent().trim().length() > 0)
                        ? nameNode.getTextContent()
                        : String.valueOf(id);

                int stock = Integer.parseInt(element.getElementsByTagName("quantity").item(0).getTextContent());

                products.add(new Product(id, name, stock));
            }
        } catch (Exception e){
            e.printStackTrace();
        }
        return products;
    }

    // Updates the stock information in the database
    private static void updateStockInDatabase(List<Product> products){
        try (Connection connection = DatabaseManager.getConnection()) {
            String sql = "INSERT INTO products (id, name, stock) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE name=VALUES(name), stock = stock + VALUES(stock)";

            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                for (Product product : products) {
                    statement.setInt(1, product.getId());
                    statement.setString(2, product.getName());
                    statement.setInt(3, product.getStock());
                    statement.addBatch();
                }
                statement.executeBatch();
                System.out.println("Stocks have been updated in the database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Moves processed XML files to a separate directory
    private static void moveProcessedFile(File file, String processedPath){
        File processedDir = new File(processedPath);
        if (!processedDir.exists()) {
            processedDir.mkdirs();
        }

        File destFile = new File(processedDir, file.getName());
        if (file.renameTo(destFile)) {
            System.out.println("The processed file has been moved: " + file.getName());
        } else {
            System.out.println("Error moving file:  " + file.getName());
        }
    }
}
