package selfdriven.ku.ccdemo

import android.util.Log

/**
 * Created by zhengxinwei@N3072 on 2017/11/18.
 */

const val DEFAULT_TAG = "CC_DEMO"

class Logger {
    companion object {
        fun d(msg: String, tag:String = DEFAULT_TAG) {
            Log.d(tag, msg)
        }
    }
}