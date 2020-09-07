package com.bengelhaupt.instacrawl.analytics

import com.bengelhaupt.instacrawl.model.aggregation.User
import org.jgrapht.graph.DefaultDirectedGraph
import org.jgrapht.graph.DefaultEdge

class UserFollowingGraph(users: List<User>) : DefaultDirectedGraph<User, DefaultEdge>(
    DefaultEdge::class.java
) {
    init {
        users.forEach { user ->
            addVertex(user)
        }

        users.forEach { user ->
            user.followings?.forEach { reference ->
                users.find { it.id == reference.id }?.let {
                    addEdge(
                        user, it
                    )
                }
            }
            user.followers?.forEach { reference ->
                users.find { it.id == reference.id }?.let {
                    addEdge(
                        it, user
                    )
                }
            }
        }
    }
}
