package com.example.user.dto;

public class ErrorResponseDto {
    private long timestamp;
    private int codigo;
    private String detail;

    public ErrorResponseDto(long timestamp, int codigo, String detail) {
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