package com.example.fireplace4k

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.setPadding
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : Activity() {
    private lateinit var root: LinearLayout
    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null
    private val prefs by lazy { getSharedPreferences("scenes", MODE_PRIVATE) }
    private val pickRequest = 400
    private var pickingSlot = 0
    private val sceneNames = listOf("4K Fireplace")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        showHome()
    }

    private fun baseLayout(): LinearLayout = LinearLayout(this).apply {
        orientation = LinearLayout.VERTICAL
        gravity = Gravity.CENTER
        setPadding(40)
        setBackgroundColor(Color.rgb(16, 16, 16))
        layoutParams = LinearLayout.LayoutParams(-1, -1)
    }

    private fun title(text: String, size: Float): TextView = TextView(this).apply {
        this.text = text
        textSize = size
        setTextColor(Color.WHITE)
        gravity = Gravity.CENTER
        setPadding(12)
    }

    private fun makeButton(text: String, action: () -> Unit): Button = Button(this).apply {
        this.text = text
        textSize = 18f
        isFocusable = true
        isFocusableInTouchMode = true
        setOnClickListener { action() }
        layoutParams = LinearLayout.LayoutParams(-1, -2).apply { setMargins(0, 10, 0, 10) }
    }

    private fun showHome() {
        releasePlayer()
        root = baseLayout()
        root.addView(title("FIREPLACE 4K", 34f))
        root.addView(title("Your living-room ambience • Offline playback", 16f))
        root.addView(title("One fireplace scene • Choose your 4K video once.", 16f))
        sceneNames.forEachIndexed { index, name ->
            val saved = prefs.getString("scene_$index", null)
            val buttonText = if (saved.isNullOrBlank()) "$name  —  Add video" else "$name  —  Play / replace video"
            root.addView(makeButton(buttonText) {
                if (saved.isNullOrBlank()) chooseVideo(index) else playVideo(Uri.parse(saved), name)
            })
        }
        root.addView(title("Choose a genuine 3840×2160 video for 4K. Playback quality also depends on your device and TV.", 13f))
        setContentView(root)
        root.getChildAt(3).requestFocus()
    }

    private fun chooseVideo(slot: Int) {
        pickingSlot = slot
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "video/*"
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
        }
        startActivityForResult(intent, pickRequest)
    }

    @Deprecated("Uses the platform document picker for broad Android compatibility")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == pickRequest && resultCode == RESULT_OK) {
            val uri = data?.data ?: return
            try { contentResolver.takePersistableUriPermission(uri, Intent.FLAG_GRANT_READ_URI_PERMISSION) } catch (_: Exception) { }
            prefs.edit().putString("scene_$pickingSlot", uri.toString()).apply()
            playVideo(uri, sceneNames[pickingSlot])
        }
    }

    private fun playVideo(uri: Uri, sceneName: String) {
        releasePlayer()
        root = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL; setBackgroundColor(Color.BLACK) }
        playerView = PlayerView(this).apply {
            useController = true
            setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
            layoutParams = LinearLayout.LayoutParams(-1, 0, 1f)
            keepScreenOn = true
        }
        val controls = LinearLayout(this).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER
            setBackgroundColor(Color.rgb(20, 20, 20))
        }
        controls.addView(makeButton("← Scenes") { showHome() }.apply {
            layoutParams = LinearLayout.LayoutParams(0, -2, 1f)
        })
        controls.addView(title(sceneName, 16f).apply { layoutParams = LinearLayout.LayoutParams(0, -2, 2f) })
        root.addView(playerView)
        root.addView(controls)
        setContentView(root)
        player = ExoPlayer.Builder(this).build().also { exo ->
            playerView?.player = exo
            exo.setMediaItem(MediaItem.fromUri(uri))
            exo.repeatMode = Player.REPEAT_MODE_ONE
            exo.prepare()
            exo.playWhenReady = true
        }
    }

    private fun releasePlayer() {
        playerView?.player = null
        player?.release()
        player = null
        playerView = null
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    @Deprecated("Hardware back navigation")
    override fun onBackPressed() {
        if (player != null) showHome() else super.onBackPressed()
    }
}
