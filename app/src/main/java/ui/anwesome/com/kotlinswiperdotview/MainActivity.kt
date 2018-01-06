package ui.anwesome.com.kotlinswiperdotview

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import ui.anwesome.com.swiperdotview.SwiperDotView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SwiperDotView.create(this)
    }
}
