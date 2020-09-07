package com.bengelhaupt.instacrawl.model.aggregation

import com.bengelhaupt.instacrawl.model.follow.FollowListItem
import com.bengelhaupt.instacrawl.model.likes.LikeListItem
import com.bengelhaupt.instacrawl.model.post.Post
import com.bengelhaupt.instacrawl.model.post.PostListItem
import com.bengelhaupt.instacrawl.model.profile.Profile
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
data class User internal constructor(
    var id: String,
    var username: String,
    @Transient var userStore: UserStore = UserStore()
) {
    var fullName: String? = null
    var isPrivate: Boolean? = null
    var biography: String? = null
    var externalUrl: String? = null
    var profilePicUrl: String? = null
    var followerCount: Int? = null
    var followingCount: Int? = null
    var postingCount: Int? = null
    var followers: List<UserReference>? = null
    var followings: List<UserReference>? = null
    var postings: List<Posting>? = null

    fun updateProfile(profile: Profile?) {
        id = profile?.id ?: id
        username = profile?.username ?: username
        fullName = profile?.full_name
        isPrivate = profile?.is_private
        biography = profile?.biography
        externalUrl = profile?.external_url
        profilePicUrl = profile?.profile_pic_url_hd
        followerCount = profile?.edge_followed_by?.count
        followingCount = profile?.edge_follow?.count
        postingCount = profile?.edge_owner_to_timeline_media?.count
    }

    fun updateFollowers(followerList: List<FollowListItem>?) {
        followers = followerList?.map {
            UserReference(it.id, it.username, userStore).apply {
                user.fullName = it.full_name
                user.isPrivate = it.is_private
            }
        }
    }

    fun updateFollowings(followingList: List<FollowListItem>?) {
        followings = followingList?.map {
            UserReference(it.id, it.username, userStore).apply {
                user.fullName = it.full_name
                user.isPrivate = it.is_private
            }
        }
    }

    fun updatePostings(postingList: List<PostListItem>?) {
        postings = postingList?.map {
            Posting(
                Posting.Companion.PostingType.parse(it.__typename)!!,
                it.id,
                it.shortcode,
                it.edge_media_to_comment.count,
                it.edge_media_preview_like.count,
                it.edge_media_to_caption.edges.firstOrNull()?.node?.text,
                it.taken_at_timestamp,
                it.display_url,
                it.is_video,
                it.video_url,
                it.video_view_count,
                userStore
            )
        }
    }

    companion object {

        @Serializable
        data class Posting(
            var type: PostingType,
            var id: String,
            var shortCode: String,
            var commentCount: Int,
            var likeCount: Int,
            var caption: String?,
            var takenAtTimestamp: Long,
            var displayUrl: String,
            var isVideo: Boolean,
            var videoUrl: String? = null,
            var videoViewCount: Int? = null,
            @Transient var userStore: UserStore = UserStore()
        ) {
            var accessibilityCaption: String? = null
            var location: String? = null
            var slides: List<PostingSlide>? = null
            var taggedUsers: List<UserReference>? = null
            var comments: Map<UserReference, String>? = null
            var likes: List<UserReference>? = null

            fun updatePosting(post: Post?) {
                type = PostingType.parse(post?.__typename) ?: type
                id = post?.id ?: id
                shortCode = post?.shortcode ?: shortCode
                commentCount = post?.edge_media_to_comment?.count ?: commentCount
                likeCount = post?.edge_media_preview_like?.count ?: likeCount
                caption = post?.edge_media_to_caption?.edges?.firstOrNull()?.node?.text ?: caption
                takenAtTimestamp = post?.taken_at_timestamp ?: takenAtTimestamp
                displayUrl = post?.display_url ?: displayUrl
                isVideo = post?.is_video ?: isVideo
                videoUrl = post?.video_url ?: videoUrl
                videoViewCount = post?.video_view_count ?: videoViewCount
                accessibilityCaption = post?.accessibility_caption
                location = post?.location?.name
                taggedUsers = post?.edge_media_to_tagged_user?.edges?.map {
                    UserReference(it.node.user.id, it.node.user.username, userStore).apply {
                        user.fullName = it.node.user.full_name
                    }
                }
                comments = post?.edge_media_to_comment?.edges?.associate {
                    UserReference(it.node.owner.id, it.node.owner.username, userStore) to it.node.text
                }
                slides = post?.edge_sidecar_to_children?.edges?.map {
                    PostingSlide(
                        PostingType.parse(it.node.__typename)!!,
                        it.node.id,
                        it.node.shortcode,
                        it.node.accessibility_caption,
                        it.node.display_url,
                        it.node.is_video,
                        it.node.video_url,
                        it.node.video_view_count,
                        it.node.edge_media_to_tagged_user.edges.map {
                            UserReference(it.node.user.id, it.node.user.username, userStore).apply {
                                user.fullName = it.node.user.full_name
                            }
                        }
                    )
                }
            }

            fun updateLikes(likeList: List<LikeListItem>) {
                likes = likeList.map {
                    UserReference(it.id, it.username, userStore).apply {
                        user.fullName = it.full_name
                        user.isPrivate = it.is_private
                    }
                }
            }

            companion object {

                enum class PostingType {
                    IMAGE, SLIDES, VIDEO, OTHER;

                    companion object {

                        fun parse(type: String?): PostingType? {
                            return when (type) {
                                "GraphImage" -> IMAGE
                                "GraphSidecar" -> SLIDES
                                "GraphVideo" -> VIDEO
                                null -> null
                                else -> OTHER
                            }
                        }
                    }
                }

                @Serializable
                data class PostingSlide(
                    var type: PostingType,
                    var id: String,
                    var shortCode: String,
                    var accessibilityCaption: String?,
                    var displayUrl: String,
                    var isVideo: Boolean,
                    var videoUrl: String? = null,
                    var videoViewCount: Int? = null,
                    var taggedUsers: List<UserReference>
                )
            }
        }
    }
}
