package com.example.entity;

/**
 * 影厅信息
 */
public class Hall {
    /** ID */
    private Integer id;
    /** 影厅名称 */
    private String name;
    /** 行数 */
    private Integer rowCount;
    /** 列数 */
    private Integer columnCount;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRowCount() {
        return rowCount;
    }

    public void setRowCount(Integer rowCount) {
        this.rowCount = rowCount;
    }

    public Integer getColumnCount() {
        return columnCount;
    }

    public void setColumnCount(Integer columnCount) {
        this.columnCount = columnCount;
    }
}
