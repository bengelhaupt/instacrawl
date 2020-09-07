package com.bengelhaupt.instacrawl.model.follow

import kotlinx.serialization.Serializable

@Serializable
data class FollowListItem(
    val id: String,
    val username: String,
    val full_name: String,
    val profile_pic_url: String,
    val is_private: Boolean
)
