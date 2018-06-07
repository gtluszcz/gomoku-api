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

        ArrayList<Pair<Cell,Long>> choices = new ArrayList<>();
        for (List<Cell> list : chopped(moves,2)) {
            choices.add(service.invoke(ForkJoinTask.adapt(new Task(this.board,this.occupied,1,list))));
        }


        try {
            return maxByScore(choices);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new Cell(0,0,0);
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

    static <T> List<List<T>> chopped(List<T> list, final int L) {
        List<List<T>> parts = new ArrayList<List<T>>();
        final int N = list.size();
        for (int i = 0; i < N; i += L) {
            parts.add(new ArrayList<T>(
                    list.subList(i, Math.min(N, i + L)))
            );
        }
        return parts;
    }

    private Cell maxByScore(ArrayList<Pair<Cell,Long>> map) throws ExecutionException, InterruptedException {
        Long score = Long.MIN_VALUE;
        Cell finalcell = new Cell(99,99,1);
        for(Pair<Cell,Long> entry : map){
            if (score <= entry.getValue()){
                score = entry.getValue();
                finalcell = entry.getKey();
            }
        }
        return finalcell;
    }


}
