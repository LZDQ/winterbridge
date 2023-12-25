package net.lzdq.winterbridge.client.handler.httpclass;

class SortRequest {
    public String type;
    public String[] hotbar;

    public SortRequest() {
        this.type = "sort";
        this.hotbar = new String[9];
    }
}
