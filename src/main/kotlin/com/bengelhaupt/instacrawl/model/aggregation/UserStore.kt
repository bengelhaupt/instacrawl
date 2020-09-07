package com.bengelhaupt.instacrawl.model.aggregation

import kotlinx.serialization.Serializable

@Serializable
class UserStore {

    internal val store: MutableMap<String, User> = mutableMapOf()

    fun getOrCreate(id: String, username: String): User {
        return store.getOrPut(
            id,
            { User(id, username, this) }
        )
    }

    fun get(id: String): User? {
        return store[id]
    }

    fun clear() {
        store.clear()
    }
}
