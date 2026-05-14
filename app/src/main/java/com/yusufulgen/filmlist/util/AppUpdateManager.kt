package com.yusufulgen.filmlist.util

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.yusufulgen.filmlist.R
import com.yusufulgen.filmlist.databinding.DialogReleaseNotesBinding
import com.yusufulgen.filmlist.databinding.DialogUpdateAvailableBinding
import org.json.JSONObject
import java.io.InputStream

class AppUpdateManager(private val context: Context) {

    private val prefs = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    fun checkAndShowReleaseNotes() {
        val releaseNotesJson = loadJSONFromAsset("release_notes.json") ?: return
        val jsonObject = JSONObject(releaseNotesJson)
        val versionCode = jsonObject.getInt("versionCode")
        val versionName = jsonObject.getString("versionName")
        val notesArray = jsonObject.getJSONArray("notes")

        val lastShownVersion = prefs.getInt("last_shown_version_notes", 0)

        if (versionCode > lastShownVersion) {
            showReleaseNotesDialog(versionName, notesArray)
            prefs.edit().putInt("last_shown_version_notes", versionCode).apply()
        }
    }

    private fun showReleaseNotesDialog(versionName: String, notesArray: org.json.JSONArray) {
        val dialog = Dialog(context, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen)
        val binding = DialogReleaseNotesBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        binding.versionLabel.text = "Versiyon $versionName"
        
        val notesList = mutableListOf<String>()
        for (i in 0 until notesArray.length()) {
            notesList.add("• ${notesArray.getString(i)}")
        }

        binding.notesRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.notesRecyclerView.adapter = object : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
                val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
                return object : RecyclerView.ViewHolder(view) {}
            }

            override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
                (holder.itemView as TextView).apply {
                    text = notesList[position]
                    setTextColor(context.getColor(R.color.on_background))
                    textSize = 16f
                }
            }

            override fun getItemCount() = notesList.size
        }

        binding.closeButton.setOnClickListener { dialog.dismiss() }
        
        // Handle system insets for the dialog (Edge-to-edge)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.setPadding(
                view.paddingLeft,
                view.paddingTop,
                view.paddingRight,
                insets.bottom + (24 * context.resources.displayMetrics.density).toInt()
            )
            windowInsets
        }

        dialog.show()
    }

    fun checkForUpdates() {
        // Mock update check: In a real app, you would fetch this from a server
        // For demonstration, we'll assume current version is 1 and an update exists with version 2
        val currentVersionCode = try {
            context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        } catch (e: Exception) { 1 }

        val remoteVersionCode = 2 // This would come from your backend

        if (remoteVersionCode > currentVersionCode) {
            showUpdateDialog()
        }
    }

    private fun showUpdateDialog() {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        val binding = DialogUpdateAvailableBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)

        dialog.window?.apply {
            setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            setBackgroundDrawableResource(android.R.color.transparent)
        }

        binding.closeButton.setOnClickListener { dialog.dismiss() }
        
        binding.updateButton.setOnClickListener {
            val appPackageName = context.packageName
            try {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
            } catch (e: Exception) {
                context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
            }
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun loadJSONFromAsset(fileName: String): String? {
        return try {
            val inputStream: InputStream = context.assets.open(fileName)
            val size: Int = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            String(buffer, Charsets.UTF_8)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }
}
