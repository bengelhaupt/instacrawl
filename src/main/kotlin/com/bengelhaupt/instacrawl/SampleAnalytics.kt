package com.bengelhaupt.instacrawl

import com.bengelhaupt.instacrawl.analytics.UserFollowingGraph
import com.bengelhaupt.instacrawl.model.aggregation.UserStore
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.net.URL
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

/**
 * Finds follower cliques, searches for certain posts and downloads all images of a given user.
 */
fun main() {
    val storeFile = File("./store.json")

    val userStore = if (storeFile.exists() && storeFile.length() != 0L) {
        Json.decodeFromString(storeFile.readText())
    } else {
        UserStore()
    }

    findCliques(userStore)
    searchPosts(userStore, listOf("nature", "@YOUR_USERNAME"))
    downloadImages(userStore, "YOUR_USERNAME")
}

fun findCliques(userStore: UserStore) {
    val followings = UserFollowingGraph(userStore.store.values.toList())

    val cliqueFinder = org.jgrapht.alg.clique.BronKerboschCliqueFinder(followings)
    cliqueFinder.maximumIterator().forEach {
        println(
            it.toList().map { user -> user.username }
        )
    }
}

fun searchPosts(userStore: UserStore, filters: List<String>) {
    println(
        Json.encodeToString(
            userStore.store.values
                .map {
                    it.postings
                }
                .reduce { acc, list ->
                    acc?.toMutableList()?.apply { addAll(list ?: listOf()) }
                }
                ?.filter {
                    filters.all { search ->
                        it.accessibilityCaption?.toLowerCase()?.contains(search) ?: false
                                || it.slides?.any {
                            it.accessibilityCaption?.toLowerCase()?.contains(search) ?: false
                        } ?: false
                                || it.caption?.toLowerCase()?.contains(search) ?: false
                                || it.comments?.any { it.value.toLowerCase().contains(search) } ?: false
                                || it.location?.toLowerCase()?.contains(search) ?: false
                                || SimpleDateFormat("EEEE dd MMMM yyyy").format(Date.from(Instant.ofEpochSecond(it.takenAtTimestamp)))
                            .toLowerCase().contains(search)
                    }
                }
                ?.map {
                    it.displayUrl
                }
        )
    )
}

fun downloadImages(userStore: UserStore, username: String) {
    userStore.store.values.filter { it.username == username }.forEach { user ->
        val dir = File("./images/${user.username}")
        val postingUrls = user.postings?.map { posting -> posting.displayUrl to posting.shortCode } ?: listOf()
        val slideUrls =
            user.postings?.flatMap { posting ->
                posting.slides?.map { slide -> slide.displayUrl to slide.shortCode } ?: listOf()
            } ?: listOf()
        (postingUrls + slideUrls).distinctBy { it.first }.forEach {
            dir.mkdirs()
            File(dir, "${it.second}.jpg").apply {
                writeBytes(
                    URL(it.first).readBytes()
                )
            }
        }
    }
}
