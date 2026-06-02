package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.table.device.Device
import com.darcy.kotlin.server.demowebsocket.http.repository.DeviceRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class DeviceService @Autowired constructor(
    private val deviceRepository: DeviceRepository,
    private val userService: UserService
) {
    fun createDeviceIfNeeded(userId: Long, deviceName: String): Device {
        val existDevice = queryByUserIdAndDeviceName(userId, deviceName)
        if (existDevice != null) {
            return existDevice
        }
        val user = userService.queryUserById(userId)
        val device = Device(
            user = user,
            name = deviceName
        )
        return deviceRepository.save(device)
    }

    fun queryByUserIdAndDeviceName(userId: Long, deviceName: String): Device? {
        val device = deviceRepository.findByUserIdAndName(userId, deviceName)
        return device
    }
}