package com.vortexos.filemanager

import java.io.File

object FileOperations {
    fun createFolder(parent: File, name: String): Boolean {
        return File(parent, name).mkdirs()
    }

    fun deleteFile(file: File): Boolean {
        return if (file.isDirectory) {
            file.deleteRecursively()
        } else {
            file.delete()
        }
    }

    fun renameFile(file: File, newName: String): Boolean {
        val dest = File(file.parent, newName)
        return file.renameTo(dest)
    }

    fun copyFile(source: File, destFolder: File): Boolean {
        return try {
            val dest = File(destFolder, source.name)
            source.copyRecursively(dest, overwrite = true)
            true
        } catch (e: Exception) {
            false
        }
    }
}
