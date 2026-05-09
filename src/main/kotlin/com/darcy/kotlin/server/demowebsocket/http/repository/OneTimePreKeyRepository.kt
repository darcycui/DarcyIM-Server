package com.darcy.kotlin.server.demowebsocket.http.repository

import com.darcy.kotlin.server.demowebsocket.domain.table.x3dh.OneTimePreKey
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface OneTimePreKeyRepository : JpaRepository<OneTimePreKey, Long> {
    fun findByUserId(userId: Long): List<OneTimePreKey>

    @Query("SELECT * FROM key_one_time_prekeys WHERE user_id = :userId AND " +
            "is_used = FALSE ORDER BY id ASC LIMIT 1")
    fun findFirstByUserIdAndIsUsedFalse(userId: Long): OneTimePreKey?
}