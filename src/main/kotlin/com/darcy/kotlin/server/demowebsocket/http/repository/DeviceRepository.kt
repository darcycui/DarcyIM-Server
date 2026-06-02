package com.darcy.kotlin.server.demowebsocket.http.repository

import com.darcy.kotlin.server.demowebsocket.domain.table.device.Device
import org.springframework.data.jpa.repository.JpaRepository

interface DeviceRepository: JpaRepository<Device, Long> {
    fun findByUserIdAndName(userId: Long, devicename: String): Device?
}