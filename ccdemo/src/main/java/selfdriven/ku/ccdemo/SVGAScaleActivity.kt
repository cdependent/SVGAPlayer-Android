package selfdriven.ku.ccdemo

import android.graphics.BitmapFactory
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.opensource.svgaplayer.*
import kotlinx.android.synthetic.main.activity_scale_test.*

/**
 * Created by zhengxinwei@N3072 on 2017/11/29.
 */
class SVGAScaleActivity : AppCompatActivity() {

    val entries = listOf<String>("CENTER", "CENTER_CROP", "CENTER_INSIDE"
        , "FIT_CENTER", "FIT_END", "FIT_START", "FIT_XY")
    lateinit var svgaParser: SVGAParser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scale_test)
        initSpinner()
        initListener()
        animateSVGAImg.adjustViewBounds = true
        svgaParser = SVGAParser(this)
    }

    private fun initListener() {
        clickStartBtn.setOnClickListener {
            val scaleTypePos = scaleTypeSpinner.selectedItemPosition
            var scaleType = ImageView.ScaleType.CENTER
            when (scaleTypePos) {
                0 -> scaleType = ImageView.ScaleType.CENTER
                1 -> scaleType = ImageView.ScaleType.CENTER_CROP
                2 -> scaleType = ImageView.ScaleType.CENTER_INSIDE
                3 -> scaleType = ImageView.ScaleType.FIT_CENTER
                4 -> scaleType = ImageView.ScaleType.FIT_END
                5 -> scaleType = ImageView.ScaleType.FIT_START
                6 -> scaleType = ImageView.ScaleType.FIT_XY
                else -> ImageView.ScaleType.CENTER
            }
            animateSVGAImg.scaleType = scaleType
            loadAnimate()
        }
    }

    private fun loadAnimate() {
        svgaParser.parse("svga_effect_in_change_3.svga", object: SVGAParser.ParseCompletion {
            override fun onComplete(videoItem: SVGAVideoEntity) {
                animateSVGAImg.setImageDrawable(createDrawable(videoItem))
                animateSVGAImg.loops = 1
                animateSVGAImg.clearsAfterStop = false
                animateSVGAImg.fillMode = SVGAImageView.FillMode.Forward
                animateSVGAImg.cropAlignRight = true
                animateSVGAImg.startAnimation()
            }

            override fun onError(e: Exception) {
                Logger.e("load error", e)
            }

        })
    }

    private fun createDrawable(videoItem: SVGAVideoEntity) = let {
        val dynamicItem = SVGADynamicEntity()
        dynamicItem.dynamicImage["Bitmap1"] = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)
        SVGADrawable(videoItem, dynamicItem)
    }

    private fun initSpinner() {
        val adapter = ArrayAdapter<CharSequence>(
                this, android.R.layout.simple_spinner_item, entries)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        scaleTypeSpinner.adapter = adapter
    }
}