package ui.anwesome.com.swiperdotview

/**
 * Created by anweshmishra on 06/01/18.
 */
import android.app.Activity
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
        val state = SwiperDotState()
        var updateFns:Array<(Float)->Unit> = arrayOf({scale -> w = size+size*scale},{scale ->
            size = 2*size-size*scale
            x = x+size*scale
        })
        init {
            updateFns.forEach {
                state.addUpdateFn(it)
            }
        }
        fun draw(canvas:Canvas,paint:Paint) {
            paint.color = Color.parseColor("#E65100")
            canvas.drawRoundRect(RectF(x-w/2,y-size/2,x+w/2,y+size/2),w/2,size/2,paint)
        }
        fun update(stopcb:()->Unit) {
            state.update(stopcb)
        }
        fun startUpdating(startcb:()->Unit) {
            state.startUpdating(startcb)
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
    data class SwiperDotRenderer(var view:SwiperDotView,var time:Int = 0) {
        var dotMover:DotMover?=null
        val animator = Animator(view)
        fun render(canvas:Canvas,paint:Paint) {
            if(time == 0) {
                val w = canvas.width.toFloat()
                val h = canvas.height.toFloat()
                dotMover = DotMover(w/2-w/10,h/2,w/10)
            }
            canvas.drawColor(Color.parseColor("#212121"))
            dotMover?.draw(canvas,paint)
            time++
        }
        fun startUpdating() {
            dotMover?.startUpdating {
                animator.startAnimation {
                    dotMover?.update{
                        animator.stop()
                    }
                }
            }
        }
    }
    data class Animator(var view:SwiperDotView,var animated:Boolean = false) {
        fun animate(updatecb:()->Unit) {
            if(animated) {
                updatecb()
                try {
                    Thread.sleep(50)
                    view.invalidate()
                }
                catch(ex:Exception) {

                }
            }
        }
        fun startAnimation(startcb:()->Unit) {
            if(!animated) {
                animated = true
                view.postInvalidate()
            }
        }
        fun stop() {
            if(animated) {
                animated = false
            }
        }
    }
    companion object {
        fun create(activity:Activity):SwiperDotView {
            val view = SwiperDotView(activity)
            activity.setContentView(view)
            return view
        }
    }
}