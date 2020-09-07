package com.bengelhaupt.instacrawl.model.likes

import kotlinx.serialization.Serializable

@Serializable
data class LikeListItem(
    val id: String,
    val username: String,
    val full_name: String,
    val is_private: Boolean
)
