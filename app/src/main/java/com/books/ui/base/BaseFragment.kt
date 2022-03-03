package com.books.ui.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.widget.Toast
import androidx.appcompat.app.AppCompatDialog
import androidx.fragment.app.Fragment
import com.books.R

open class BaseFragment : Fragment() {

    private val progressDialog: AppCompatDialog by lazy {
        AppCompatDialog(requireActivity()).apply {
            setContentView(R.layout.dialog_progress)
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCancelable(false)
        }
    }

    val toast: Toast by lazy {
        Toast.makeText(context, "", Toast.LENGTH_SHORT)
    }

    fun showProgressDialog() {
        if (!progressDialog.isShowing) {
            progressDialog.show()
        }
    }

    fun dismissProgressDialog() {
        if (progressDialog.isShowing) {
            progressDialog.dismiss()
        }
    }

    fun toast(message: String) {
        toast.cancel()
        toast.setText(message)
        toast.show()
    }
}