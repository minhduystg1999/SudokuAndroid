package com.example.sudoku.game

class Cell(
    val row: Int,
    val col: Int,
    var value: Int,
    var isStartingCell: Boolean = false,                //Cac cell duoc khoi tao ban dau va co san value, khong the thay doi
    var isRightValue: Boolean = false,                  //Gia tri khi nhap vao co phai la gia tri dung cua cell
    var notes: MutableSet<Int> = mutableSetOf()
){
}