package com.example.sudoku.game

import androidx.lifecycle.MutableLiveData

class SudokuGame {

    //Dung de nhan select cell data live tu game
    var selectCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLivcData = MutableLiveData<List<Cell>>()

    //Dung de nhan live data trong trang thai notes
    val isNotesLiveData = MutableLiveData<Boolean>()
    val highlightKeysLiveData = MutableLiveData<Set<Int>>()

    private var selectRow = -1
    private var selectColumn = -1

    private val board: Board

    //Khoi tao cell va live data
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
        //Return if 'there's no selected cell' or 'selected cell is a starting cell'
        if (selectRow == -1 || selectColumn == -1) return
        if (!board.getCell(selectRow, selectColumn).isStartingCell) return

        board.getCell(selectRow, selectColumn).value = number
        cellsLivcData.postValue(board.cells)
    }

    //Update select cell
    fun updateSelectCell(row: Int, col: Int){
        if (!board.getCell(row, col).isStartingCell) {
            selectRow = row
            selectColumn = col
            selectCellLiveData.postValue(Pair(row, col))
        }
    }
}