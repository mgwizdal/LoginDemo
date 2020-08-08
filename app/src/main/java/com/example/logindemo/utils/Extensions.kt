package com.example.logindemo.utils

import android.content.Context
import android.view.View
import android.widget.Toast
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable


infix fun CompositeDisposable.include(observable: Disposable) {
    this.add(observable)
}

fun Context.showToast(text: String) {
    Toast.makeText(this, text, Toast.LENGTH_LONG).show()
}

fun View.show() {
    visibility = View.VISIBLE
}
fun View.makeInvisible() {
    visibility = View.INVISIBLE
}
fun View.hide() {
    visibility = View.GONE
}
