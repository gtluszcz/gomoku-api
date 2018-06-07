package com.example.gomokuapi.models;

import javafx.util.Pair;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Task implements Callable<Pair<Cell,Long>>{

    private Cell[][] board;
    private ArrayList<Cell> occupied;
    private List<Cell> movesToDo;
    private Integer level;

    Task(Cell[][] board, ArrayList<Cell> occupied,Integer level,List<Cell> moves){
        this.board = new Cell[board.length][board[0].length];
        for (int i=0;i<board.length;i++){
            for (int j = 0; j < board[0].length; j++) {
                this.board[i][j] = new Cell(board[i][j]);
            }
        }

        this.level = level;

        this.occupied = new ArrayList<>(occupied.size());
        occupied.forEach(cell -> this.occupied.add(this.board[cell.x][cell.y]));

        this.movesToDo = new ArrayList<>(moves.size());
        moves.forEach(cell -> this.movesToDo.add(this.board[cell.x][cell.y]));

    }

    private void setCell(Cell cell, Integer type){
        this.board[cell.x][cell.y].setValue(type);
        this.occupied.add(this.board[cell.x][cell.y]);
    }

    private void removeCell(Cell cell){
        this.board[cell.x][cell.y].setValue(null);
        this.occupied.removeIf(cell2 -> cell2.y.equals(cell.y) && cell2.x.equals(cell.x));
    }


    private Long heuristic(){
        long score = 0;
        HashMap<Cell,Cell> found_pairs = new HashMap<>();

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


    private Pair<Cell,Long> maxByScore(ArrayList<Pair<Cell,Long>> map) throws ExecutionException, InterruptedException {
        Long score = Long.MIN_VALUE;
        Pair<Cell,Long> finalcell = new Pair<>(new Cell(99,99,1),score);
        for(Pair<Cell,Long> entry : map){
            if (score <= entry.getValue()){
                score = entry.getValue();
                finalcell = entry;
            }
        }
        return finalcell;
    }
    private Pair<Cell,Long> minByScore(ArrayList<Pair<Cell,Long>> map) throws ExecutionException, InterruptedException {
        Long score = Long.MAX_VALUE;
        Pair<Cell,Long> finalcell = new Pair<>(new Cell(99,99,1),score);
        for(Pair<Cell,Long> entry : map){
            if (score >= entry.getValue()){
                score = entry.getValue();
                finalcell = entry;
            }
        }
        return finalcell;
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

    @Override
    public Pair<Cell,Long> call() throws ExecutionException, InterruptedException {

        ArrayList<Pair<Cell,Long>> choices = new ArrayList<>();
        for (Cell cell : movesToDo) {
            choices.add(new Pair<>(cell,minMax(cell,this.level)));
        }


        return (this.level % 2 == 0) ? maxByScore(choices) : minByScore(choices);
    }

    private Long minMax(Cell cell, Integer level) throws ExecutionException, InterruptedException {
        if (level == 4) {
            if (level % 2 == 1)
                this.setCell(cell,1);
            else
                this.setCell(cell,0);
            Long num = this.heuristic();
            this.removeCell(cell);
            return num;
        }

        if (level % 2 == 1)
            this.setCell(cell,1);
        else
            this.setCell(cell,0);

        ArrayList<Cell> moves = this.getAllMoves();
        ArrayList<Long> result = new ArrayList<>();

        if (moves.size() > 40){
            ArrayList<ForkJoinTask<Pair<Cell,Long>>> choices = new ArrayList<>();

            for (List<Cell> list : chopped(moves,2)) {
                ForkJoinTask<Pair<Cell,Long>> cos = ForkJoinTask.adapt(new Task(this.board,this.occupied,1,list));
                choices.add(cos);
                cos.fork();
            }

            for (ForkJoinTask<Pair<Cell,Long>> tsk : choices){
                result.add(tsk.join().getValue());
            }
        }
        else {
            for (Cell cell2 : moves){
                result.add(minMax(cell2,level+1));
            }
        }

        this.removeCell(cell);

        return (level % 2 == 0) ? Collections.max(result) : Collections.min(result);
    }

}
