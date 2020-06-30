package com.example.sudoku.game

import androidx.lifecycle.MutableLiveData

class SudokuGame {

    private var selectRow = -1
    private var selectColumn = -1

    private val board: Board

    //Dung de nhan select cell data live tu game
    var selectCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()

    //Dung de nhan live data trong trang thai notes
    var isNotesLiveData = MutableLiveData<Boolean>()
    var highlightKeysLiveData = MutableLiveData<Set<Int>>()
    private var isNotes = false

    //Khoi tao cell va live data
    init{
        val cells = List(9 * 9) {
            i -> Cell(i / 9, i % 9, i % 9)
        }

        board = Board(9, cells)

        selectCellLiveData.postValue(Pair(selectRow, selectColumn))
        cellsLiveData.postValue(board.cells)
        isNotesLiveData.postValue(isNotes)
    }

    //Dua vao value cho cells
    fun handleInput(number: Int) {
        //Return if 'there's no selected cell' or 'selected cell is a starting cell'
        if (selectRow == -1 || selectColumn == -1) return
        val cell = board.getCell(selectRow, selectColumn)
        if (cell.isStartingCell) return

        if (isNotes) {
            if (cell.notes.contains(number))
                cell.notes.remove(number)
            else
                cell.notes.add(number)
            highlightKeysLiveData.postValue(cell.notes)
        } else {
            cell.value = number
        }
        cellsLiveData.postValue(board.cells)
    }

    //Update select cell
    fun updateSelectCell(row: Int, col: Int){
        val cell = board.getCell(row, col)
        if (!cell.isStartingCell) {
            selectRow = row
            selectColumn = col
            selectCellLiveData.postValue(Pair(row, col))

            if (isNotes)
                highlightKeysLiveData.postValue(cell.notes)
        }
    }

    //Function change notes status
    fun notesStatusChange() {
        isNotes = !isNotes
        isNotesLiveData.postValue(isNotes)

        val currentNotes = if (isNotes)
                                board.getCell(selectRow, selectColumn).notes
                           else
                                setOf<Int>()
        highlightKeysLiveData.postValue(currentNotes)
    }

    //Function delete
    fun delete() {
        val cell = board.getCell(selectRow, selectColumn)
        if (isNotes) {
            cell.notes.clear()
            highlightKeysLiveData.postValue(setOf())
        } else
            cell.value = 0
        cellsLiveData.postValue(board.cells)
    }
}