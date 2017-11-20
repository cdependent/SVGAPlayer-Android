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

object SVGAHelper {
//    fun getCCSVGAParser(context: Context) = SVGAParser(context).also {
//        it.fileDownloader = object : SVGAParser.FileDownloader() {
//            override fun resume(url: URL, complete: (inputStream: InputStream) -> Unit, failure: (e: Exception) -> Unit) {
//                OkHttpUtils.get().url(url.toString()).build().execute(object: OkCallBack<Unit>() {
//                    override fun parseNetworkResponse(response: Response?, id: Int) {
//                        response?.let {
//                            complete(it.body().byteStream())
//                        }
//                    }
//
//                    override fun onError(e: java.lang.Exception?, errorCode: Int) {
//                        failure(e)
//                    }
//
//                    override fun onResponse(response: Unit?, statusCode: Int) {
//                    }
//
//                })
//            }
//        }
//    }
}