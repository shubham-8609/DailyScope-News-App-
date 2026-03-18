package com.codeleg.dailyscope.utils

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.bumptech.glide.Glide
import com.codeleg.dailyscope.R
import com.codeleg.dailyscope.ui.activity.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object NotificationHelper {

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showBreakingNews(context: Context, title:String, content:String){

        val notification = NotificationCompat.Builder(context , NotificationChannel.BREAKING_NEWS).apply {
            setSmallIcon(R.drawable.app_icon_monochrome)
            setContentTitle(title)
            setContentText(content)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setAutoCancel(true)
        }.build()
        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt() , notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    suspend  fun showImageNotification(context: Context, title: String, content:String, imageUrl:String){

        val bitmap = withContext(Dispatchers.IO){
            try{
                Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .submit()
                    .get()
            }catch (e: Exception){
                e.printStackTrace()
                Log.d("codeleg", "Failed to load image for notification: ${e.message}")
                null
            }
        }

        val notification = NotificationCompat.Builder(context , NotificationChannel.BREAKING_NEWS).apply {
            setSmallIcon(R.drawable.app_icon_monochrome)
            setContentTitle(title)
            setContentText(content)
            setStyle(if(bitmap != null) NotificationCompat.BigPictureStyle().bigPicture(bitmap).bigLargeIcon(null as Bitmap?) else NotificationCompat.BigTextStyle().bigText(content) )
            setPriority(NotificationCompat.PRIORITY_DEFAULT)
            setAutoCancel(true)
        }.build()
        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt() , notification)
    }

    @RequiresPermission(Manifest.permission.POST_NOTIFICATIONS)
    fun showNewFetchedNewsNotification(context: Context, title: String, content:String){
        val builder = NotificationCompat.Builder(context , NotificationChannel.NEW_FETCHED_NEWS).apply {
            setSmallIcon(R.drawable.app_icon_monochrome)
            setContentTitle(title)
            setContentText(content)
            setPriority(NotificationCompat.PRIORITY_HIGH)
            setAutoCancel(true)
        }

        val intent = Intent(context, MainActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = builder.setContentIntent(pendingIntent).build()

        NotificationManagerCompat.from(context).notify(System.currentTimeMillis().toInt() , notification)
    }


}