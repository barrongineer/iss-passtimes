package com.barron.isspasstimes.activities

import android.Manifest
import android.app.ProgressDialog
import android.content.Context
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.barron.isspasstimes.ISSPassTimesApplication
import com.barron.isspasstimes.R
import com.barron.isspasstimes.clients.ISSAPIClient
import com.barron.isspasstimes.models.PassTime
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.indeterminateProgressDialog
import org.jetbrains.anko.toast
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class MainActivity : AppCompatActivity() {

    companion object {
        const val LOCATION_REQUEST_CODE = 1
    }

    private val mTag: String = this.javaClass.name

    @Inject
    lateinit var mAPIClient: ISSAPIClient
    lateinit var mLoadingDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ISSPassTimesApplication.graph.inject(this)

        mLoadingDialog = indeterminateProgressDialog(title = "Fetching data")
        mLoadingDialog.show()

        val permStatus = checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
        if (permStatus != PackageManager.PERMISSION_GRANTED) {
            bg {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_REQUEST_CODE)
            }
        } else {
            fetchPassTimes(this)
        }
    }

    private fun fetchPassTimes(ctx: Context) {
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            val location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
            mAPIClient.getPassTimes(location.latitude, location.longitude)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ data ->
                        mLoadingDialog.dismiss()
                        main_passtimes.adapter = PassTimesAdapter(data.response, ctx)
                    }, { e ->
                        toast(e.message ?: "Failed to retrieve pass times")
                    })
        } catch (e: SecurityException) {
            toast("This application needs access to your location to continue")
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
                    fetchPassTimes(this)
                } else {
                    toast("This application needs access to your location to continue")
                }
                return
            }
        }
    }

    class PassTimesAdapter(val passTimes: List<PassTime>, val ctx: Context) : BaseAdapter() {

        private val mLocalOffset = OffsetDateTime.now().offset
        private val mDateFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm a")

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val inflater = LayoutInflater.from(ctx)
            val row = inflater.inflate(R.layout.row_passtime, parent, false)

            val risetimeTextView = row.findViewById<TextView>(R.id.passtime_risetime)
            val timestamp = passTimes[position].risetime
            val risetimeText = "Rise Time: ${getFormattedLocalTime(timestamp)}"
            risetimeTextView.text = risetimeText

            val durationTextView = row.findViewById<TextView>(R.id.passtime_duration)
            val durationText = "Duration: ${passTimes[position].duration} seconds"
            durationTextView.text = durationText

            return row
        }

        override fun getItem(position: Int) = passTimes[position]

        override fun getItemId(position: Int) = position.toLong()

        override fun getCount() = passTimes.size

        private fun getFormattedLocalTime(timestamp: Long): String {
            val dateTime = LocalDateTime.ofEpochSecond(
                    timestamp,
                    0,
                    mLocalOffset)
            return mDateFormatter.format(dateTime)
        }

    }
}
