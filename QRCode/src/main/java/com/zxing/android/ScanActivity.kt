package com.zxing.android

import android.app.Activity
import android.content.Intent
import android.widget.Toast
import com.zxing.android.utils.UriHelp
import kotlin.concurrent.thread

class ScanActivity : CaptureActivity() {
    override fun getQRResult(result: String?) {
        setResult(Activity.RESULT_OK, Intent().putExtra("URL", result))
        finish()
    }

    override fun onPicSelectClick() {
        startActivityForResult(Intent(Intent.ACTION_GET_CONTENT)
                .setType("image/*").addCategory(Intent.CATEGORY_OPENABLE), 66)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == 66) {
            val path = UriHelp.getPathFromIntent(this, data)
            if (path != null) {
//                Log("选取图片：$path")
                thread {
                    val result = scanningImage(path)
                    runOnUiThread {
                        if (result != null) {
                            setResult(Activity.RESULT_OK, Intent().putExtra("URL", result))
                            finish()
                        } else {
                            Toast.makeText(this, "未扫描到二维码！", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            } else {
            }
        }
    }
}