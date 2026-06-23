package com.darcy.kotlin.server.demowebsocket.http.service

import com.darcy.kotlin.server.demowebsocket.domain.table.media.UploadFile
import com.darcy.kotlin.server.demowebsocket.http.repository.FileRepository
import com.darcy.kotlin.server.demowebsocket.http.repository.UploadFileRepository
import com.darcy.kotlin.server.demowebsocket.log.DarcyLogger
import com.darcy.kotlin.server.demowebsocket.log.logI
import com.darcy.kotlin.server.demowebsocket.log.logW
import org.springframework.transaction.annotation.Transactional
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

@Service
class UploadFileService @Autowired constructor(
    private val fileRepository: FileRepository,
    private val uploadFileRepository: UploadFileRepository
) {
    // @Value注解注入了文件保存路径uploadPath 配置在application.properties或application.yml文件中
    @Value("\${upload.path.image}")
    private val imageDir: String = ""

    fun saveImageFile(file: MultipartFile): File? {
        return fileRepository.saveImageFile(file, imageDir)
    }

    fun calculateFileHash(file: File): String {
        return fileRepository.calculateFileHash(file, "SHA-1")
    }

    @Transactional
    fun createItem(userId: Long, name: String, path: String, size: Long, type: Int, hash: String): UploadFile {
        val existItem = uploadFileRepository.findByHash(hash).firstOrNull()
        if (existItem != null) {
            logW("数据库已存在:$name 无需写入数据库")
            return existItem
        }
        logI("数据库不存在:$name 写入数据库.")
        return uploadFileRepository.save(
            UploadFile(
                userId = userId,
                name = name,
                path = path,
                size = size,
                type = UploadFile.FileType.fromCode(type),
                hash = hash
            )
        )
    }

    fun getItemId(id: Long): UploadFile? {
        return uploadFileRepository.findById(id).orElse(null)
    }

    fun updateItem(
        id: Long,
        userId: Long,
        name: String,
        path: String,
        size: Long,
        type: Int,
        hash: String
    ): UploadFile? {
        val file = getItemId(id)
        if (file != null) {
            file.userId = userId
            file.name = name
            file.path = path
            file.size = size
            file.type = UploadFile.FileType.fromCode(type)
            file.hash = hash
            return uploadFileRepository.save(file)
        }
        return null
    }

    fun deleteItem(id: Long): Boolean {
        val file = getItemId(id)
        if (file != null) {
            uploadFileRepository.delete(file)
            return true
        }
        return false
    }

    fun getFileResourceByName(fileName: String): Triple<String, String, Resource?> {
        val fileStoragePath: Path = Paths.get(imageDir)
        // 构造文件路径 normalize() 防止遍历攻击
        val filePath = fileStoragePath.resolve(fileName).normalize()
        val contentType: String = Files.probeContentType(filePath) ?: "application/octet-stream"
        val resource = UrlResource(filePath.toUri())
        val contentDisposition = "attachment; filename=\"${resource.filename}\""

        // 验证文件是否存在且可读
        if (!resource.exists() || !resource.isReadable) {
            return Triple(contentType, contentDisposition, null)
        }
        return Triple(contentType, contentDisposition, resource)
    }
}