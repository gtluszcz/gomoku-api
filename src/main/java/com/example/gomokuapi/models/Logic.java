package com.example.gomokuapi.models;

import java.util.ArrayList;

public class Logic {
    private Cell[][] board;
    private ArrayList<Cell> occupied = new ArrayList<>(0);

    public Logic(int xlenght, int ylength){
        this.board = new Cell[ylength][xlenght];
        for (int i = 0; i < ylength; i++) {
            for (int j = 0; j < xlenght; j++) {
                this.board[i][j] = new Cell(i,j,null);
            }
        }
    }

    public Boolean setCell(Integer x, Integer y, Integer value){
        if (x < 0 || y < 0 || x >= this.board.length || y >= this.board[0].length)
            return false;

        if (value==null)
            return false;

        this.board[x][y].setValue(value);
        occupied.add(this.board[x][y]);
        return true;
    }

    public Cell intelligentMove(){
            AI computer = new AI(this.board,this.occupied);
            return computer.computeMove();
    }

}
