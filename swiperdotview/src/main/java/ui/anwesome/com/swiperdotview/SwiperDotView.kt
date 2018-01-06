package ui.anwesome.com.swiperdotview

/**
 * Created by anweshmishra on 06/01/18.
 */
import android.view.*
import android.content.*
import android.graphics.*
class SwiperDotView(ctx:Context):View(ctx) {
    val paint = Paint(Paint.ANTI_ALIAS_FLAG)
    override fun onDraw(canvas:Canvas) {

    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {

            }
        }
        return true
    }
    data class DotMover(var x:Float,var y:Float,var size:Float = 0f,var w:Float = size) {
        var updateFns:Array<(Float)->Unit> = arrayOf({scale -> w = size+size*scale},{scale ->
            size = 2*size-size*scale
            x = x+size*scale
        })
        fun draw(canvas:Canvas,paint:Paint) {
            paint.color = Color.parseColor("#E65100")
            canvas.drawRoundRect(RectF(x-size/2,y-size/2,x+size/2,y+size/2),size/2,size/2,paint)
        }
        fun update(stopcb:()->Unit) {

        }
        fun startUpdating(statcb:()->Unit) {

        }
    }
}