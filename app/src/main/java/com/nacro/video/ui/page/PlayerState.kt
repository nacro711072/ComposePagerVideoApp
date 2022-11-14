package com.nacro.video.ui.page

import androidx.compose.runtime.Stable
import androidx.media3.common.Player
import com.nacro.video.ext.timer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@Stable
class PlayerState(
    val player: Player,
    coroutine: CoroutineScope
) {

    val duration: Long
        get() = player.duration

    private val _currentPosition: MutableStateFlow<Long> = MutableStateFlow(0L)
    val currentPosition = _currentPosition.asStateFlow()

    private val _isPlayingEnd = MutableStateFlow(false)
    val isPlayingEnd = _isPlayingEnd.asStateFlow()

    init {
        coroutine.launch {
            timer(0L, 15L).collect {
                if (player.isPlaying) {
                    _currentPosition.value = player.currentPosition
                }
                _isPlayingEnd.value = player.playbackState == Player.STATE_ENDED
            }
        }
    }

    fun prepare() {
        player.prepare()
    }
    fun release() {
        player.release()
    }

    fun play() {
        player.play()
    }

    fun pause() {
        player.pause()
    }

    fun stop() {
        player.seekTo(0L)
        player.stop()
        _currentPosition.value = 0L
    }


}