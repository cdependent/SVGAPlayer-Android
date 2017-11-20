package selfdriven.ku.ccdemo

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.opensource.svgaplayer.SVGADrawable
import com.opensource.svgaplayer.SVGAParser
import com.opensource.svgaplayer.SVGAVideoEntity
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
        CCSVGAHelper.getParser(this).safeParseUrl(REMOTE_URL, object: SVGAParser.ParseCompletion {
            override fun onComplete(videoItem: SVGAVideoEntity) {
                val drawable = SVGADrawable(videoItem)
                downloadedSVGAImage.setImageDrawable(drawable)
                downloadedSVGAImage.loops = 1
                downloadedSVGAImage.startAnimation()
            }

            override fun onError(e: Exception?) {
                Logger.e("load svga error", e)
            }

        })
    }
}
