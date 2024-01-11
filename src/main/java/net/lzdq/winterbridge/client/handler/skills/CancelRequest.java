package net.lzdq.winterbridge.client.handler.skills;

public class CancelRequest {
    public String type;
    public String cause;
    public CancelRequest(String cause){
        this.type = "cancel";
        this.cause = cause;
    }
}
