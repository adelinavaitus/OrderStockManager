package models;

public class OrderResponse {
    private int orderId;
    private OrderStatus status;
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
