package com.example.logindemo.utils

import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class RxSchedulers(
    val disk: Scheduler = Schedulers.single(),
    val io: Scheduler = Schedulers.io(),
    val computation: Scheduler = Schedulers.computation(),
    val ui: Scheduler = AndroidSchedulers.mainThread()
)
