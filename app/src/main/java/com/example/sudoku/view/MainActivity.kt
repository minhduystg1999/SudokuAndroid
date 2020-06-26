package com.example.sudoku.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sudoku.R
import com.example.sudoku.game.Cell
import com.example.sudoku.view.custom.BoardView
import com.example.sudoku.viewmodel.SudokuViewModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), BoardView.OnTouchListener {

    private lateinit var viewModel: SudokuViewModel     //View model

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardview.registerListener(this)

        viewModel = ViewModelProviders.of(this).get(SudokuViewModel::class.java)
        viewModel.sudokuGame.selectCellLiveData.observe(this, Observer { updateSelectCellUI(it) })
        viewModel.sudokuGame.cellsLivcData.observe(this, Observer { updateCells(it) })

        val buttons = listOf(firstbutton, secondbutton, thirdbutton, fourthbutton, fifthbutton, sixthbutton, seventhbutton, eighthbutton, ninthbutton)

        buttons.forEachIndexed { index, button -> button.setOnClickListener { viewModel.sudokuGame.handleInput(index + 1 ) } }
    }

    private fun updateSelectCellUI(cell: Pair<Int, Int>?) = cell?.let{
        boardview.updateSelectCellUI(cell.first, cell.second)
    }

    private fun updateCells(cells: List<Cell>?) = cells?.let {
        boardview.updateCells(cells)
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectCell(row, col)
    }
}
