package com.bengelhaupt.instacrawl.model.post

import com.bengelhaupt.instacrawl.model.base.*
import kotlinx.serialization.Serializable

@Serializable
data class Post(
    val __typename: String,
    val id: String,
    val shortcode: String,
    val display_url: String,
    val accessibility_caption: String? = null,
    val is_video: Boolean,
    val video_url: String? = null,
    val video_view_count: Int? = null,
    val edge_media_to_tagged_user: Edge<UserWrapper<TaggedUser>>,
    val edge_media_to_caption: Edge<TextWrapper>,
    val edge_media_to_comment: PaginatedEdge<Comment>,
    val taken_at_timestamp: Long,
    val edge_media_preview_like: CountWrapper,
    val location: Location?,
    val edge_sidecar_to_children: Edge<SidecarPost>? = null
) {

    @Serializable
    data class TaggedUser(
        val full_name: String,
        val id: String,
        val username: String
    )

    @Serializable
    data class Comment(
        val id: String,
        val text: String,
        val owner: CommentOwner,
        val edge_liked_by: CountWrapper
    ) {

        @Serializable
        data class CommentOwner(
            val id: String,
            val username: String
        )
    }

    @Serializable
    data class Location(
        val name: String
    )

    @Serializable
    data class SidecarPost(
        val __typename: String,
        val id: String,
        val shortcode: String,
        val accessibility_caption: String?,
        val display_url: String,
        val is_video: Boolean,
        val video_url: String? = null,
        val video_view_count: Int? = null,
        val edge_media_to_tagged_user: Edge<UserWrapper<TaggedUser>>
    )
}
