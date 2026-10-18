
package com.example.fireplace4k

import android.app.Activity
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

class MainActivity : Activity() {

    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null
    private var crackling: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        hideSystemUI()

        playFireplace()
        prepareCrackling()
    }

    private fun hideSystemUI() {
        window.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION)
    }

    private fun playFireplace() {
        val root = FrameLayout(this).apply {
            setBackgroundColor(Color.BLACK)
        }

        playerView = PlayerView(this).apply {
            useController = false
            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
            setShutterBackgroundColor(Color.BLACK)
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }

        root.addView(playerView)
        setContentView(root)

        player = ExoPlayer.Builder(this).build().also { exo ->
            playerView?.player = exo

            exo.addListener(object : Player.Listener {
                override fun onRenderedFirstFrame() {
                    if (crackling?.isPlaying == false) {
                        crackling?.start()
                    }
                }
            })

            exo.setMediaItem(
                MediaItem.fromUri(Uri.parse("asset:///fireplace.mp4"))
            )
            exo.repeatMode = Player.REPEAT_MODE_ONE
            exo.prepare()
            exo.playWhenReady = true
        }
    }

    private fun prepareCrackling() {
        try {
            assets.openFd("fire_crackling.mp3").use { descriptor ->
                crackling = MediaPlayer().apply {
                    setDataSource(
                        descriptor.fileDescriptor,
                        descriptor.startOffset,
                        descriptor.length
                    )
                    isLooping = true
                    setVolume(0.8f, 0.8f)
                    prepare()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStart() {
        super.onStart()

        hideSystemUI()

        playerView?.player = player

        player?.let { exo ->
            if (exo.playbackState == Player.STATE_IDLE) {
                exo.prepare()
            }

            exo.playWhenReady = true
        }

        crackling?.let { audio ->
            if (!audio.isPlaying) {
                try {
                    audio.start()
                } catch (e: IllegalStateException) {
                    e.printStackTrace()
                }
            }
        }
    }

    override fun onStop() {
        player?.playWhenReady = false

        crackling?.let { audio ->
            if (audio.isPlaying) {
                audio.pause()
            }
        }

        super.onStop()
    }

    override fun onDestroy() {
        playerView?.player = null

        player?.release()
        player = null

        crackling?.release()
        crackling = null

        super.onDestroy()
    }
}
