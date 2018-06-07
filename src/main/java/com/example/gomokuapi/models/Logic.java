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
        System.out.println("Initial occupied size: "+occupied.size());
    }

    public Boolean setCell(Integer x, Integer y, Integer value){
        if (x < 0 || y < 0 || x >= this.board.length || y >= this.board[0].length)
            return false;

        if (value==null)
            return false;

        this.board[x][y].setValue(value);
        occupied.add(this.board[x][y]);
        this.occupied.forEach(cell -> System.out.print(cell.toString()+" "));
        System.out.println(" omg ");
        System.out.println(" koncze  dodawac");
        return true;
    }

    public Cell IntelligentMove(){
            System.out.println(" odpalam move");
            System.out.println(this.occupied);
            System.out.println(" omg 1");
            AI computer = new AI(this.board,this.occupied);
            return computer.computeMove();
    }

}
