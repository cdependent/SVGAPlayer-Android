package com.opensource.svgaplayer

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.opensource.svgaplayer.proto.MovieEntity
import org.json.JSONObject
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.security.MessageDigest
import java.util.zip.Inflater
import java.util.zip.ZipInputStream

/**
 * Created by PonyCui_Home on 16/6/18.
 */

private var sharedLock: Int = 0

class SVGAParser(private val context: Context) {

    interface ParseCompletion {

        fun onComplete(videoItem: SVGAVideoEntity)
        fun onError(e: Exception? = null)

    }

    val mUiHandler = Handler(Looper.getMainLooper())

    open class FileDownloader {

        open fun resume(url: URL, complete: (inputStream: InputStream) -> Unit, failure: (e: Exception) -> Unit) {
            Thread({
                try {
                    (url.openConnection() as? HttpURLConnection)?.let {
                        it.connectTimeout = 20 * 1000
                        it.requestMethod = "GET"
                        it.connect()
                        val inputStream = it.inputStream
                        val outputStream = ByteArrayOutputStream()
                        val buffer = ByteArray(4096)
                        var count: Int
                        while (true) {
                            count = inputStream.read(buffer, 0, 4096)
                            if (count == -1) {
                                break
                            }
                            outputStream.write(buffer, 0, count)
                        }
                        complete(ByteArrayInputStream(outputStream.toByteArray()))
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    failure(e)
                }
            }).start()
        }

    }

    var fileDownloader = FileDownloader()

    fun parse(assetsName: String, callback: ParseCompletion) {
        try {
            context.assets.open(assetsName)?.let {
                parse(it, cacheKey("file:///assets/" + assetsName), callback)
            }
        } catch (e: Exception) {
            callback.onError(e)
        }
    }

    fun parse(url: URL, callback: ParseCompletion) {
        if (cacheDir(cacheKey(url)).exists()) {
            parseWithCacheKey(cacheKey(url))?.let {
                mUiHandler.post {
                    callback.onComplete(it)
                }
                return
            }
        }
        fileDownloader.resume(url, {
            val videoItem = try { parseData(it, cacheKey(url)) } catch (e: Exception) {
                mUiHandler.post{ callback.onError(e) }
                null
            } ?: return@resume
            mUiHandler.post {
                callback.onComplete(videoItem)
            }
        }, {
            mUiHandler.post {
                callback.onError(it)
            }
        })
    }

    fun parse(inputStream: InputStream, cacheKey: String, callback: ParseCompletion) {
        Thread({
            val videoItem = try { parseData(inputStream, cacheKey) } catch (e: Exception) {
                mUiHandler.post{ callback.onError(e) }
                null
            } ?: return@Thread
            mUiHandler.post {
                callback.onComplete(videoItem)
            }
        }).start()
    }

    fun parseData(inputStream: InputStream, cacheKey: String): SVGAVideoEntity? {
        val bytes = readAsBytes(inputStream)
        if (bytes.size > 4 && bytes[0].toInt() == 80 && bytes[1].toInt() == 75 && bytes[2].toInt() == 3 && bytes[3].toInt() == 4) {
            synchronized(sharedLock, {
                if (!cacheDir(cacheKey).exists()) {
                    unzip(ByteArrayInputStream(bytes), cacheKey)
                }
            })
            val cacheDir = File(context.cacheDir.absolutePath + "/" + cacheKey + "/")
            File(cacheDir, "movie.binary")?.takeIf { it.isFile }?.let { binaryFile ->
                try {
                    FileInputStream(binaryFile)?.let {
                        val videoItem = SVGAVideoEntity(MovieEntity.ADAPTER.decode(it), cacheDir)
                        it.close()
                        return videoItem
                    }
                } catch (e: Exception) {
                    cacheDir.delete()
                    binaryFile.delete()
                    throw e
                }
            }
            File(cacheDir, "movie.spec")?.takeIf { it.isFile }?.let { jsonFile ->
                try {
                    FileInputStream(jsonFile)?.let { fileInputStream ->
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        val buffer = ByteArray(2048)
                        while (true) {
                            val size = fileInputStream.read(buffer, 0, buffer.size)
                            if (size == -1) {
                                break
                            }
                            byteArrayOutputStream.write(buffer, 0, size)
                        }
                        byteArrayOutputStream.toString()?.let {
                            JSONObject(it)?.let {
                                fileInputStream.close()
                                return SVGAVideoEntity(it, cacheDir)
                            }
                        }
                    }
                } catch (e: Exception) {
                    cacheDir.delete()
                    jsonFile.delete()
                    throw e
                }
            }
        } else {
            inflate(bytes)?.let {
                return SVGAVideoEntity(MovieEntity.ADAPTER.decode(it), File(cacheKey))
            }
        }
        return null
    }

    private fun parseWithCacheKey(cacheKey: String): SVGAVideoEntity? {
        try {
            val cacheDir = File(context.cacheDir.absolutePath + "/" + cacheKey + "/")
            File(cacheDir, "movie.binary")?.takeIf { it.isFile }?.let { binaryFile ->
                try {
                    FileInputStream(binaryFile)?.let {
                        val videoItem = SVGAVideoEntity(MovieEntity.ADAPTER.decode(it), cacheDir)
                        it.close()
                        return videoItem
                    }
                } catch (e: Exception) {
                    cacheDir.delete()
                    binaryFile.delete()
                    throw e
                }
            }
            File(cacheDir, "movie.spec")?.takeIf { it.isFile }?.let { jsonFile ->
                try {
                    FileInputStream(jsonFile)?.let { fileInputStream ->
                        val byteArrayOutputStream = ByteArrayOutputStream()
                        val buffer = ByteArray(2048)
                        while (true) {
                            val size = fileInputStream.read(buffer, 0, buffer.size)
                            if (size == -1) {
                                break
                            }
                            byteArrayOutputStream.write(buffer, 0, size)
                        }
                        byteArrayOutputStream.toString()?.let {
                            JSONObject(it)?.let {
                                fileInputStream.close()
                                return SVGAVideoEntity(it, cacheDir)
                            }
                        }
                    }
                } catch (e: Exception) {
                    cacheDir.delete()
                    jsonFile.delete()
                    throw e
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    private fun cacheKey(str: String): String {
        val messageDigest = MessageDigest.getInstance("MD5")
        messageDigest.update(str.toByteArray(charset("UTF-8")))
        val digest = messageDigest.digest()
        val sb = StringBuffer()
        for (b in digest) {
            sb.append(String.format("%02x", b))
        }
        return sb.toString()
    }

    private fun cacheKey(url: URL): String {
        return cacheKey(url.toString())
    }

    private fun cacheDir(cacheKey: String): File {
        return File(context.cacheDir.absolutePath + "/" + cacheKey + "/")
    }

    private fun readAsBytes(inputStream: InputStream): ByteArray {
        val byteArrayOutputStream = ByteArrayOutputStream()
        val byteArray = ByteArray(2048)
        while (true) {
            val count = inputStream.read(byteArray, 0, 2048)
            if (count <= 0) {
                break
            }
            else {
                byteArrayOutputStream.write(byteArray, 0, count)
            }
        }
        return byteArrayOutputStream.toByteArray()
    }

    private fun inflate(byteArray: ByteArray): ByteArray? {
        val inflater = Inflater()
        inflater.setInput(byteArray, 0, byteArray.size)
        val inflatedBytes = ByteArray(2048)
        val inflatedOutputStream = ByteArrayOutputStream()
        while (true) {
            val count = inflater.inflate(inflatedBytes, 0, 2048)
            if (count <= 0) {
                break
            } else {
                inflatedOutputStream.write(inflatedBytes, 0, count)
            }
        }
        return inflatedOutputStream.toByteArray()
    }

    private fun unzip(inputStream: InputStream, cacheKey: String) {
        val cacheDir = this.cacheDir(cacheKey)
        cacheDir.mkdirs()
        val zipInputStream = ZipInputStream(BufferedInputStream(inputStream))
        while (true) {
            val zipItem = zipInputStream.nextEntry ?: break
            if (zipItem.name.contains("/")) {
                continue
            }
            val file = File(cacheDir, zipItem.name)
            val fileOutputStream = FileOutputStream(file)
            val buff = ByteArray(2048)
            while (true) {
                val readBytes = zipInputStream.read(buff)
                if (readBytes <= 0) {
                    break
                }
                fileOutputStream.write(buff, 0, readBytes)
            }
            fileOutputStream.close()
            zipInputStream.closeEntry()
        }
        zipInputStream.close()
    }

}