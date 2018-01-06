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
    val renderer = SwiperDotRenderer(this)
    override fun onDraw(canvas:Canvas) {
        renderer.render(canvas,paint)
    }
    override fun onTouchEvent(event:MotionEvent):Boolean {
        when(event.action) {
            MotionEvent.ACTION_DOWN -> {
                renderer.startUpdating()
            }
        }
        return true
    }
    data class DotMover(var x:Float,var y:Float,var size:Float,var w:Float = 0f,var prevX:Float = x) {
        val state = SwiperDotState()
        var updateFns:Array<(Float)->Unit> = arrayOf({scale -> w = 2*size*scale},{scale ->
            w = 2*size*(1-scale)
            x = prevX+2*size*scale
        })
        init {
            updateFns.forEach {
                state.addUpdateFn(it)
            }
        }
        fun draw(canvas:Canvas,paint:Paint) {
            paint.color = Color.parseColor("#E65100")
            canvas.save()
            canvas.translate(x,y)
            paint.style = Paint.Style.FILL_AND_STROKE
            canvas.drawArc(RectF(-size/2,-size/2,size/2,size/2),90f,180f,true,paint)
            canvas.drawRect(RectF(0f,-size/2,w,size/2),paint)
            canvas.drawArc(RectF(w-size/2,-size/2,w+size/2,size/2),270f,180f,true,paint)
            canvas.restore()
        }
        fun update(stopcb:()->Unit) {
            state.update(stopcb)
        }
        fun startUpdating(startcb:()->Unit) {
            state.startUpdating(startcb)
        }
    }
    data class SwiperDotState(var scale:Float = 0f,var dir:Float = 0f,var prevScale:Float = 0f,var j:Int = 0,var prevDir:Int = 1) {
        val updateFns:ConcurrentLinkedQueue<(Float)->Unit> = ConcurrentLinkedQueue()
        fun addUpdateFn(updateFn:(Float)->Unit) {
            updateFns.add(updateFn)
        }
        fun update(stopcb:()->Unit) {
            scale += 0.1f*dir
            updateFns.at(j)?.invoke(scale)
            if(Math.abs(scale - prevScale) > 1) {
                scale = prevScale
                j+=prevDir
                if(j == updateFns.size || j == -1) {
                    scale = prevScale + dir
                    dir = 0f
                    prevDir *=-1
                    j += prevDir
                    prevScale = scale
                    stopcb()
                }
            }
        }
        fun startUpdating(startcb:()->Unit) {
            if(dir == 0f) {
                dir = prevDir.toFloat()
                startcb()
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
            animator.animate {
                dotMover?.update {
                    animator.stop()
                }
            }
        }
        fun startUpdating() {
            dotMover?.startUpdating {
                animator.startAnimation()
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
        fun startAnimation() {
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
fun ConcurrentLinkedQueue<(Float)->Unit>.at(j:Int):((Float)->Unit)? {
    var i = 0
    forEach {
        if(i == j) {
            return it
        }
        i++
    }
    return null
}