package com.example.loadimagefromurl

import android.content.Context
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

import android.net.ConnectivityManager
import android.os.AsyncTask
import android.util.Log
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import java.io.*
import java.net.URL
import javax.net.ssl.HttpsURLConnection


private const val TAG = "NetworkFragment"
private const val URL_KEY = "UrlKey"

class NetworkFragment : Fragment() {
    private var callback: DownloadCallback<String>? = null
    private var downloadTask: DownloadTask? = null
    private var urlString: String? = null

    companion object {
        /**
         * Static initializer for NetworkFragment that sets the URL of the host it will be
         * downloading from.
         */

        /**
         * adding the headless fragment to the parent fragment manager
         * adding the URL to be downloaded into the fragment manager
         */
        fun getInstance(fragmentManager: FragmentManager, url: String): NetworkFragment {
            Log.i("Rohit:function_Fragment","Getting instance")
            val networkFragment = NetworkFragment()
            val args = Bundle()
            args.putString(URL_KEY, url)
            networkFragment.arguments = args
            fragmentManager.beginTransaction().add(networkFragment, TAG).commit()
            return networkFragment
        }

    }

    /**
     * the context supplied in the below function is the MainActivity Context = from its point of view
     *  deals with only the main activity object
     */
    override fun onAttach(context: Context) {
        Log.i("Rohit:function_Fragment","attach fragment")
        super.onAttach(context)
        //to read
        // Host Activity will handle callbacks from task.
        callback = context as? DownloadCallback<String>
    }

    /**
     * deals with only the initialization of url string
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.i("Rohit:function_Fragment","create fragment")
        urlString = arguments?.getString(URL_KEY)
        Log.i("Rohit:function_Fragment","Start Download from create method")
        //downloadTask might not have been initialized
        downloadTask?.cancel(true)
        /*downloadTask = callback?.let {
            DownloadTask(it).apply{
                this.execute(urlString)
            }
        }*/

        if(callback != null){
            Log.i("Rohit:Fragment","Callback is not null")
        }else{
            Log.i("Rohit:Fragment","Callback is null")
        }

        //same as above
        callback?.also {
            downloadTask = DownloadTask(it).apply {
                execute(urlString)
            }
        }
    }

    /**
     * deals with only the main activity context
     */
    override fun onDetach() {
        super.onDetach()
        Log.i("Rohit:function_Fragment","detach download")
        // Clear reference to host Activity to avoid memory leak.
        callback = null
    }

    /**
     * deals with downloadTask
     */
    override fun onDestroy() {
        Log.i("Rohit:function_Fragment","destroy download")
        // Cancel task when Fragment is destroyed.
        downloadTask?.cancel(true)
        super.onDestroy()
    }

    /**
     * Start non-blocking execution of DownloadTask.
     * deals with downloadTask
     */
    /*fun startDownload() {
        Log.i("Rohit:function_Fragment","StartDownload")
        //downloadTask might not have been initialized
        downloadTask?.cancel(true)

        *//*downloadTask = callback?.let {
            DownloadTask(it).apply{
                this.execute(urlString)
            }
        }*//*

        if(callback != null){
            Log.i("Rohit:Fragment","Callback is not null")
        }else{
            Log.i("Rohit:Fragment","Callback is null")
        }

        //same as above
        callback?.also {
            downloadTask = DownloadTask(it).apply {
                execute(urlString)
            }
        }
    }*/

    /**
     * Cancel (and interrupt if necessary) any ongoing DownloadTask execution.
     * deals with only the downloadTask
     */
    fun cancelDownload() {
        Log.i("Rohit:function_Fragment","cancel download")
        downloadTask?.cancel(true)
    }

}

/**
 * Implementation of AsyncTask designed to fetch data from the network.
 */
private class DownloadTask(callback: DownloadCallback<String>)
    : AsyncTask<String, Int, DownloadTask.Result>() {

    private var callback: DownloadCallback<String>? = null

    init {
        Log.i("Rohit:DownLoadTask","init")
        this.callback = callback
        //setCallback(callback)
    }

    /*internal fun setCallback(callback: DownloadCallback<String>) {
        this.callback = callback
    }*/
    /**
     * Wrapper class that serves as a union of a result value and an exception. When the download
     * task has completed, either the result value or exception can be a non-null value.
     * This allows you to pass exceptions to the UI thread that were thrown during doInBackground().
     */
    internal class Result {
        var resultValue: String? = null
        var exception: Exception? = null
        var drawable: RoundedBitmapDrawable? = null

        constructor(resultValue: String) {
            this.resultValue = resultValue
        }

        constructor(drawable: RoundedBitmapDrawable){
            this.drawable = drawable
        }

        constructor(exception: Exception) {
            this.exception = exception
        }
    }

    /**
     * Cancel background network operation if we do not have network connectivity.
     */
    override fun onPreExecute() {
        Log.i("Rohit:DownloadTask","Pre Executing")
        if (callback != null) {
            val networkInfo = callback?.getActiveNetworkInfo()
            if (networkInfo?.isConnected == false
                || networkInfo?.type != ConnectivityManager.TYPE_WIFI
                && networkInfo?.type != ConnectivityManager.TYPE_MOBILE) {
                // If no connectivity, cancel task and update Callback with null data.
                Log.i("Rohit:Download Task","No connectivity")
                callback?.updateFromDownload(null)
                cancel(true)
            }
            else {
                Log.i("Rohit:Download Task","Yes connectivity")
            }
        }
    }

    /**
     * Defines work to perform on the background thread.
     */
    override fun doInBackground(vararg urls: String): DownloadTask.Result? {
        Log.i("Rohit: DownloadTask","Doing the background")
        var result: Result? = null
        Log.i("Rohit:Download Task","Checking urls ${urls.toString()}")
        if (!isCancelled && urls.isNotEmpty()) {
            Log.i("Rohit:Download Task","Urls not empty")
            val urlString = urls[0]
            Log.i("Rohit:Download Task","Url[0] is $urlString")
            result = try {
                val url = URL(urlString)
//                val resultString = downloadUrl(url)

                val resultString = url.content as InputStream

                if (resultString != null) {
                    val d: RoundedBitmapDrawable = RoundedBitmapDrawableFactory.create(Resources.getSystem() , resultString)
                    d.isCircular = true
                    Result(d)
                } else {
                    throw IOException("No response received.")
                }
            } catch (e: Exception) {
                Result(e)
            }

        }

        return result
    }

    /**
     * Updates the DownloadCallback with the result.
     */
    override fun onPostExecute(result: Result?) {
        Log.i("Rohit:DownloadTask","Post execution")
        Log.i("Rohit:Download Task","Result is  ${result.toString()}")
        callback?.apply {
            result?.exception?.also { exception ->
                Log.i("Rohit:Download Task","Exception encountered")
                Log.i("Rohit:Download Task","Exception is ${exception.message}")
                return
            }

            if (result != null) {
                Log.i("Rohit:Download Task","Sending result manually")
                result.resultValue?.let { this.updateFromDownload(it) }
            }

            result?.resultValue?.also { resultValue ->
                Log.i("Rohit:Download Task","Sending result")
                try{
                    updateFromDownload(resultValue)
                }
                catch (e: Exception){
                    Log.i("Rohit:Download Task","Exception is $e")
                }
                Log.i("Rohit:Download Task","Sending result")
            }

            result?.drawable?.also { drawable ->
                Log.i("Rohit:Download Task","Sending result")
                try{
                    updateFromDownloadDraw(drawable)
                }
                catch (e: Exception){
                    Log.i("Rohit:Download Task","Exception is $e")
                }
                Log.i("Rohit:Download Task","Sending result")
            }

            finishDownloading()
            Log.i("Rohit:Download Task","Download finish")
        }
    }

    /**
     * Override to add special behavior for cancelled AsyncTask.
     */
    override fun onCancelled(result: Result) {}

    /**
     * Given a URL, sets up a connection and gets the HTTP response body from the server.
     * If the network request is successful, it returns the response body in String form. Otherwise,
     * it will throw an IOException.
     */
    @Throws(IOException::class)
    private fun downloadUrl(url: URL): String? {
        var connection: HttpsURLConnection? = null
        return try {
            connection = (url.openConnection() as? HttpsURLConnection)
            connection?.run {
                // Timeout for reading InputStream arbitrarily set to 3000ms.
                readTimeout = 3000
                // Timeout for connection.connect() arbitrarily set to 3000ms.
                connectTimeout = 3000
                // For this use case, set HTTP method to GET.
                requestMethod = "GET"
                // Already true by default but setting just in case; needs to be true since this request
                // is carrying an input (response) body.
                doInput = true
                // Open communications link (network traffic occurs here).
                connect()
                publishProgress(CONNECT_SUCCESS)
                if (responseCode != HttpsURLConnection.HTTP_OK) {
                    throw IOException("HTTP error code: $responseCode")
                }
                // Retrieve the response body as an InputStream.
                publishProgress(GET_INPUT_STREAM_SUCCESS, 0)
                inputStream?.let { stream ->
                    // Converts Stream to String with max length of 500.
                    readStream(stream, 500)
                }
            }
        } finally {
            // Close Stream and disconnect HTTPS connection.
            connection?.inputStream?.close()
            connection?.disconnect()
        }
    }

    /**
     * Converts the contents of an InputStream to a String.
     */
    @Throws(IOException::class, UnsupportedEncodingException::class)
    fun readStream(stream: InputStream, maxReadSize: Int): String? {
        val reader: Reader? = InputStreamReader(stream, "UTF-8")
        val rawBuffer = CharArray(maxReadSize)
        val buffer = StringBuffer()
        var readSize: Int = reader?.read(rawBuffer) ?: -1
        var maxReadBytes = maxReadSize
        while (readSize != -1 && maxReadBytes > 0) {
            if (readSize > maxReadBytes) {
                readSize = maxReadBytes
            }
            buffer.append(rawBuffer, 0, readSize)
            maxReadBytes -= readSize
            readSize = reader?.read(rawBuffer) ?: -1
        }
        return buffer.toString()
    }


}

