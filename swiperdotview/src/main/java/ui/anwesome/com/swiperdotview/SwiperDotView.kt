package ui.anwesome.com.swiperdotview

/**
 * Created by anweshmishra on 06/01/18.
 */
import android.view.*
import android.content.*
import android.graphics.*
import java.util.concurrent.ConcurrentLinkedQueue

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
    data class SwiperDotState(var scale:Float = 0f,var dir:Float = 0f,var prevScale:Float = 0f,var j:Int = 0,var prevDir:Int = 0) {
        val updateFns:ConcurrentLinkedQueue<(Float)->Unit> = ConcurrentLinkedQueue()
        fun addUpdateFn(updateFn:(Float)->Unit) {
            updateFns.add(updateFn)
        }
        fun update(stopcb:()->Unit) {
            scale += 0.1f*dir
            if(Math.abs(scale - prevScale) > 1) {
                scale = prevScale
                j+=prevDir
                if(j == updateFns.size || j == -1) {
                    scale = prevScale + dir
                    dir = 0f
                    prevDir *=-1
                    j += prevDir
                    prevScale = scale
                }
            }
        }
        fun startUpdating(startcb:()->Unit) {
            if(dir == 0f) {
                dir = prevDir.toFloat()
            }
        }
    }
}