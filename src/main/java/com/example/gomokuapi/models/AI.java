package com.example.gomokuapi.models;

import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.*;

class AI {
    private Cell[][] board;
    private ArrayList<Cell> occupied;

    AI(Cell[][] board, ArrayList<Cell> occupied){
        this.board = new Cell[board.length][board[0].length];
        for (int i=0;i<board.length;i++){
            for (int j = 0; j < board[0].length; j++) {
                this.board[i][j] = new Cell(board[i][j]);
            }
        }


        this.occupied = new ArrayList<>(occupied.size());
        occupied.forEach(cell -> this.occupied.add(this.board[cell.x][cell.y]));
    }

    private void setCell(Cell cell, Integer type){
        this.board[cell.x][cell.y].setValue(type);
        this.occupied.add(this.board[cell.x][cell.y]);
    }

    private void removeCell(Cell cell){
        this.board[cell.x][cell.y].setValue(null);
        this.occupied.removeIf(cell2 -> cell2.y.equals(cell.y) && cell2.x.equals(cell.x));
    }

    Cell computeMove(){
        ArrayList<Cell> moves = this.getAllMoves();
        ForkJoinPool service = new ForkJoinPool(4);

        HashMap<Cell,Long> choices = new HashMap<>();
        for (Cell cell : moves) {
            choices.put(cell,service.invoke(ForkJoinTask.adapt(new Task(this.board,this.occupied,cell,1))));
        }



        return maxByScore(choices);
    }

    private ArrayList<Cell> getAllMoves() {

        ArrayList<Cell> newmoves = new ArrayList<>();
        for (Cell cell : this.occupied) {
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    //search all neighbours

                    //skip this cell
                    if (x == 0 && y == 0) {
                        continue;
                    }

                    //check if exists
                    if (cell.x + x < 0 || cell.y + y < 0 || cell.x + x >= this.board.length || cell.y + y >= this.board[0].length)
                        continue;

                    //if can make move on this tile add to allmoves
                    if (this.board[cell.x + x][cell.y + y].value == null && !this.occupied.contains(this.board[cell.x + x][cell.y + y])) {
                        newmoves.add(this.board[cell.x + x][cell.y + y]);
                    }
                }
            }
        }
        return newmoves;
    }

    private Cell maxByScore(HashMap<Cell,Long> map){
        Long score = Long.MIN_VALUE;
        Cell finalcell = new Cell(99,99,1);
        for(Map.Entry<Cell,Long> entry : map.entrySet()){
            if (score <= entry.getValue()){
                score = entry.getValue();
                finalcell = entry.getKey();
            }
        }
        return finalcell;
    }


}
