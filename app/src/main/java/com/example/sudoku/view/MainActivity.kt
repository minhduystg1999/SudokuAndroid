package com.example.sudoku.view

import android.graphics.Color
import android.graphics.PorterDuff
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.sudoku.R
import com.example.sudoku.game.Cell
import com.example.sudoku.view.custom.BoardView
import com.example.sudoku.viewmodel.SudokuViewModel
import kotlinx.android.synthetic.main.activity_main.*

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), BoardView.OnTouchListener {

    private lateinit var viewModel: SudokuViewModel     //View model

    private lateinit var numberButtons: List<Button>    //Number buttons

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardview.registerListener(this)

        viewModel = ViewModelProviders.of(this).get(SudokuViewModel::class.java)
        viewModel.sudokuGame.selectCellLiveData.observe(this, Observer { updateSelectCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer { updateCells(it) })
        viewModel.sudokuGame.isNotesLiveData.observe(this, Observer { updateNotesUI(it) })
        viewModel.sudokuGame.highlightKeysLiveData.observe(this, Observer { updateHighlightKeys(it) })

        numberButtons = listOf(firstbutton, secondbutton, thirdbutton, fourthbutton, fifthbutton, sixthbutton, seventhbutton, eighthbutton, ninthbutton)

        numberButtons.forEachIndexed { index, button -> button.setOnClickListener { viewModel.sudokuGame.handleInput(index + 1 ) } }

        notesButton.setOnClickListener { viewModel.sudokuGame.notesStatusChange() }

        deleteButton.setOnClickListener { viewModel.sudokuGame.delete() }
    }

    private fun updateSelectCellUI(cell: Pair<Int, Int>?) = cell?.let{
        boardview.updateSelectCellUI(cell.first, cell.second)
    }

    private fun updateCells(cells: List<Cell>?) = cells?.let {
        boardview.updateCells(cells)
    }

    private fun updateNotesUI(isNotes: Boolean?) = isNotes?.let {
        val color = if (it) ContextCompat.getColor(this, R.color.colorPrimary)
                    else Color.LTGRAY
        notesButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    private fun updateHighlightKeys(set: Set<Int>?) = set?.let {
        numberButtons.forEachIndexed { index, button ->
            val color = if (set.contains(index + 1)) ContextCompat.getColor(this, R.color.colorPrimary)
                        else Color.LTGRAY
            button.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
    }

    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectCell(row, col)
    }
}
