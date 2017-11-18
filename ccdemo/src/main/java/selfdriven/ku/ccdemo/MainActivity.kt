package selfdriven.ku.ccdemo

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.netease.cc.common.okhttp.OkHttpUtils
import com.netease.cc.common.okhttp.callbacks.OkStringCallBack
import java.lang.Exception

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        OkHttpUtils.get().url("http://www.baidu.com").build().execute(object : OkStringCallBack() {
            override fun onError(e: Exception?, errorCode: Int) {
                Logger.d("error:" + e);
            }

            override fun onResponse(response: String?, statusCode: Int) {
                Logger.d("content: $response")
            }

        })
    }
}
