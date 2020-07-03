package com.example.sudoku.game

import androidx.lifecycle.MutableLiveData
import java.util.*

class SudokuGame {

    private var selectRow = -1
    private var selectColumn = -1

    private val blockSize = 3       //Block size = size of a block 3x3 = 3 cells on each direction
    private val boardSize = 9       //Board size = size of each direction = 9 cells

    private val board: Board

    //Dung de nhan select cell data live tu game
    var selectCellLiveData = MutableLiveData<Pair<Int, Int>>()
    var cellsLiveData = MutableLiveData<List<Cell>>()

    //Dung de tra ve gia tri live da hoan thanh game hay chua
    var isFinishedLiveData = MutableLiveData<Boolean>()

    //Dung de nhan live data trong trang thai notes
    var isNotesLiveData = MutableLiveData<Boolean>()
    var highlightKeysLiveData = MutableLiveData<Set<Int>>()
    private var isNotes = false


    // INIT AND CELLS & BUTTONS & STATUS FUNCTION REGION
    //Khoi tao cell va live data
    init{
        val cells = createSolution()

        board = Board(boardSize, cells)

        selectCellLiveData.postValue(Pair(selectRow, selectColumn))
        cellsLiveData.postValue(board.cells)
        isNotesLiveData.postValue(isNotes)
        isFinishedLiveData.postValue(isFinished())
    }

    //Function xu li input cho cells
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
            cell.isRightValue = isPossibleNumber(cell, number)
            cell.value = number
        }
        cellsLiveData.postValue(board.cells)
        isFinishedLiveData.postValue(isFinished())
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
        } else {
            cell.value = 0
            cell.isRightValue = false
        }
        cellsLiveData.postValue(board.cells)
    }

    //Function reset
    fun reset() {
        for (i in 0 until boardSize*boardSize)
            if (!board.cells[i].isStartingCell) {
                board.cells[i].value = 0
                board.cells[i].isRightValue = false
                board.cells[i].notes.clear()
            }
        cellsLiveData.postValue(board.cells)
    }

    //Function new game
    fun newGame() {
        board.cells = createSolution()
        selectRow = -1
        selectColumn = -1
        isNotes = false
        selectCellLiveData.postValue(Pair(selectRow, selectColumn))
        cellsLiveData.postValue(board.cells)
        isNotesLiveData.postValue(isNotes)
        isFinishedLiveData.postValue(isFinished())
    }

    //--------------------------------------------------------------------------------------------------------
    // GAME LOGIC REGION
    //Function generate solution
    fun createSolution(): List<Cell>{
        val solution = List(boardSize * boardSize) {
                i -> Cell(i / boardSize, i % boardSize, 0)
        }

        val numbers: List<Int> = listOf(
            0, 5, 9, 6, 1, 8, 4, 2, 7,
            7, 4, 2, 5, 3, 9, 8, 6, 0,
            1, 0, 8, 4, 7, 2, 9, 5, 3,
            4, 2, 0, 8, 9, 5, 7, 1, 6,
            5, 8, 7, 1, 6, 4, 3, 9, 2,
            6, 9, 1, 7, 2, 3, 5, 8, 4,
            2, 7, 5, 9, 4, 6, 1, 3, 8,
            8, 3, 4, 2, 5, 1, 6, 7, 9,
            0, 1, 6, 3, 8, 7, 2, 4, 5
        )

        val startingCell: List<Boolean> = listOf(
            false, true, true, true, true, true, true, true, true,
            true, true, true, true, true, true, true, true, false,
            true, false, true, true, true, true, true, true, true,
            true, true, false, true, true, true, true, true, true,
            true, true, true, true, true, true, true, true, true,
            true, true, true, true, true, true, true, true, true,
            true, true, true, true, true, true, true, true, true,
            true, true, true, true, true, true, true, true, true,
            false, true, true, true, true, true, true, true, true
        )

        for (i in 0 until boardSize * boardSize) {
            solution[i].value = numbers[i]
            solution[i].isStartingCell = startingCell[i]
        }
        return solution
    }

    //Function to check if Number is possible in selected cell
    fun isPossibleNumber(cell: Cell, number: Int): Boolean {
        return isPossibleColumn(cell.col, number) && isPossibleRow(cell.row, number) && isPossibleBlock(cell.row, cell.col, number)
    }

    //Function to check if Number is possible on column X
    fun isPossibleColumn(col: Int, number: Int): Boolean {
        for (row in 0 until boardSize)
            if (board.getCell(row, col).value == number)
                return false
        return true
    }

    //Function to check if Number is possible on row Y
    fun isPossibleRow(row: Int, number: Int): Boolean {
        for (col in 0 until boardSize)
            if (board.getCell(row, col).value == number)
                return false
        return true
    }

    //Function to check if Number is possible on its Block 3x3
    fun isPossibleBlock(row: Int, col: Int, number: Int): Boolean {
        val startRow = row / blockSize * blockSize      //tra ve gia tri Int nen se lay phan nguyen, tuong duong voi Row dau tien cua block can xet
        val startCol = col / blockSize * blockSize      //tuong duong voi Col dau tien cua block can xet

        for (r in startRow until (startRow + blockSize))
                for (c in startCol until (startCol + blockSize))
                    if (board.getCell(r, c).value == number)
                        return false
        return true
    }

    //Function to check if the game is finished or not
    fun isFinished(): Boolean {
        board.cells.forEach {
            if (!it.isStartingCell)
                if (!it.isRightValue)
                    return false
        }
        return true
    }
}