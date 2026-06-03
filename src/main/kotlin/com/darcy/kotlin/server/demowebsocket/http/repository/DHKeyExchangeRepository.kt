package com.darcy.kotlin.server.demowebsocket.http.repository

import com.darcy.kotlin.server.demowebsocket.domain.table.dh.DHKeyExchange
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface DHKeyExchangeRepository : JpaRepository<DHKeyExchange, Long> {

    fun findByUserId(userId:Long): DHKeyExchange?
}