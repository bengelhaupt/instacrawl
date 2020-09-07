package com.bengelhaupt.instacrawl.model.base

import kotlinx.serialization.Serializable

@Serializable
data class Edge<T>(
    val edges: List<Node<T>>
) {
    @Serializable
    data class Node<T>(
        val node: T
    )
}
