package com.example.jetsparkle.work_manager

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.Constraints
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager

@Composable
fun WorkManagerScreen() {

    val context = LocalContext.current

    Column {
        Button(onClick = {
            startNotificationWorker(context)
        }, modifier = Modifier.padding(10.dp,10.dp)) {
            Text(text = "Send Notification")
        }
    }
    
}

fun startNotificationWorker(current: Context) {

    val constraints = Constraints.Builder()
        .setRequiresBatteryNotLow(true)
        .build()

    val workRequest = OneTimeWorkRequestBuilder<NotificationWorkManager>()
        .setConstraints(constraints)
        .build()



    WorkManager
        .getInstance(current)
        .enqueue(workRequest)

    Toast.makeText(current, "WorkManager Request Made", Toast.LENGTH_SHORT).show()
}