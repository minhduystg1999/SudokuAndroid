package com.example.sudoku.game

import androidx.lifecycle.MutableLiveData

class SudokuGame {

    //Dung de nhan select cell data live tu game
    val selectCellLiveData = MutableLiveData<Pair<Int, Int>>()

    private var selectRow = -1
    private var selectColumn = -1

    init{
        selectCellLiveData.postValue(Pair(selectRow, selectColumn))
    }

    fun updateSelectCell(row: Int, col: Int){
        selectRow = row
        selectColumn = col
        selectCellLiveData.postValue(Pair(row, col))
    }
}