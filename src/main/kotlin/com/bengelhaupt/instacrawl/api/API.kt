package com.bengelhaupt.instacrawl.api

import com.bengelhaupt.instacrawl.model.base.DataWrapper
import com.bengelhaupt.instacrawl.model.base.GraphqlWrapper
import com.bengelhaupt.instacrawl.model.base.ShortcodeMediaWrapper
import com.bengelhaupt.instacrawl.model.base.UserWrapper
import com.bengelhaupt.instacrawl.model.follow.FollowListItem
import com.bengelhaupt.instacrawl.model.follow.FollowerListWrapper
import com.bengelhaupt.instacrawl.model.follow.FollowingsListWrapper
import com.bengelhaupt.instacrawl.model.likes.LikeListItem
import com.bengelhaupt.instacrawl.model.likes.LikeListWrapper
import com.bengelhaupt.instacrawl.model.post.Post
import com.bengelhaupt.instacrawl.model.post.PostListItem
import com.bengelhaupt.instacrawl.model.post.PostListWrapper
import com.bengelhaupt.instacrawl.model.profile.Profile
import com.bengelhaupt.instacrawl.util.Log
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.util.*

class API(cookies: List<String>) {

    companion object {
        const val GET_FOLLOWINGS_QUERY = "d04b0a864b4b54837c0d870b0e77e076"
        const val GET_FOLLOWERS_QUERY = "c76146de99bb02f6415203be841dd25a"
        const val GET_POSTS = "472f257a40c653c64c666ce877d59d2b"
        const val GET_POST = "2418469a2b4d9b47ae7bec08e3ec53ad"
        const val GET_LIKES = "d5d763b1e2acf209d62d22d184488e57"
    }

    val log: Log = Log()

    private val client = OkHttpClient()

    private val json = Json {
        ignoreUnknownKeys = true
    }

    private val cookieQueue: MutableList<String> = cookies.toMutableList()

    fun getProfile(username: String, includeCookie: Boolean = true): Profile {
        return makeRequest<GraphqlWrapper<UserWrapper<Profile>>>(
            "https://www.instagram.com/$username/?__a=1",
            includeCookie = includeCookie
        ).graphql.user
    }

    fun getPostsFor(userId: String, limit: Int = Int.MAX_VALUE, includeCookie: Boolean = true): List<PostListItem> {
        val result = mutableListOf<PostListItem>()

        var hasNextPage = true
        var after: String? = null
        while (hasNextPage && result.size <= limit) {
            val response = makeRequest<DataWrapper<UserWrapper<PostListWrapper>>>(
                GET_POSTS,
                "id",
                userId,
                10000,
                after,
                includeCookie = includeCookie
            )

            hasNextPage = response.data.user.edge_owner_to_timeline_media.page_info.has_next_page
            after = response.data.user.edge_owner_to_timeline_media.page_info.end_cursor

            result.addAll(
                response.data.user.edge_owner_to_timeline_media.edges.map { it.node }
            )
        }

        return result
    }

    fun getPost(shortcode: String, includeCookie: Boolean = true): Post {
        return makeRequest<DataWrapper<ShortcodeMediaWrapper<Post>>>(
            GET_POST,
            "shortcode",
            shortcode,
            1000,
            null,
            "\"fetch_comment_count\":1000",
            includeCookie = includeCookie
        ).data.shortcode_media
    }

    fun getLikesForPost(
        shortcode: String,
        limit: Int = Int.MAX_VALUE,
        includeCookie: Boolean = true
    ): List<LikeListItem> {
        val result = mutableListOf<LikeListItem>()

        var hasNextPage = true
        var after: String? = null
        while (hasNextPage && result.size <= limit) {
            val response = makeRequest<DataWrapper<ShortcodeMediaWrapper<LikeListWrapper>>>(
                GET_LIKES,
                "shortcode",
                shortcode,
                1000,
                after,
                includeCookie = includeCookie
            )

            hasNextPage = response.data.shortcode_media.edge_liked_by.page_info.has_next_page
            after = response.data.shortcode_media.edge_liked_by.page_info.end_cursor

            result.addAll(
                response.data.shortcode_media.edge_liked_by.edges.map { it.node }
            )
        }

        return result
    }

    fun getFollowersFor(
        userId: String,
        limit: Int = Int.MAX_VALUE,
        includeCookie: Boolean = true
    ): List<FollowListItem> {
        val result = mutableListOf<FollowListItem>()

        var hasNextPage = true
        var after: String? = null
        while (hasNextPage && result.size <= limit) {
            val response = makeRequest<DataWrapper<UserWrapper<FollowerListWrapper>>>(
                GET_FOLLOWERS_QUERY,
                "id",
                userId,
                10000,
                after,
                includeCookie = includeCookie
            )

            hasNextPage = response.data.user.edge_followed_by.page_info.has_next_page
            after = response.data.user.edge_followed_by.page_info.end_cursor

            result.addAll(
                response.data.user.edge_followed_by.edges.map { it.node }
            )
        }

        return result
    }

    fun getFollowingsFor(
        userId: String,
        limit: Int = Int.MAX_VALUE,
        includeCookie: Boolean = true
    ): List<FollowListItem> {
        val result = mutableListOf<FollowListItem>()

        var hasNextPage = true
        var after: String? = null
        while (hasNextPage && result.size <= limit) {
            val response = makeRequest<DataWrapper<UserWrapper<FollowingsListWrapper>>>(
                GET_FOLLOWINGS_QUERY,
                "id",
                userId,
                10000,
                after,
                includeCookie = includeCookie
            )

            hasNextPage = response.data.user.edge_follow.page_info.has_next_page
            after = response.data.user.edge_follow.page_info.end_cursor

            result.addAll(
                response.data.user.edge_follow.edges.map { it.node }
            )
        }

        return result
    }

    private inline fun <reified T> makeRequest(
        hash: String,
        idName: String,
        id: String,
        first: Int,
        after: String?,
        params: String? = null,
        includeCookie: Boolean = true,
        retries: Int = 10
    ): T {
        var url =
            "https://www.instagram.com/graphql/query/?query_hash=$hash&variables={\"$idName\":\"$id\",\"first\":$first"
        if (after != null) {
            url += ",\"after\":\"$after\""
        }
        if (params != null) {
            url += ",$params"
        }
        url += "}"
        return makeRequest<T>(
            url,
            includeCookie,
            retries
        )
    }

    private inline fun <reified T> makeRequest(url: String, includeCookie: Boolean = true, retries: Int = 10): T {
        var exception = Exception()
        for (i in 1..retries) {
            try {
                log.i(url)
                val response = client.newCall(
                    Request.Builder()
                        .url(url)
                        .apply {
                            if (includeCookie) {
                                header("Cookie", getCookie())
                            }
                        }
                        .build()
                ).execute()

                if (response.isSuccessful) {
                    return json.decodeFromString<T>(response.body!!.string())
                } else {
                    handleError(response, i)
                }
            } catch (e: Exception) {
                log.e(e)
                exception = e
            }
        }
        throw exception
    }

    private fun handleError(response: Response, retryCount: Int) {
        val error = response.body!!.string()

        val map = try {
            json.decodeFromString<Map<String, String>>(error)
        } catch (e: Exception) {
            null
        }
        if (map != null) {
            if (map.containsKey("message")) {
                when (map["message"]) {
                    "rate limited" -> {
                        log.e("Rate limited: $error")
                        invalidateCookie()
                        Thread.sleep(retryCount * 10000L)
                    }
                    "Please wait a few minutes before you try again." -> {
                        log.e("Rate limited: $error")
                        invalidateCookie()
                        Thread.sleep(retryCount * 60000L)
                    }
                }
            }
        } else if (error.contains("Please wait a few minutes before you try again.")) {
            log.e("Rate limited: $error")
            invalidateCookie()
            Thread.sleep(retryCount * 60000L)
        }
    }

    private fun getCookie(): String {
        return cookieQueue.first()
    }

    private fun invalidateCookie() {
        Collections.rotate(cookieQueue, -1)
    }
}
