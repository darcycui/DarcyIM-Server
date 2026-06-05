package com.darcy.kotlin.server.demowebsocket.http.controller

import com.darcy.kotlin.server.demowebsocket.api.IUploadFileApi
import com.darcy.kotlin.server.demowebsocket.crypto.annotation.Encrypted
import com.darcy.kotlin.server.demowebsocket.domain.ResultEntity
import com.darcy.kotlin.server.demowebsocket.domain.dto.media.toDTO
import com.darcy.kotlin.server.demowebsocket.domain.table.media.UploadFile
import com.darcy.kotlin.server.demowebsocket.domain.table.media.toFileType
import com.darcy.kotlin.server.demowebsocket.exception.BaseException
import com.darcy.kotlin.server.demowebsocket.exception.code300.DBException
import com.darcy.kotlin.server.demowebsocket.exception.code200.FileException
import com.darcy.kotlin.server.demowebsocket.exception.toJsonString
import com.darcy.kotlin.server.demowebsocket.http.service.UploadFileService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile


@RestController
@Encrypted
class UploadFileController @Autowired constructor(
    val uploadFileService: UploadFileService
) : IUploadFileApi {

    override fun uploadImage(
        @RequestParam("userId") userId: Long,
        @RequestParam("file") file: MultipartFile
    ): String {
        try {
            val savedFile = try {
                uploadFileService.saveImageFile(file)
            } catch (e: Exception) {
                e.printStackTrace()
                return FileException.FILE_SAVE_FAILED.toJsonString()
            }
            if (savedFile == null) {
                return FileException.FILE_SAVE_FAILED.toJsonString()
            }
            val fileHash = uploadFileService.calculateFileHash(savedFile)
            val fileEntity = try {
                uploadFileService.createItem(
                    userId,
                    savedFile.name,
                    savedFile.absolutePath,
                    savedFile.length(),
                    file.contentType?.toFileType()?.toCode() ?: UploadFile.FileType.OTHER.toCode(),
                    fileHash
                )
            } catch (e: Exception) {
                e.printStackTrace()
                return DBException.DB_SAVE_FAILURE.toJsonString()
            }
            return ResultEntity.success(fileEntity.toDTO()).toJsonString()
        } catch (e: Exception) {
            e.printStackTrace()
            return BaseException.UNKNOWN_EXCEPTION.toJsonString()
        }
    }

    override fun downloadImage(@PathVariable("fileName") fileName: String): ResponseEntity<Resource> {
        // 从服务中获取文件 Resource
        val triple =
            uploadFileService.getFileResourceByName(fileName)
        val resource = triple.third ?: return ResponseEntity.notFound().build()
        // 设置响应头
        val contentType = triple.first
        val contentDisposition = triple.second

        return ResponseEntity.ok()
            .contentType(MediaType.parseMediaType(contentType))
            .header(HttpHeaders.CONTENT_DISPOSITION, contentDisposition)
            .body(resource)
    }

    override fun getAllImages(): String {
        TODO("Not yet implemented")
    }
}
