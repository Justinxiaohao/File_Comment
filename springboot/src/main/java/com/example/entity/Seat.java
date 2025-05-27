package com.example.entity;

/**
 * 座位信息
 */
public class Seat {
    /** ID */
    private Integer id;
    /** 行号 */
    private Integer row;
    /** 列号 */
    private Integer column;
    /** 影厅ID */
    private Integer hallId;
    /** 状态：0-不可用，1-可用 */
    private Integer status;
    /** 影厅名称 */
    private String hallName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getRow() {
        return row;
    }

    public void setRow(Integer row) {
        this.row = row;
    }

    public Integer getColumn() {
        return column;
    }

    public void setColumn(Integer column) {
        this.column = column;
    }

    public Integer getHallId() {
        return hallId;
    }

    public void setHallId(Integer hallId) {
        this.hallId = hallId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getHallName() {
        return hallName;
    }

    public void setHallName(String hallName) {
        this.hallName = hallName;
    }
}
