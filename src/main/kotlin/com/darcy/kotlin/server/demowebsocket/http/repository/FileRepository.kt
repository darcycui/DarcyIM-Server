package com.darcy.kotlin.server.demowebsocket.http.repository

import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.log.logE
import com.darcy.kotlin.server.demowebsocket.log.logI
import com.darcy.kotlin.server.demowebsocket.log.logW
import org.springframework.stereotype.Repository
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest

@Repository
class FileRepository {

    fun saveImageFile(file: MultipartFile, uploadPath: String): File? {
        val fileName = file.originalFilename
        try {
            // 获取文件名
            // 指定保存路径
            val filePath = "$uploadPath/$fileName"
            // 保存文件到本地
            val saveFile = File(filePath)
            if (saveFile.exists()) {
                logW("文件已存在 无需保存！ $fileName ")
                return saveFile
            }
            logI("文件保存成功: $fileName")
            if (!saveFile.parentFile.exists()) {
                saveFile.parentFile.mkdirs()
            }
            file.transferTo(saveFile)
            return saveFile
        } catch (e: IOException) {
            e.printStackTrace()
            logE("文件保存失败: $fileName")
        }
        return null
    }

    fun calculateFileHash(file: File, algorithm: String = "SHA-1"): String {
        // 计算文件的哈希值 流式读取文件
        val digest = MessageDigest.getInstance(algorithm)
        FileInputStream(file).use { inputStream ->
            val buffer = ByteArray(8192)
            var bytesRead: Int
            while (inputStream.read(buffer).also { bytesRead = it } > 0) {
                digest.update(buffer, 0, bytesRead)
            }
        }
        return digest.digest().joinToString(separator = "") { "%02x".format(it) }
    }
}