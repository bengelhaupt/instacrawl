package com.bengelhaupt.instacrawl

import com.bengelhaupt.instacrawl.api.API
import com.bengelhaupt.instacrawl.model.aggregation.UserReference
import com.bengelhaupt.instacrawl.model.aggregation.UserStore
import com.bengelhaupt.instacrawl.util.Log
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Crawls users recursively by their followers and followings.
 */
fun main() {
    val api = API(
        Json.decodeFromString(API::class.java.getResource("/apikeys.json").readText())
    )
    api.log.level = Log.Level.ERROR

    val storeFile = File("./store.json")
    val stateFile = File("./crawlstate.json")

    val userStore = if (storeFile.exists() && storeFile.length() != 0L) {
        Json.decodeFromString(storeFile.readText())
    } else {
        UserStore()
    }

    val crawlState = if (stateFile.exists() && stateFile.length() != 0L) {
        Json.decodeFromString<MutableList<String>>(stateFile.readText())
    } else {
        mutableListOf()
    }

    val localLog = Log(Log.Level.INFO)

    fun crawl(entity: UserReference, depth: Int, includeAuth: Boolean) {
        val id = entity.id
        val username = entity.username
        if (depth > 0) {
            val crawledUser = if (!crawlState.contains(id)) {
                val user = userStore.getOrCreate(id, username).apply {
                    localLog.i("$username: start fetch profile")
                    updateProfile(api.getProfile(username, includeAuth))
                    localLog.i("$username: profile fetched")

                    localLog.i("$username: start fetch followers")
                    updateFollowers(api.getFollowersFor(id, 1000))
                    localLog.i("$username: followers fetched")
                    localLog.i("$username: start fetch followings")
                    updateFollowings(api.getFollowingsFor(id, 1000))
                    localLog.i("$username: followings fetched")

                    localLog.i("$username: start fetch posts")
                    updatePostings(api.getPostsFor(id, 160, false))
                    localLog.i("$username: posts fetched")

                    postings?.forEach {
                        localLog.i("$username: start fetch post ${it.shortCode}")
                        it.updatePosting(api.getPost(it.shortCode, false))
                        it.updateLikes(api.getLikesForPost(it.shortCode, 1000, false))
                        localLog.i("$username: post ${it.shortCode} fetched")
                    }
                }

                crawlState.add(id)

                localLog.i("$username: start writing user store")
                storeFile.writeText(
                    Json.encodeToString(userStore)
                )
                localLog.i("$username: user store written")
                localLog.i("$username: start writing crawl state")
                stateFile.writeText(
                    Json.encodeToString(crawlState)
                )
                localLog.i("$username: crawl state written")

                user
            } else {
                localLog.i("$username: already crawled")
                userStore.get(id)!!
            }

            ((crawledUser.followers ?: listOf()) + (crawledUser.followings ?: listOf()))
                .distinct()
                .forEach {
                    localLog.i("Next user: ${it.username}")
                    crawl(it, depth - 1, (it.user.isPrivate ?: false) && includeAuth)
                }
        } else {
            localLog.i("$username: maximum depth reached")
        }
    }

    val userToCrawl = UserReference(api.getProfile("YOUR_USERNAME").id, "YOUR_USERNAME")
    crawl(
        userToCrawl, 2, false
    )
}
