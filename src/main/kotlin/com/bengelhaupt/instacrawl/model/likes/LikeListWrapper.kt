package com.bengelhaupt.instacrawl.model.likes

import com.bengelhaupt.instacrawl.model.base.PaginatedEdge
import kotlinx.serialization.Serializable

@Serializable
data class LikeListWrapper(
    val edge_liked_by: PaginatedEdge<LikeListItem>
)
