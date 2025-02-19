package models;

import com.fasterxml.jackson.annotation.JsonProperty;

// Represents the response for an order, including its status and any error message
public class OrderResponse {

    @JsonProperty("order_id")
    private int orderId;

    @JsonProperty("order_status")
    private OrderStatus status;

    @JsonProperty("error_message")
    private String errorMessage;

    public int getOrderId(){
        return orderId;
    }

    public OrderStatus getStatus(){
        return status;
    }

    public String getErrorMessage(){
        return errorMessage;
    }

    public void setOrderId(int orderId){
        this.orderId = orderId;
    }

    public void setStatus(OrderStatus status){
        this.status = status;
    }

    public void setErrorMessage(String errorMessage){
        this.errorMessage = errorMessage;
    }
}
