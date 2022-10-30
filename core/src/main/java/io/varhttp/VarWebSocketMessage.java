package io.varhttp;

public class VarWebSocketMessage {

    private String client;
    private String data;

    public VarWebSocketMessage(String client, String data) {
        this.client = client;
        this.data = data;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
