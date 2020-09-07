package com.bengelhaupt.instacrawl.model.base

import kotlinx.serialization.Serializable

@Serializable
data class ShortcodeMediaWrapper<T>(
    val shortcode_media: T
)
