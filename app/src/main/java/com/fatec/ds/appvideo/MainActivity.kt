package com.fatec.ds.appvideo

import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

class MainActivity : AppCompatActivity() {

    private val video1Url = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/flower.mp4"
    private val video2Url = "https://interactive-examples.mdn.mozilla.net/media/cc0-videos/friday.mp4"

    private var player: ExoPlayer? = null
    private lateinit var playerView: PlayerView
    private lateinit var btnPlayPause: Button
    private lateinit var btnFullScreen: Button
    private lateinit var btnSairTelaCheia: Button
    private lateinit var controlsLayout: LinearLayout
    private lateinit var tvTitulo: TextView
    private lateinit var tvAtribuicao: TextView

    private var isFullScreen = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        playerView          = findViewById(R.id.playerView)
        controlsLayout      = findViewById(R.id.controlsLayout)
        tvTitulo            = findViewById(R.id.tvTitulo)
        tvAtribuicao        = findViewById(R.id.tvAtribuicao)
        btnPlayPause        = findViewById(R.id.btnPlayPause)
        btnFullScreen       = findViewById(R.id.btnFullScreen)
        btnSairTelaCheia    = findViewById(R.id.btnSairTelaCheia)

        val btnVideo1: Button   = findViewById(R.id.btnVideo1)
        val btnVideo2: Button   = findViewById(R.id.btnVideo2)
        val btnReiniciar: Button = findViewById(R.id.btnReiniciar)

        initPlayer()

        btnVideo1.setOnClickListener    { playVideoFromUrl(video1Url) }
        btnVideo2.setOnClickListener    { playVideoFromUrl(video2Url) }

        btnPlayPause.setOnClickListener {
            player?.let {
                if (it.isPlaying) it.pause() else it.play()
            }
        }

        btnReiniciar.setOnClickListener {
            player?.apply {
                seekTo(0)
                play()
            }
        }

        btnFullScreen.setOnClickListener    { entrarTelaCheia() }
        btnSairTelaCheia.setOnClickListener { sairTelaCheia() }
    }

    private fun initPlayer() {
        player = ExoPlayer.Builder(this).build().also { exoPlayer ->
            playerView.player = exoPlayer

            exoPlayer.addListener(object : Player.Listener {
                override fun onIsPlayingChanged(isPlaying: Boolean) {
                    btnPlayPause.text = getString(
                        if (isPlaying) R.string.pausar else R.string.reproduzir
                    )
                }
            })
        }

        playVideoFromUrl(video1Url)
    }

    private fun playVideoFromUrl(url: String) {
        player?.apply {
            setMediaItem(MediaItem.fromUri(url))
            prepare()
            playWhenReady = true
        }
    }

    private fun entrarTelaCheia() {
        isFullScreen = true

        tvTitulo.visibility         = View.GONE
        controlsLayout.visibility   = View.GONE
        tvAtribuicao.visibility     = View.GONE
        btnSairTelaCheia.visibility = View.VISIBLE

        supportActionBar?.hide()

        val lp = playerView.layoutParams
        lp.height = android.view.ViewGroup.LayoutParams.MATCH_PARENT
        playerView.layoutParams = lp

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.let { c ->
                c.hide(WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars())
                c.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_FULLSCREEN
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            )
        }
    }

    private fun sairTelaCheia() {
        isFullScreen = false

        tvTitulo.visibility         = View.VISIBLE
        controlsLayout.visibility   = View.VISIBLE
        tvAtribuicao.visibility     = View.VISIBLE
        btnSairTelaCheia.visibility = View.GONE

        supportActionBar?.show()

        val lp = playerView.layoutParams
        lp.height = (220 * resources.displayMetrics.density).toInt()
        playerView.layoutParams = lp

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.show(
                WindowInsets.Type.statusBars() or WindowInsets.Type.navigationBars()
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
        }
    }

    override fun onStop() {
        super.onStop()
        player?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        player?.release()
        player = null
    }
}
