package com.bengelhaupt.instacrawl.model.follow

import com.bengelhaupt.instacrawl.model.base.PaginatedEdge
import kotlinx.serialization.Serializable

@Serializable
data class FollowingsListWrapper(
    val edge_follow: PaginatedEdge<FollowListItem>
)
