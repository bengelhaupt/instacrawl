package com.bengelhaupt.instacrawl.model.post

import com.bengelhaupt.instacrawl.model.base.PaginatedEdge
import kotlinx.serialization.Serializable

@Serializable
data class PostListWrapper(
    val edge_owner_to_timeline_media: PaginatedEdge<PostListItem>
)
