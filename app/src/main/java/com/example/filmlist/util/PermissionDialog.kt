package com.example.filmlist.util

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.appcompat.app.AlertDialog

class PermissionDialog : DialogFragment() {
    interface PermissionListener {
        fun onPermissionGranted(chooseAlways: Boolean)
        fun onPermissionDenied()
        fun onPermissionChangeRequested()
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

        return builder.create()
    }
}
