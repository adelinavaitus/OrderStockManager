# Order Stock Manager

## Overview 🌍 
This project is a multi-threaded order processing system that integrates with RabbitMQ for 
message queuing and processes stock updates from XML files. It consists of three main components:
1. StockFileProcessor - Reads stock data from XML files and updates the database.
2. OrderConsumer - Listens to the RabbitMQ queue and processes incoming orders.
3. OrderResponseProducer - Sends responses back to RabbitMQ after processing orders.

## Features ✨
- Processing of XML Files: The system monitors a directory for new XML files and updates product stock in the database.
- Multithreading Support: Orders are processed concurrently to handle multiple requests at once.
- RabbitMQ Integration: Orders are received and responses are sent through RabbitMQ queues.
- Database Management: Orders and stock updates are persisted in a relational database.

## Installation & Setup 🚀
### Prerequisites 📋 
Ensure you have the following installed:
1. Java 15
2. MySQL Database
3. RabbitMQ Server
4. Maven (for dependency management)

### Configuration ⚙️
Update the application.properties file with:
- Database connection settings
- RabbitMQ queue names
- File paths for XML processing
