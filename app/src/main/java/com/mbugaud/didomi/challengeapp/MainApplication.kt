package com.mbugaud.didomi.challengeapp

import android.app.Application
import com.mbugaud.didomi.challengelib.ConsentManager

class MainApplication: Application() {
    override fun onCreate() {
        super.onCreate()
        ConsentManager.initialize(this)
    }
}