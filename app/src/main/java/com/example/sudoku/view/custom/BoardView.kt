package com.example.sudoku.view.custom

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import com.example.sudoku.game.Cell

class BoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var sqrtSize = 3                // = number of row or column
    private var size = 9                    // = number of all cells = sqrtSize^2

    //Size pixels (0F vi den function onDraw va on Measure se duoc gan gia tri da duoc measured)
    private var cellSizePixels = 0F         // a cell's size pixels
    private var notesSizePixels = 0F        // notes' size pixels

    //For selected cell
    private var selectRow = -1              //selected row
    private var selectColumn = -1           //selected column

    private var cells: List<Cell>? = null   //List cell

    private var listener: BoardView.OnTouchListener? = null     //listener of touch event

    //Thick line for drawing lines
    private val thickLine = Paint().apply{
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 6F
    }

    //Thin line for drawing lines
    private val thinLine = Paint().apply {
        style = Paint.Style.STROKE
        color = Color.BLACK
        strokeWidth = 2F
    }

    //Cell paint for fill cells
    private val selectCell = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#6ead3a")
    }

    //Cell paint for conflict cells
    private val conflictCell = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#efedef")
    }

    //Value text paint
    private val textPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        textSize = 50F
    }

    private val startingCellPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.parseColor("#acacac")
    }

    //Value text paint for the starting cells
    private val startingCellTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
        textSize = 50F
        typeface = Typeface.DEFAULT_BOLD
    }

    //Notes text paint
    private val notesTextPaint = Paint().apply {
        style = Paint.Style.FILL_AND_STROKE
        color = Color.BLACK
    }


    //Override function onMeasure de nhan gia tri kich thuoc cell
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = Math.min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)
    }

    //Update cac gia tri measure trong onMeasure
    private fun updateMeasurement(width: Int) {
        cellSizePixels = (width / size).toFloat()
        notesSizePixels = cellSizePixels / sqrtSize.toFloat()
        notesTextPaint.textSize = notesSizePixels
        textPaint.textSize = cellSizePixels / 1.5F
        startingCellTextPaint.textSize = cellSizePixels / 1.5F
    }

    //Override function onDraw de thuc hien fill cell, draw line, write value
    override fun onDraw(canvas: Canvas) {
        updateMeasurement(width)
        fillCells(canvas)
        drawLine(canvas)
        writeValue(canvas)
    }

    //Fill tat ca cac cells
    private fun fillCells(canvas: Canvas) {
        cells?.forEach {
            val row = it.row
            val col = it.col

            if (it.isStartingCell)
                fillCell(canvas, row, col, startingCellPaint)
            else if (row == selectRow && col == selectColumn)
                fillCell(canvas, row, col, selectCell)
            else if (row == selectRow || col == selectColumn)
                fillCell(canvas, row, col, conflictCell)
            else if (row / sqrtSize == selectRow / sqrtSize && col / sqrtSize == selectColumn / sqrtSize)
                fillCell(canvas, row, col, conflictCell)
        }
    }

    //Fill 1 cell
    private fun fillCell(canvas: Canvas, row: Int, col: Int, paint: Paint) {
        canvas.drawRect(col*cellSizePixels, row*cellSizePixels, (col + 1)*cellSizePixels, (row + 1)*cellSizePixels, paint )
    }

    //Ve line chia cach cac o va line vien ngoai
    private fun drawLine(canvas: Canvas) {
        //Ve vien ngoai cua toan bo canvas
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), thickLine)

        //Ve cac line ngang doc cho tung don vi
        for( i in 1 until size )
        {
            val lineUsing = when (i % sqrtSize) {
                0 -> thickLine          //neu la dong ngoai thi dung thickLine
                else -> thinLine        //con lai dung thinLine
            }

            canvas.drawLine(i * cellSizePixels, 0F, i * cellSizePixels, height.toFloat(), lineUsing)
            canvas.drawLine(0F, i * cellSizePixels, width.toFloat() , i* cellSizePixels, lineUsing)
        }
    }

    //Ghi value vao cell
    private fun writeValue(canvas: Canvas) {
        cells?.forEach { cell ->
            //Chuyen value sang string va tao bounds cho text cell
            val value = cell.value
            val textBounds = Rect()

            if (value == 0) {
                //Write notes
                cell.notes.forEach {note ->
                    val rowInCell = (note - 1) / sqrtSize
                    val colInCell = (note - 1) % sqrtSize
                    val valueString = note.toString()
                    notesTextPaint.getTextBounds(valueString, 0, valueString.length, textBounds)
                    val textWidth = notesTextPaint.measureText(valueString)
                    val textHeight = textBounds.height()

                    canvas.drawText(
                        valueString,
                        (cell.col * cellSizePixels) + (colInCell * notesSizePixels) + notesSizePixels / 2 - textWidth / 2,
                        (cell.row * cellSizePixels) + (rowInCell * notesSizePixels) + notesSizePixels / 2 - textHeight / 2,
                        notesTextPaint
                    )
                }
            } else {
                val valueString = cell.value.toString()
                val writeValuePaint = if (cell.isStartingCell) startingCellTextPaint else textPaint
                writeValuePaint.getTextBounds(valueString, 0, valueString.length, textBounds)
                val textWidth = writeValuePaint.measureText(valueString)
                val textHeight = textBounds.height()

                canvas.drawText(
                    valueString,
                    (cell.col * cellSizePixels) + cellSizePixels / 2 - textWidth / 2,
                    (cell.row * cellSizePixels) + cellSizePixels / 2 - textHeight / 2,
                    writeValuePaint
                )
            }
        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                true
            }
            else -> false
        }
    }

    fun updateCells(cells: List<Cell>) {
        this.cells = cells
        invalidate()
    }

    private fun handleTouchEvent(x: Float, y: Float) {
        val posSelectColumn = (x / cellSizePixels).toInt()
        val posSelectRow = (y / cellSizePixels).toInt()
        listener?.onCellTouched(posSelectRow, posSelectColumn)
    }

    fun updateSelectCellUI(row: Int, col: Int) {
        selectRow = row
        selectColumn = col
        invalidate()
    }

    fun registerListener(listener: BoardView.OnTouchListener) {
        this.listener = listener
    }

    interface OnTouchListener{
        fun onCellTouched(row: Int, col: Int)
    }
}