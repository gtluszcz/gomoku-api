package com.example.gomokuapi.models;

import javafx.util.Pair;

import java.util.*;

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

//        this.occupied.forEach(cell -> System.out.print(cell.toString()+ " "));
//        System.out.println(" omg 2");
    }

    private void setCell(Cell cell, Integer type){
//        if (type==null)
//            System.out.println("lol");
        this.board[cell.x][cell.y].setValue(type);
        this.occupied.add(this.board[cell.x][cell.y]);
//        System.out.println("dodaje "+this.board[cell.x][cell.y].value);
    }

    private void removeCell(Cell cell){
        this.board[cell.x][cell.y].setValue(null);
        this.occupied.removeIf(cell2 -> cell2.y.equals(cell.y) && cell2.x.equals(cell.x));
//        System.out.println("odejmuje "+this.board[cell.x][cell.y].value);
    }

    Cell computeMove(){
        ArrayList<Cell> moves = this.getAllMoves();

        HashMap<Cell,Long> choices = new HashMap<>();
        for (Cell cell : moves) {
            choices.put(cell,this.minmax(cell,1));
        }
        return maxByScore(choices);
    }

    private Long minmax(Cell cell, Integer level){
        if (level == 4) {
            if (level % 2 == 1)
                this.setCell(cell,1);
            else
                this.setCell(cell,0);
//            System.out.println("dół");
            Long num = this.heuristic();
            this.removeCell(cell);
            return num;
        }

        if (level % 2 == 1)
            this.setCell(cell,1);
        else
            this.setCell(cell,0);
        ArrayList<Cell> moves = this.getAllMoves();
//        System.out.println("tutaj");

        ArrayList<Long> choices = new ArrayList<>();
        for (Cell cell2 : moves) {
            choices.add(this.minmax(cell2, level + 1));
        }
//        System.out.println("wychodzę");
        this.removeCell(cell);

        return (level % 2 == 0) ? Collections.max(choices) : Collections.min(choices);
    }

    private Long heuristic(){
        long score = 0;
        HashMap<Cell,Cell> found_pairs = new HashMap<>();

//        System.out.print("occupied ");
//        occupied.forEach(cell -> System.out.print(cell.toString()+ " "));
//        System.out.println(" ");

        for (Cell cell : this.occupied) {
            for (int x = 0; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    //search all neighbours

                    //skip this cell
                    if (x == 0 && y == 0) {
                        continue;
                    }


                    int num1 = 0;
                    int num2 = 0;

                    //check if exists
                    if (!(cell.x + x < 0 || cell.y + y < 0 || cell.x + x >= this.board.length || cell.y + y >= this.board[0].length))
                        if (
                                this.board[cell.x + x][cell.y + y].value != null &&
                                this.board[cell.x + x][cell.y + y].value.equals(cell.value) &&
                                ! this._hasPair(found_pairs,new Pair<>(this.board[cell.x + x][cell.y + y],this.board[cell.x][cell.y])))
                    {
                        found_pairs.put(this.board[cell.x][cell.y],this.board[cell.x + x][cell.y + y]);
                        num1 = this.checkForCellsInDirection(this.board[cell.x + x][cell.y + y], cell.value, 1, x, y,found_pairs);
                    }

                    if (!(cell.x - x < 0 || cell.y - y < 0 || cell.x - x >= this.board.length || cell.y - y >= this.board[0].length))
                        if (
                                this.board[cell.x - x][cell.y - y].value != null &&
                                this.board[cell.x - x][cell.y - y].value.equals(cell.value) &&
                                ! this._hasPair(found_pairs,new Pair<>(this.board[cell.x - x][cell.y - y],this.board[cell.x][cell.y])))
                    {
                        found_pairs.put(this.board[cell.x][cell.y],this.board[cell.x - x][cell.y - y]);
                        num2 = this.checkForCellsInDirection(this.board[cell.x - x][cell.y - y], cell.value, 1, -x, -y,found_pairs);
                    }


                    long num = 0;
                    switch (num1+num2+1){
                        case 2:
                            num+=1;
                            break;
                        case 3:
                            num+=50;
                            break;
                        case 4:
                            num+=1000;
                            break;
                        case 5:
                            num+=100000;
                            break;
                        case 6:
                            num+=10000000;
                            break;
                    }

                    if (cell.value.equals(1))
                        score += num;
                    else
                        score -= num;
                }
            }
        }
        return score;
    }

    private boolean _hasPair(HashMap<Cell,Cell> pairs, Pair<Cell,Cell> pair) {
        for (Map.Entry<Cell, Cell> entry : pairs.entrySet()){
            if (
                    (entry.getKey() == pair.getKey() && entry.getValue() == pair.getValue()) ||
                    (entry.getKey() == pair.getValue() && entry.getValue() == pair.getKey())
                ){
                return true;
            }
        }
        return false;
    }

    private Cell maxByScore(HashMap<Cell,Long> map){
        Long score = Long.MIN_VALUE;
        Cell finalcell = new Cell(99,99,1);
        for(Map.Entry<Cell, Long> entry : map.entrySet()){
            if (score <= entry.getValue()){
                score = entry.getValue();
                finalcell = entry.getKey();
            }
        }
        return finalcell;
    }

    private ArrayList<Cell> getAllMoves() {

//        System.out.print("getAllMoves occupied ");
//        occupied.forEach(cell -> System.out.print(cell.toString()+ " "));
//        System.out.println(" ");

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
//        System.out.println("getmoves: "+newmoves.size());
        return newmoves;
    }

    private Integer checkForCellsInDirection(Cell cell,Integer type,Integer step,Integer dir_x,Integer dir_y,HashMap<Cell,Cell> found_pairs) {

        //check if exists
        if (cell.x + dir_x < 0 || cell.y + dir_y < 0 || cell.x + dir_x >= this.board.length || cell.y + dir_y >= this.board[0].length)
            return step;

        if (this.board[cell.x + dir_x][cell.y + dir_y].value == type ) {
            found_pairs.put(this.board[cell.x][cell.y] , this.board[cell.x + dir_x][cell.y + dir_y]);
            return this.checkForCellsInDirection(this.board[cell.x + dir_x][cell.y + dir_y], type, step + 1, dir_x, dir_y, found_pairs);
        }
        return step;
    }
}
