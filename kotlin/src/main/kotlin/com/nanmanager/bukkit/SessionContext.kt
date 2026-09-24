package com.nanmanager.bukkit

import com.fasterxml.jackson.annotation.JsonProperty

/** Server session fencing headers issued when a server connection is opened. */
data class SessionContext(
    @JsonProperty("session_id")
    val id: String,
    @JsonProperty("session_epoch")
    val epoch: Long
) {
    init {
        require(id.isNotBlank()) { "session id must not be blank" }
        require(id.length in 32..64) { "session id length must be between 32 and 64 characters" }
        require(epoch > 0) { "session epoch must be greater than zero" }
    }
}
