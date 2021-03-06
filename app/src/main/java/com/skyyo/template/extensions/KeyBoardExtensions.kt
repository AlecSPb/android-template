package com.skyyo.template.extensions

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment

fun View?.hideKeyboard(activity: Activity?) {
    (activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager?)?.let {
        if (it.isActive) it.hideSoftInputFromWindow(this@hideKeyboard?.windowToken, 0)
    }
}

fun Fragment?.showKeyboard() {
    (this?.activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).toggleSoftInput(
        InputMethodManager.SHOW_IMPLICIT,
        InputMethodManager.HIDE_IMPLICIT_ONLY
    )
}
