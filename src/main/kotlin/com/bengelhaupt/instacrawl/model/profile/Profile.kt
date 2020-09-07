package com.bengelhaupt.instacrawl.model.profile

import com.bengelhaupt.instacrawl.model.base.CountWrapper
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val biography: String,
    val external_url: String?,
    val edge_followed_by: CountWrapper,
    val followed_by_viewer: Boolean,
    val edge_follow: CountWrapper,
    val full_name: String,
    val id: String,
    val is_private: Boolean,
    val profile_pic_url_hd: String,
    val username: String,
    val edge_owner_to_timeline_media: CountWrapper
)
