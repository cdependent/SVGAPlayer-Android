package selfdriven.ku.ccdemo

import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.TextPaint
import com.opensource.svgaplayer.*
import kotlinx.android.synthetic.main.activity_main.*

const val REMOTE_URL = "https://github.com/yyued/SVGA-Samples/blob/master/angel.svga?raw=true"

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        clickLoad.setOnClickListener { loadSVGAResource() }
//        SVGAHelper.getCCSVGAParser(this)
//                .safeParseUrl(REMOTE_URL, object: SVGAParser.ParseCompletion {
//            override fun onComplete(videoItem: SVGAVideoEntity) {
//                Logger.d("load finish ${videoItem.toString()}")
//                val drawable = SVGADrawable(videoItem)
//                downloadedSVGAImage.setImageDrawable(drawable)
//                downloadedSVGAImage.startAnimation()
//            }
//
//            override fun onError(e: Exception?) {
//                Logger.e("message", e)
//            }
//
//        })
    }

    private fun loadSVGAResource() {
        Logger.d("start load")
//        val url = "http://c.cotton.netease.com/buckets/4NhQWd/files/QbyhJxp"

//        val url = "https://github.com/yyued/SVGA-Samples/blob/master/angel.svga?raw=true"
        val url = "http://c.cotton.netease.com/buckets/4NhQWd/files/QcAfUcK"

        var start = System.currentTimeMillis();
        CCSVGAHelper.getParser(this).safeParseUrl(url, object : SVGAParser.ParseCompletion {
            //        CCSVGAHelper.getParser(this).parse("chiji2.svga", object: SVGAParser.ParseCompletion {
            override fun onComplete(videoItem: SVGAVideoEntity) {
                var current = System.currentTimeMillis();
                Logger.d("load time: ${current - start}ms")
                start = current;
                val svgaDynamic = SVGADynamicEntity()
                val drawable = SVGADrawable(videoItem, svgaDynamic)
                updateContent(svgaDynamic)
                downloadedSVGAImage.setImageDrawable(drawable)
                downloadedSVGAImage.startAnimation()
                current = System.currentTimeMillis();
                Logger.d("init and play time: ${current - start}ms")
            }

            override fun onError(e: Exception) {
                Logger.e("load svga error", e)
            }

        })
    }

    private fun updateContent(svgaDynamic: SVGADynamicEntity) {
        val paint = TextPaint()
        paint.textSize = resources.displayMetrics.density * 30
        paint.isFakeBoldText = true
        paint.color = Color.BLACK
        svgaDynamic.setDynamicText("O_O", paint, "Bitmap1")
        svgaDynamic.setDynamicImage(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher), "3c22e52f296007a044e5974d0d3d2e7a")
        svgaDynamic.setDynamicImage(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher), "7e84ebd73f62b19d06db5635cb89ffb7")
        svgaDynamic.setDynamicImage(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher), "9f61e6898c76d8ad8d4614cf2d1912a4")
        svgaDynamic.setDynamicImage(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher), "83dab6067b5c0d74cdd9c83a0bc202cb")
        svgaDynamic.setDynamicImage(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher), "c270a501a4677e172c4dcc5229d3c01b")
        svgaDynamic.setDynamicImage(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher), "d44eb9af51b75ede9714a2004c0a83c5")
        svgaDynamic.setDynamicImage(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher), "d46a950b51997625e6a634b44e1aaeed")
        svgaDynamic.setDynamicImage(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher), "d559e7302cf62879ace0c74e811c03d4")
        svgaDynamic.setDynamicImage(BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher), "f96fca7e6749a13bb202b47cca90d2c3")
    }

    abstract class SimpleSVGACallback : SVGACallback {
        override fun onPause() {

        }

        override fun onFinished() {
        }

        override fun onRepeat() {
        }

        override fun onStep(frame: Int, percentage: Double) {
        }

    }
}
