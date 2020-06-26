package com.example.sudoku.game

import androidx.lifecycle.MutableLiveData

class SudokuGame {

    //Dung de nhan select cell data live tu game
    val selectCellLiveData = MutableLiveData<Pair<Int, Int>>()
    val cellsLivcData = MutableLiveData<List<Cell>>()

    private var selectRow = -1
    private var selectColumn = -1

    private val board: Board

    //Khoi tao cell va live date
    init{
        val cells = List(9 * 9) {
            i -> Cell(i / 9, i % 9, i % 9)
        }

        board = Board(9, cells)

        selectCellLiveData.postValue(Pair(selectRow, selectColumn))
        cellsLivcData.postValue(board.cells)
    }

    //Dua vao value cho cells
    fun handleInput(number: Int) {
        if (selectRow == -1 || selectColumn == -1) return

        board.getCell(selectRow, selectColumn).value = number
        cellsLivcData.postValue(board.cells)
    }

    //Update select cell
    fun updateSelectCell(row: Int, col: Int){
        selectRow = row
        selectColumn = col
        selectCellLiveData.postValue(Pair(row, col))
    }
}