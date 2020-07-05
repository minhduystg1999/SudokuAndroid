package com.example.sudoku.view

import android.app.AlertDialog
import android.content.res.Resources
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
        viewModel.sudokuGame.highlightKeysLiveData.observe(this, Observer { updateHighlightKeys(it) })
        viewModel.sudokuGame.isFinishedLiveData.observe(this, Observer { updateFinishGame(it) })

        numberButtons = listOf(firstButton, secondButton, thirdButton, fourthButton, fifthButton, sixthButton, seventhButton, eighthButton, ninthButton)
        numberButtons.forEachIndexed { index, button -> button.setOnClickListener { viewModel.sudokuGame.handleInput(index + 1 ) } }

        exitButton.setOnClickListener { showExitDialog(viewModel) }

        newButton.setOnClickListener { showNewGameDialog(viewModel) }

        hintButton.setOnClickListener { viewModel.sudokuGame.hint() }

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

    //Function update cac number buttons duoc highlight
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
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
        dialog.setIcon(R.drawable.finish_icon)
        dialog.setTitle("Congratulations!")
        dialog.setMessage("You have finished the sudoku. Cheers for the hard work!")
        dialog.setPositiveButton("New game") {
            dialog, which ->
            run {
                dialog.cancel()
                showNewGameDialog(viewModel)
            }
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
        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_DARK)
        dialog.setIcon(R.drawable.exit_game_icon)
        dialog.setTitle("Exit!")
        dialog.setMessage("Exit the game?")
        dialog.setNegativeButton("No") {
                dialog, which -> dialog.dismiss()
        }
        dialog.setPositiveButton("Yes") {
                dialog, which ->
            run {
                finish()
                exitProcess(0)
            }
        }
        dialog.show()
    }

    private fun showNewGameDialog(viewModel: SudokuViewModel) {
        val diffNumber: Array<Int> = arrayOf(25, 35, 45)
        val diffName: Array<String> = arrayOf("Easy", "Normal", "Hard")
        var diffChoice: Int = 0

        val dialog = AlertDialog.Builder(this, AlertDialog.THEME_HOLO_LIGHT)
        dialog.setIcon(R.drawable.new_game_icon)
        dialog.setTitle("Choose your game difficulty:")
        dialog.setSingleChoiceItems(diffName, 0) {
            dialog, i -> diffChoice = diffNumber[i]
        }
        dialog.setPositiveButton("OK") {
            dialog, which -> viewModel.sudokuGame.newGame(diffChoice)
        }
        dialog.setNegativeButton("Cancel") {
            dialog, which ->  dialog.cancel()
        }

        dialog.show()
    }
}
