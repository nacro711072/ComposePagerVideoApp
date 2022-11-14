package com.nacro.video.ui.page

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nacro.video.model.domain.VideoData
import com.nacro.video.repository.VideoRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel(
    private val repository: VideoRepository
): ViewModel() {

    private val _video: MutableStateFlow<List<VideoData>> = MutableStateFlow(listOf())

    val video = _video.asStateFlow()

    fun getVideo() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getVideos().collect {
                _video.value = it
            }

        }
    }
}