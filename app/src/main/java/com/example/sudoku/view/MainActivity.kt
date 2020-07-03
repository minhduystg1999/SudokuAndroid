package com.example.sudoku.view

import android.app.AlertDialog
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
import kotlin.system.exitProcess

@Suppress("DEPRECATION")
class MainActivity : AppCompatActivity(), BoardView.OnTouchListener {

    private lateinit var viewModel: SudokuViewModel     //View model

    private lateinit var numberButtons: List<Button>    //Number buttons

    //Function on create app
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        boardView.registerListener(this)

        viewModel = ViewModelProviders.of(this).get(SudokuViewModel::class.java)
        viewModel.sudokuGame.selectCellLiveData.observe(this, Observer { updateSelectCellUI(it) })
        viewModel.sudokuGame.cellsLiveData.observe(this, Observer { updateCells(it) })
        viewModel.sudokuGame.isNotesLiveData.observe(this, Observer { updateNotesUI(it) })
        viewModel.sudokuGame.highlightKeysLiveData.observe(this, Observer { updateHighlightKeys(it) })
        viewModel.sudokuGame.isFinishedLiveData.observe(this, Observer { updateFinishGame(it) })

        numberButtons = listOf(firstButton, secondButton, thirdButton, fourthButton, fifthButton, sixthButton, seventhButton, eighthButton, ninthButton)
        numberButtons.forEachIndexed { index, button -> button.setOnClickListener { viewModel.sudokuGame.handleInput(index + 1 ) } }

        exitButton.setOnClickListener { showExitDialog(viewModel) }

        newButton.setOnClickListener { viewModel.sudokuGame.newGame() }

        notesButton.setOnClickListener { viewModel.sudokuGame.notesStatusChange() }

        resetButton.setOnClickListener { viewModel.sudokuGame.reset() }

        deleteButton.setOnClickListener { viewModel.sudokuGame.delete() }
    }

    //Function update selected cell
    private fun updateSelectCellUI(cell: Pair<Int, Int>?) = cell?.let{
        boardView.updateSelectCellUI(cell.first, cell.second)
    }

    //Function update cells
    private fun updateCells(cells: List<Cell>?) = cells?.let {
        boardView.updateCells(cells)
    }

    //Function update trang thai note
    private fun updateNotesUI(isNotes: Boolean?) = isNotes?.let {
        val color = if (it) ContextCompat.getColor(this, R.color.colorPrimary)
                    else Color.LTGRAY
        //notesButton.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
    }

    //Function update cac number buttons duoc highlight khi trong trang thai note
    private fun updateHighlightKeys(set: Set<Int>?) = set?.let {
        numberButtons.forEachIndexed { index, button ->
            val color = if (set.contains(index + 1)) ContextCompat.getColor(this, R.color.colorPrimary)
                        else Color.LTGRAY
            button.background.setColorFilter(color, PorterDuff.Mode.MULTIPLY)
        }
    }

    //Function update da finish game hay chua
    private fun updateFinishGame(isFinished: Boolean?) = isFinished?.let {
        if (it)
            showCongratsDialog(viewModel)
    }

    //Function update selected cell khi touch vao cell
    override fun onCellTouched(row: Int, col: Int) {
        viewModel.sudokuGame.updateSelectCell(row, col)
    }

    //Function show dialog khi finish game
    private fun showCongratsDialog(viewModel: SudokuViewModel) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Congratulations!")
        dialog.setMessage("You have finished the sudoku. Cheers for the hard work!")
        dialog.setPositiveButton("New game") {
            dialog, which -> viewModel.sudokuGame.newGame()
        }
        dialog.setNegativeButton("Exit") {
            dialog, which ->
            run {
                finish()
                exitProcess(0)
            }
        }
        dialog.show()
    }

    private fun showExitDialog(viewModel: SudokuViewModel) {
        val dialog = AlertDialog.Builder(this)
        dialog.setTitle("Exit!")
        dialog.setMessage("Exit the game?")
        dialog.setPositiveButton("No") {
                dialog, which -> dialog.dismiss()
        }
        dialog.setNegativeButton("Yes") {
                dialog, which ->
            run {
                finish()
                exitProcess(0)
            }
        }
        dialog.show()
    }
}
