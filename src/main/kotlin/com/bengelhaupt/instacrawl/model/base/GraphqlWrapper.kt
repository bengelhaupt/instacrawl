package com.bengelhaupt.instacrawl.model.base

import kotlinx.serialization.Serializable

@Serializable
data class GraphqlWrapper<T>(
    val graphql: T
)
