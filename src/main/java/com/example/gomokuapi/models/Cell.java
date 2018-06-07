package com.example.gomokuapi.models;

public class Cell {
    Integer x;
    Integer y;
    Integer value;

    public Cell(int x, int y, Integer value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public Cell(Cell cell) {
        this(cell.x, cell.y, cell.value);
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }


    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    @Override
    public String toString() {
        String str = "{x: "+  x.toString() + ",y: "+ y.toString();
        if (value == null){
            str+=", value: null";
        }else {
            str+=", value: "+value;
        }

        str+="}";
        return str;
    }
}
