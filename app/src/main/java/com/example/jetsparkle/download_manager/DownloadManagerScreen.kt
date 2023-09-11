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
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.*


var downloadID: Long? = null
const val FILE_URL = "http://speedtest.ftp.otenet.gr/files/test10Mb.db"


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
    var progress by remember{ mutableStateOf(0.0f) }
    var showMessage by remember{ mutableStateOf(false) }
    var filePath by remember{ mutableStateOf("") }

    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(15.dp).fillMaxSize(), verticalArrangement = Arrangement.Center) {
        Button(onClick = {
            CoroutineScope(Dispatchers.IO).launch {
                val file = createFile()
                filePath = file.path
                startDownloadRequest(context, file){
                    progress = it
                    if(progress == 1.0f){
                        showMessage = true
                    }
                }
                context.registerReceiver(onDownloadComplete,IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
            }
        }, modifier = Modifier.padding(10.dp,10.dp)) {
            Text(text = "Start Download")
        }

        Spacer(modifier = Modifier.height(40.dp))

        LinearProgressIndicator(progress = progress, modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(30.dp))

        if(showMessage){
            Text(text = "Download Success", style = TextStyle(color = Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold))
            Text(text = "File Path : $filePath", style = TextStyle(color = Color.Black, fontSize = 15.sp))
        }
    }
}

fun createFile(): File {
    val ext: File = Environment.getExternalStorageDirectory()
    val newDir =
        File((ext.path + "/" + Environment.DIRECTORY_DOCUMENTS) + "/" + "testFile")

    if (!newDir.exists()) {
        newDir.mkdir()
    }

    println("Directory Path: " + newDir.absolutePath)

    return newDir
}


@SuppressLint("Range")
suspend fun startDownloadRequest(context: Context,filePath: File, updateProgress: (value: Float) -> Unit) {

    var fileName = FILE_URL.substring(FILE_URL.lastIndexOf('/') + 1)
    fileName = fileName.substring(0, 1).uppercase(Locale.getDefault()) + fileName.substring(1)

    val request: DownloadManager.Request = DownloadManager.Request(Uri.parse(FILE_URL))
        .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
        .setDestinationUri(Uri.fromFile(filePath))
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
                        println("Progress: $progress")
                        updateProgress(progress.div(100f))
                    }
                }
                DownloadManager.STATUS_SUCCESSFUL -> {
                    progress = 100
                    updateProgress(progress.div(100f))
                    finishDownload = true
                    withContext(Dispatchers.Main){
                        Toast.makeText(context, "Download Completed", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
            }
        }
    }

}
