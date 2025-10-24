package com.mycompany.tp1sabri.llm;
public class RequeteException extends Exception {

    private int status;
    private String requeteJson;
    public RequeteException() {
        super();
    }
    public RequeteException(String message) {
        super(message);
    }
    public RequeteException(String message, String requeteJson) {
        super(message);
        this.requeteJson = requeteJson;
    }
    public RequeteException(int status, String message, String requeteJson) {
        super(message);
        this.status = status;
        this.requeteJson = requeteJson;
    }
    public int getStatus() {
        return status;
    }
    public String getRequeteJson() {
        return requeteJson;
    }
}