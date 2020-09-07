package com.bengelhaupt.instacrawl.model.follow

import com.bengelhaupt.instacrawl.model.base.PaginatedEdge
import kotlinx.serialization.Serializable

@Serializable
data class FollowerListWrapper(
    val edge_followed_by: PaginatedEdge<FollowListItem>
)
