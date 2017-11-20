package selfdriven.ku.ccdemo

import android.util.Log

/**
 * Created by zhengxinwei@N3072 on 2017/11/18.
 */

const val DEFAULT_TAG = "CC_DEMO"

object Logger {
    @JvmOverloads
    fun d(msg: String, tag: String = DEFAULT_TAG) {
        Log.d(tag, msg)
    }

    @JvmOverloads
    fun e(msg: String, e: Exception?, tag: String = DEFAULT_TAG) {
        Log.e(tag, msg, e);
    }
}