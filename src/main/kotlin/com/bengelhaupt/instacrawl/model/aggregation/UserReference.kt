package com.bengelhaupt.instacrawl.model.aggregation

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable(with = UserReferenceSerializer::class)
data class UserReference(
    var id: String,
    var username: String,
    @Transient var userStore: UserStore = UserStore()
) {
    @Transient
    val user = userStore.getOrCreate(id, username)
}
