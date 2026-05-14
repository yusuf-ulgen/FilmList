package com.yusufulgen.filmlist.util

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.yusufulgen.filmlist.R

object NotificationHelper {
    fun showNotification(activity: AppCompatActivity, message: String, isError: Boolean = false) {
        val rootView = activity.findViewById<ViewGroup>(android.R.id.content) ?: return
        val context = activity
        
        val notificationView = LayoutInflater.from(context).inflate(R.layout.layout_custom_notification, rootView, false)
        val text = notificationView.findViewById<TextView>(R.id.notificationText)
        val icon = notificationView.findViewById<ImageView>(R.id.notificationIcon)
        
        text.text = message
        if (isError) {
            icon.setImageResource(R.drawable.ic_close)
            icon.setColorFilter(context.getColor(R.color.secondary))
        } else {
            icon.setImageResource(R.drawable.ic_check)
            icon.setColorFilter(context.getColor(R.color.secondary))
        }

        rootView.addView(notificationView)
        
        // Initial position
        notificationView.translationY = -300f
        
        // Target position (below status bar)
        val targetY = 120f
        
        notificationView.animate()
            .translationY(targetY)
            .setDuration(500)
            .setInterpolator(AccelerateDecelerateInterpolator())
            .withEndAction {
                notificationView.postDelayed({
                    notificationView.animate()
                        .translationY(-300f)
                        .setDuration(500)
                        .setInterpolator(AccelerateDecelerateInterpolator())
                        .withEndAction {
                            rootView.removeView(notificationView)
                        }
                }, 3000)
            }
    }
}
