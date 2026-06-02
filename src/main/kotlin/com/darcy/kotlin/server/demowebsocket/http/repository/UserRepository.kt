package com.darcy.kotlin.server.demowebsocket.http.repository

import com.darcy.kotlin.server.demowebsocket.domain.table.user.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query

interface UserRepository : JpaRepository<User, Long> {

    // Spring Data JPA 支持方法名派生查询，它会根据方法名自动生成 JPQL 查询
    // Mysql默认大小写不敏感 这里使用BINARY 精确检查大小写 (已经通过迁移数据库解决)
//    @Query("select u from User u where function('BINARY', u.username) = :userName")
//    @Query("select u from User u where  u.username = :userName")
    fun findByUsername(userName: String): User?

    fun findByPhone(phone: String): User?

    fun findByEmail(email: String): User?

    fun existsByUsername(username: String): Boolean

    fun existsByPhone(phone: String): Boolean

    fun existsByEmail(email: String): Boolean

    @Query("SELECT u.jwtTokenVersion FROM User u WHERE u.id = :id")
    fun readTokenVersionById(id: Long): Int?

    @Query("SELECT u.jwtTokenVersion FROM User u WHERE u.phone = :phone")
    fun readTokenVersionByPhone(phone: String): Int?

    @Query("SELECT u.jwtTokenVersion FROM User u WHERE u.username = :username")
    fun readTokenVersionByUsername(username: String): Int?
}