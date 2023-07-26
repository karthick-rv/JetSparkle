package com.example.jetsparkle.download_manager

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.DOWNLOAD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Environment
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.*


var downloadID: Long? = null


private val onDownloadComplete: BroadcastReceiver = object : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent) {
        //Fetching the download id received with the broadcast
        val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        //Checking if the received broadcast is for our enqueued download by matching download id
        if (downloadID == id) {
            Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT).show()
        }
    }
}


//https://medium.com/@aungkyawmyint_26195/downloading-file-properly-in-android-d8cc28d25aca

@Composable
fun DownloadManagerScreen (
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val context = LocalContext.current

    Column {
        Button(onClick = {
            startDownloadRequest(context)
            context.registerReceiver(onDownloadComplete,IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
        }, modifier = Modifier.padding(10.dp,10.dp)) {
            Text(text = "Start Download")
        }
    }

}


@SuppressLint("Range")
fun startDownloadRequest(context: Context) {
    val url = "http://speedtest.ftp.otenet.gr/files/test10Mb.db"
    var fileName = url.substring(url.lastIndexOf('/') + 1)
    fileName = fileName.substring(0, 1).uppercase(Locale.getDefault()) + fileName.substring(1)


    val ext: File = Environment.getExternalStorageDirectory()
    val newDir =
        File((ext.path + "/" + Environment.DIRECTORY_DOCUMENTS) + "/" + "testFile")

    if (!newDir.exists()) {
        newDir.mkdir()
    }


    val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(url))
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        .setDestinationUri(Uri.fromFile(newDir))
        .setTitle(fileName)
        .setDescription("Downloading")
        .setRequiresCharging(false)
        .setAllowedOverMetered(true)
        .setAllowedOverRoaming(true)

    val downloadManager: DownloadManager? = context.getSystemService(DOWNLOAD_SERVICE) as DownloadManager?
    downloadID = downloadManager?.enqueue(request) // enqueue puts the download request in the queue.


    var finishDownload = false
    var progress: Int
    while (!finishDownload) {
        val cursor: Cursor = downloadManager!!.query(
            DownloadManager.Query().setFilterById(
                downloadID!!
            )
        )
        if (cursor.moveToFirst()) {
            val status: Int = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
            when (status) {
                DownloadManager.STATUS_FAILED -> {
                    finishDownload = true
                }
                DownloadManager.STATUS_PAUSED -> {}
                DownloadManager.STATUS_PENDING -> {}
                DownloadManager.STATUS_RUNNING -> {
                    val total: Long =
                        cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                    if (total >= 0) {
                        val downloaded: Long =
                            cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                        progress = (downloaded * 100L / total).toInt()
                    }
                }
                DownloadManager.STATUS_SUCCESSFUL -> {
                    progress = 100
                    finishDownload = true
                    Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }

}
