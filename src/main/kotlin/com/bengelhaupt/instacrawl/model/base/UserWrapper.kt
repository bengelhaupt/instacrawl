package com.bengelhaupt.instacrawl.model.base

import kotlinx.serialization.Serializable

@Serializable
data class UserWrapper<T>(
    val user: T
)
