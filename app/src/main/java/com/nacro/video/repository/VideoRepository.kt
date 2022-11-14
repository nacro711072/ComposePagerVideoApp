package com.nacro.video.repository

import com.nacro.video.datasource.VideoDataSource
import com.nacro.video.model.domain.VideoData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class VideoRepository(
    private val dataSource: VideoDataSource
) {
    fun getVideos(): Flow<List<VideoData>> {
        return flow {
            emit(dataSource.localData)
        }
    }
}