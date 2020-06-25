package com.example.sudoku.view.custom

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View

class BoardView(context: Context, attributeSet: AttributeSet) : View(context, attributeSet) {

    private var sqrtSize = 3                // = number of row or column
    private var size = 9                    // = number of all cells = sqrtSize^2
    private var cellSizePixels = 0F         // a cell's size pixels

    //For selected cell
    private var selectRow = -1              //selected row
    private var selectColumn = -1           //selected column

    private var listener: BoardView.OnTouchListener? = null

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

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val sizePixels = Math.min(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(sizePixels, sizePixels)
    }

    override fun onDraw(canvas: Canvas) {
        cellSizePixels = (width / size).toFloat()
        fillCells(canvas)
        drawLine(canvas)
    }

    //Fill cac cell duoc select hoac conflict
    private fun fillCells(canvas: Canvas) {
        if (selectRow == -1 || selectColumn == -1) return  //Neu chua chon cell thi khong can fill
        for (row in 0..size)
            for (col in 0..size) {
                if (row == selectRow && col == selectColumn)
                    fillCell(canvas, row, col, selectCell)
                else if (row == selectRow || col == selectColumn)
                    fillCell(canvas, row, col, conflictCell)
                else if (row / sqrtSize == selectRow / sqrtSize && col / sqrtSize == selectColumn / sqrtSize)
                    fillCell(canvas, row, col, conflictCell)
            }
    }

    //Fill cell
    private fun fillCell(canvas: Canvas, row: Int, col: Int, paint: Paint) {
        canvas.drawRect(col*cellSizePixels, row*cellSizePixels, (col + 1)*cellSizePixels, (row + 1)*cellSizePixels, paint )
    }

    //Ve line chia cach cac o va line vien ngoai
    private fun drawLine(canvas: Canvas) {
        canvas.drawRect(0F, 0F, width.toFloat(), height.toFloat(), thickLine)

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

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                handleTouchEvent(event.x, event.y)
                true
            }
            else -> false
        }
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