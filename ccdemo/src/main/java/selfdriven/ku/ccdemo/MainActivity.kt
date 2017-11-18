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
        val parser = SVGAParser(this)
        parser.safeParseUrl(REMOTE_URL, object: SVGAParser.ParseCompletion {
            override fun onComplete(videoItem: SVGAVideoEntity) {
                Logger.d("load finish ${videoItem.toString()}")
                val drawable = SVGADrawable(videoItem)
                downloadedSVGAImage.setImageDrawable(drawable)
                downloadedSVGAImage.startAnimation()
            }

            override fun onError(e: Exception?) {
                Logger.e("message", e)
            }

        })
    }
}
