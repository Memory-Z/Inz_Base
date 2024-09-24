package com.inz.base.crash

import androidx.annotation.UiThread

/**
 * Crash Handler Listener .
 */
interface CrashHandlerListener {

    /**
     * start crash handler.
     */
    fun startHandler()

    /**
     * update log callback
     * @param filePath log file path.
     * @param context log content.
     */
    fun uploadLogCallback(filePath: String, context: String)

    /**
     * Show Error Tint Ui.
     */
    @UiThread
    fun showErrorTintUi()
}