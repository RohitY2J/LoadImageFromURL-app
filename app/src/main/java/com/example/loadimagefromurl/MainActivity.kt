package com.example.loadimagefromurl

import android.content.Context
import android.graphics.Color.green
import android.graphics.drawable.Drawable
import android.graphics.drawable.RotateDrawable
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import androidx.core.graphics.drawable.RoundedBitmapDrawable
import androidx.fragment.app.FragmentActivity
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.squareup.picasso.Picasso
import java.io.InputStream
import java.lang.Error
import java.lang.Exception
import java.net.URL

class MainActivity : FragmentActivity(), DownloadCallback<String>, SwipeRefreshLayout.OnRefreshListener {
    private lateinit var presenter: MainActivityPresenter
    private lateinit var imageView: ImageView
    private var networkFragment: NetworkFragment? = null
    private var downloading = false
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        presenter = MainActivityPresenter(this)
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout)
        swipeRefreshLayout.setOnRefreshListener(this)
        //swipeRefreshLayout.isRefreshing = true
        swipeRefreshLayout.setColorScheme(R.color.blue,
            R.color.green, R.color.orange, R.color.purple);
        networkFragment = NetworkFragment.getInstance(supportFragmentManager, "https://th.bing.com/th/id/OIP.OC6w23HA8bZp77lN8TXY3wAAAA?pid=Api&rs=1")
        networkFragment!!.arguments?.toString()?.let { Log.i("Rohit:Arguments", it) }
    }

    private fun startDownload() {
        Log.i("Rohit:function","Starting Download")
        if (!downloading) {
            // Execute the async download.
            networkFragment?.apply {
                startDownload()
                downloading = true
            }
        }
    }
    //hello world
    override fun onStart() {
        super.onStart()
        Log.i("Rohit","onStartInvoked")
        //Picasso.with(applicationContext).load(resources.getString(R.string.castle_url)).into(imageView)
        loadImage()
    }

    fun loadImage(){
        val imageUrl = resources.getString(R.string.castle_url)
        //Log.i("Rohit","No Crash $imageUrl")
        try {
            val input = URL(imageUrl).content as InputStream
            var d = Drawable.createFromStream(input, "src")
            imageView.setImageDrawable(d)
            Log.i("Rohit", input.toString())
        }
        catch (e: Exception){
            Log.i("Rohit","Error $e")
        }
    }

    override fun updateFromDownload(result: String?) {
        Log.i("Rohit","View updated")
    }

    override fun getActiveNetworkInfo(): NetworkInfo {
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return connectivityManager.activeNetworkInfo!!
    }

    override fun onProgressUpdate(progressCode: Int, percentComplete: Int) {
        when (progressCode) {
            // You can add UI behavior for progress updates here.
            ERROR -> {
            }
            CONNECT_SUCCESS -> {
            }
            GET_INPUT_STREAM_SUCCESS -> {
            }
            PROCESS_INPUT_STREAM_IN_PROGRESS -> {
            }
            PROCESS_INPUT_STREAM_SUCCESS -> {
            }
        }

    }

    override fun finishDownloading() {
        Log.i("Rohit","Downloading finished")
        downloading = false
        networkFragment?.cancelDownload()
    }

    override fun updateFromDownloadDraw(result: RoundedBitmapDrawable?) {
        imageView.setImageDrawable(result)
        Log.i("Rohit", "Drawn")
    }

    override fun onRefresh() {
        Log.i("Rohit","Listening")
        //networkFragment?.startDownload()
        swipeRefreshLayout.isRefreshing = false
    }


}