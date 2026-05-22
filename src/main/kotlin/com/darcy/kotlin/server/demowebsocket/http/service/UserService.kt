package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.table.User
import com.darcy.kotlin.server.demowebsocket.exception.code100.UserException
import com.darcy.kotlin.server.demowebsocket.http.repository.UserRepository
import com.darcy.kotlin.server.demowebsocket.utils.PasswordUtil
import com.darcy.kotlin.server.demowebsocket.utils.UUIdGenerator
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.UUID

@Service
class UserService @Autowired constructor(
    val userRepository: UserRepository,
    val passwordUtil: PasswordUtil
) {

    @Transactional
    fun createUser(userEntity: User): User {
        if (userEntity.username.isBlank() or userEntity.username.isEmpty()) {
            throw UserException.USER_NAME_EMPTY
        }
        if (userRepository.existsByUsername(userEntity.username)) {
            throw UserException.USER_NAME_ALREADY_EXIST
        }
        if (userRepository.existsByPhone(userEntity.phone)) {
            throw UserException.USER_PHONE_ALREADY_EXIST
        }
        if (userRepository.existsByEmail(userEntity.email)) {
            throw UserException.USER_EMAIL_ALREADY_EXIST
        }
        val realUser = userEntity.apply {
            userid = UUIdGenerator().nextUserId()
            passwordHash = passwordUtil.encode(passwordHash)
        }
        return userRepository.save(realUser)
    }

    fun queryUserById(id: Long): User {
        val user = userRepository.findById(id)
        if (user.isEmpty) {
            throw UserException.USER_NOT_EXIST
        }
        return user.get()
    }

    fun isUserExistByName(username: String): Boolean {
        return userRepository.existsByUsername(username)
    }

    fun isUserExistById(userId: Long): Boolean {
        return userRepository.existsById(userId)
    }

    fun queryUserByPhone(phone: String): User {
        return userRepository.findByPhone(phone) ?: throw UserException.USER_NOT_EXIST
    }

    fun queryUserByEmail(email: String): User {
        return userRepository.findByEmail(email) ?: throw UserException.USER_NOT_EXIST
    }

    @Transactional
    fun updateUser(user: User): User {
        return userRepository.save(user)
    }

    fun deleteUser(user: User) {
        userRepository.delete(user)
    }

    fun queryAllUsers(): List<User> {
        return userRepository.findAll()
    }

    fun queryJwtTokenVersionByUsername(username: String): Int {
        return userRepository.readTokenVersionByUsername(username) ?: 0
    }
}