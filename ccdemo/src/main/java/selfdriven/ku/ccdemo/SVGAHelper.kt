package selfdriven.ku.ccdemo

import com.opensource.svgaplayer.SVGAParser
import java.net.URL

/**
 * Created by zhengxinwei@N3072 on 2017/11/18.
 */

fun SVGAParser.safeParseUrl(url : String, callback: SVGAParser.ParseCompletion) {
    val result = try { URL(url) } catch (e: Exception) {
        Logger.e("url error", e)
        null
    }
    result?.let {
        parse(result, callback)
    }
}