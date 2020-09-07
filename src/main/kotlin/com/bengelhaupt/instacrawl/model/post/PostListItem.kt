package com.bengelhaupt.instacrawl.model.post

import com.bengelhaupt.instacrawl.model.base.CountWrapper
import com.bengelhaupt.instacrawl.model.base.Edge
import com.bengelhaupt.instacrawl.model.base.TextWrapper
import kotlinx.serialization.Serializable

@Serializable
data class PostListItem(
    val id: String,
    val __typename: String,
    val edge_media_to_caption: Edge<TextWrapper>,
    val shortcode: String,
    val edge_media_to_comment: CountWrapper,
    val taken_at_timestamp: Long,
    val display_url: String,
    val edge_media_preview_like: CountWrapper,
    val is_video: Boolean,
    val video_url: String? = null,
    val video_view_count: Int? = null
)
