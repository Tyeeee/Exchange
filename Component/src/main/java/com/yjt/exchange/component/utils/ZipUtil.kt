package com.hynet.heebit.components.utils

import com.google.common.collect.Lists
import net.lingala.zip4j.core.ZipFile
import net.lingala.zip4j.exception.ZipException
import net.lingala.zip4j.model.ZipParameters
import net.lingala.zip4j.util.Zip4jConstants
import java.io.*

class ZipUtil {

    companion object {

        val instance by lazy(LazyThreadSafetyMode.SYNCHRONIZED) {
            ZipUtil()
        }

    }

    /**
     * 单个文件压缩
     *
     * @param filePath   要压缩的文件路径
     * @param outZipPath 压缩后zip的保存路径
     */
    fun addFileToZip(filePath: String, outZipPath: String) {
        val filesPath = Lists.newArrayList<String>()
        filesPath.add(filePath)
        addFilesToZip(filesPath, outZipPath)
    }

    /**
     * 多个文件压缩
     *
     * @param filesPath  多个文件路径集合
     * @param outZipPath 压缩后的zip存放路径
     */
    fun addFilesToZip(filesPath: List<String>, outZipPath: String) {
        try {
            val zipFile = ZipFile(outZipPath)
            val filesToAdd = Lists.newArrayList<File>()
            for (i in filesPath.indices) {
                filesToAdd.add(File(filesPath[i]))
            }
            val parameters = ZipParameters()
            parameters.compressionMethod = Zip4jConstants.COMP_DEFLATE // set compression method to deflate compression
            parameters.compressionLevel = Zip4jConstants.DEFLATE_LEVEL_NORMAL
            zipFile.addFiles(filesToAdd, parameters)
        } catch (e: ZipException) {
            e.printStackTrace()
        }
    }

    /**
     * 单个文件加密压缩
     *
     * @param filePath   要压缩的文件路径
     * @param outZipPath 压缩后zip存储路径
     * @param password   密码
     */
    fun addFileWithAESEncryp(filePath: String, outZipPath: String, password: String) {
        val filesPath = Lists.newArrayList<String>()
        filesPath.add(filePath)
        addFilesWithAESEncryp(filesPath, outZipPath, password)
    }

    /**
     * 多个文件加密压缩
     *
     * @param filesPath  多个要压缩的文件路径集合
     * @param outZipPath 压缩后zip存储路径
     * @param password   密码
     */
    fun addFilesWithAESEncryp(filesPath: List<String>, outZipPath: String, password: String) {
        try {
            val zipFile = ZipFile(outZipPath)
            val filesToAdd = Lists.newArrayList<File>()
            for (i in filesPath.indices) {
                filesToAdd.add(File(filesPath[i]))
            }
            val parameters = ZipParameters()
            parameters.compressionMethod = Zip4jConstants.COMP_DEFLATE
            parameters.compressionLevel = Zip4jConstants.DEFLATE_LEVEL_NORMAL
            parameters.isEncryptFiles = true
            parameters.encryptionMethod = Zip4jConstants.ENC_METHOD_STANDARD
            //            parameters.setAesKeyStrength(Zip4jConstants.AES_STRENGTH_256);
            parameters.setPassword(password)
            zipFile.addFiles(filesToAdd, parameters)
        } catch (e: ZipException) {
            e.printStackTrace()
        }
    }

    /**
     * 通过流的方式进行压缩
     *
     * @param filePath    要压缩的文件路径
     * @param outZipPath  压缩后存储路径
     * @param zipfileName 要压缩的文件压缩后的文件名
     */
    fun addStreamToZip(filePath: String, outZipPath: String, zipfileName: String) {
        var inputStream: InputStream? = null
        try {
            val zipFile = ZipFile(outZipPath)
            val parameters = ZipParameters()
            parameters.compressionMethod = Zip4jConstants.COMP_DEFLATE
            parameters.fileNameInZip = zipfileName
            parameters.isSourceExternalStream = true
            inputStream = FileInputStream(filePath)
            zipFile.addStream(inputStream, parameters)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: ZipException) {
            e.printStackTrace()
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }

            }
        }
    }

    /**
     * 解压
     *
     * @param zipPath     要解压的Zip路径
     * @param outFilePath 解压到的目录
     */
    fun extractAllFiles(zipPath: String, outFilePath: String) {
        try {
            val zipFile = ZipFile(zipPath)
            zipFile.extractAll(outFilePath)
        } catch (e: ZipException) {
            e.printStackTrace()
        }

    }

    /**
     * 解压
     *
     * @param zipPath     要解压的Zip路径
     * @param outFilePath 解压到的目录
     */
    fun extractZipWithDecryp(zipPath: String, outFilePath: String, password: String) {
        try {
            val zipFile = ZipFile(zipPath)
            if (zipFile.isEncrypted) {
                zipFile.setPassword(password)
            }
            zipFile.extractAll(outFilePath)
        } catch (e: ZipException) {
            e.printStackTrace()
        }

    }
    
}