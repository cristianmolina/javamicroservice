package com.example.user.infrastructure.exception;

//@Data
//@AllArgsConstructor
public class ErrorResponse {
    private long timestamp;
    private int codigo;
    private String detail;

    public ErrorResponse(long timestamp, int codigo, String detail) {
        this.timestamp = timestamp;
        this.codigo = codigo;
        this.detail = detail;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public int getCodigo() {
        return codigo;
    }

    public String getDetail() {
        return detail;
    }
}