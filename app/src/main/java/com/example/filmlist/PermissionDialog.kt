package com.example.filmlist

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog
import android.content.DialogInterface

class PermissionDialog : DialogFragment() {
    interface PermissionListener {
        fun onPermissionGranted(chooseAlways: Boolean)
        fun onPermissionDenied()
        fun onPermissionChangeRequested()  // İzin durumu değiştirme isteği
    }

    private var permissionListener: PermissionListener? = null

    fun setPermissionListener(listener: PermissionListener) {
        permissionListener = listener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): android.app.Dialog {
        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle("Galeriye Erişim")
        builder.setMessage("Galeriye erişim izni vermek ister misiniz?")

        builder.setPositiveButton("Her Zaman İzin Ver") { dialog, id ->
            permissionListener?.onPermissionGranted(true)
        }

        builder.setNeutralButton("Bu Seferlik İzin Ver") { dialog, id ->
            permissionListener?.onPermissionGranted(false)
        }

        builder.setNegativeButton("İzin Verme") { dialog, id ->
            permissionListener?.onPermissionDenied()
        }

        builder.setPositiveButton("Her Zaman İzin Ver") { dialog, id ->
            permissionListener?.onPermissionChangeRequested() // İzin değiştirme talebi
        }

        return builder.create()
    }
}
