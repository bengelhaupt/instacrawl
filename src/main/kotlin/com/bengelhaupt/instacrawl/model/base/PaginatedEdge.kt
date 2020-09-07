package com.bengelhaupt.instacrawl.model.base

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedEdge<T>(
    val count: Int,
    val page_info: PageInfo,
    val edges: List<Node<T>>
) {
    @Serializable
    data class Node<T>(
        val node: T
    )

    @Serializable
    data class PageInfo(
        val has_next_page: Boolean,
        val end_cursor: String?
    )
}
