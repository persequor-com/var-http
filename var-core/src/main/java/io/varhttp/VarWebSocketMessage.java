package io.varhttp;

public class VarWebSocketMessage {

    private String data;

    public VarWebSocketMessage(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
