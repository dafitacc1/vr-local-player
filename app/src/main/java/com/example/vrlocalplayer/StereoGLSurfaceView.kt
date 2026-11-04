package com.example.vrlocalplayer

import android.annotation.SuppressLint
import android.content.Context
import android.opengl.GLSurfaceView
import android.util.AttributeSet
import android.view.GestureDetector
import android.view.MotionEvent

class StereoGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    /** Callback for taps on the video. Zoom and pan are changed from the control panel sliders only. */
    interface GestureListener {
        fun onVideoTap()
    }

    lateinit var stereoRenderer: StereoRenderer
        private set

    var gestureListener: GestureListener? = null

    private val gestureDetector = GestureDetector(context,
        object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                gestureListener?.onVideoTap()
                return true
            }
        })

    fun init(
        duplicateSbs: Boolean,
        initialSettings: VrSettings.Values,
        onSurfaceReady: (android.view.Surface) -> Unit
    ) {
        setEGLContextClientVersion(2)
        stereoRenderer = StereoRenderer(duplicateSbs, initialSettings, onSurfaceReady)
        setRenderer(stereoRenderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    fun releaseRendererIfReady() {
        if (::stereoRenderer.isInitialized) {
            stereoRenderer.release()
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!::stereoRenderer.isInitialized) return super.onTouchEvent(event)
        gestureDetector.onTouchEvent(event)
        return true
    }
}
