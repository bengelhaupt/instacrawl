package com.bengelhaupt.instacrawl.model.base

import kotlinx.serialization.Serializable

@Serializable
data class DataWrapper<T>(
    val data: T
)
