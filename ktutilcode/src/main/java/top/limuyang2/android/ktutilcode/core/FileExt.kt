package top.limuyang2.android.ktutilcode.core

import java.io.*

typealias ExtFileFilter = (file: File) -> Boolean

/**
 * 路径字符串转为File
 * @receiver String?
 * @return File?
 */
fun String.toFile(): File {
    return File(this)
}

/**
 * 文件是否存在
 * @receiver File?
 * @return Boolean
 */
fun File?.isFileExists(): Boolean {
    return this != null && this.exists()
}

/**
 * 重命名文件
 *
 * @param newName 新文件名
 * @return
 */
fun File?.rename(newName: String): Boolean {
    // file is null and doesn't exist then return false
    if (this == null || !this.exists()) return false
    // the new name is space then return false
    if (newName.isBlank()) return false
    // the new name equals old name then return true
    if (newName == this.name) return true
    val newFile = File(this.parent + File.separator + newName)
    // the new name of file exists then return false
    return !newFile.exists() && this.renameTo(newFile)
}

/**
 * 判断是否是文件夹
 */
inline val File?.isDir: Boolean
    get() {
        return this != null && this.exists() && this.isDirectory
    }

/**
 * 新建文件夹（如果目录存在，则跳过创建，直接返回true）
 * @receiver File?
 * @return Boolean
 */
fun File?.createDir(): Boolean {
    return this != null && if (this.exists()) this.isDirectory else this.mkdirs()
}

/**
 * 新建文件（如果文件存在，则跳过创建）
 * @receiver File?
 * @param isDeleteOldFile 如果文件存在，是否删除。'false': 跳过，'true': 删除后重新创建
 * @return Boolean
 */
fun File?.createFile(isDeleteOldFile: Boolean = false): Boolean {
    if (this == null) return false
    if (this.exists()) {
        if (isDeleteOldFile) {
            if (!this.delete()) return false
        } else {
            return this.isFile
        }
    }
    if (!this.parentFile.createDir()) return false
    return try {
        this.createNewFile()
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

/**
 * 删除文件
 */
fun File?.deleteFile(): Boolean {
    return this != null && (!this.exists() || this.isFile && this.delete())
}

/**
 * 删除目录
 */
fun File?.deleteDir(): Boolean {
    if (this == null) return false
    // dir doesn't exist then return true
    if (!this.exists()) return true
    // dir isn't a directory then return false
    if (!this.isDirectory) return false
    val files = this.listFiles()
    if (!files.isNullOrEmpty()) {
        for (file in files) {
            if (file.isFile) {
                if (!file.delete()) return false
            } else if (file.isDirectory) {
                if (!file.deleteDir()) return false
            }
        }
    }
    return this.delete()
}

/**
 * 删除文件 或 目录
 * 与系统的 delete() 不同，系统的目录删除方法，该目录必须为空才能被删除。
 */
inline val File?.delete: Boolean
    get() {
        if (this == null) return false
        return if (this.isDirectory) {
            this.deleteDir()
        } else this.deleteFile()
    }

/**
 * 删除目录下所有东西（除去过滤的文件）
 *
 * @param extFileFilter  过滤的文件
 * @return Boolean `true`: success, `false`: fail
 */
fun File?.deleteAllInDir(extFileFilter: ExtFileFilter? = null): Boolean {
    if (this == null) return false
    // dir doesn't exist then return true
    if (!this.exists()) return true
    // dir isn't a directory then return false
    if (!this.isDirectory) return false
    val files = this.listFiles()
    if (!files.isNullOrEmpty()) {
        for (file in files) {
            if (extFileFilter == null || extFileFilter.invoke(this)) {
                if (!file.delete)
                    return false
            }
        }
    }
    return true
}

/**
 * 复制 或 移动此文件 到 目标文件
 * @receiver File?
 * @param toFile 目标文件
 * @param isMove Boolean
 * @param replaceExtFileFilter 文件过滤器。当目标文件存在时，是否进行替换，'true'替换; 'false' 不替换
 * @return Boolean
 */
private fun File?.copyOrMoveFile(
        toFile: File,
        isMove: Boolean,
        replaceExtFileFilter: ExtFileFilter? = null): Boolean {
    if (this == null) return false
    // srcFile equals destFile then return false
    if (this == toFile) return false
    // srcFile doesn't exist or isn't a file then return false
    if (!this.exists() || !this.isFile) return false
    if (toFile.exists()) {
        if (replaceExtFileFilter == null || replaceExtFileFilter.invoke(toFile)) {// require delete the old file
            if (!toFile.delete()) {// unsuccessfully delete then return false
                return false
            }
        } else {
            return true
        }
    }
    if (!toFile.parentFile.createDir()) return false
    return try {
        writeFileFromIS(FileOutputStream(toFile), FileInputStream(this))
                && !(isMove && !this.deleteFile())
    } catch (e: FileNotFoundException) {
        e.printStackTrace()
        false
    }
}

/**
 * 复制 或 移动此文件夹 到 目标文件夹
 * @param toDir 需要复制到的目标文件夹
 * @param isMove Boolean
 * @param replaceExtFileFilter 文件夹过滤器。当目标文件夹存在时，是否进行替换，'true'替换; 'false' 不替换
 * @return Boolean
 */
private fun File?.copyOrMoveDir(
        toDir: File,
        isMove: Boolean,
        replaceExtFileFilter: ExtFileFilter? = null): Boolean {
    if (this == null) return false
    // destDir's path locate in srcDir's path then return false
    val srcPath = this.path + File.separator
    val destPath = toDir.path + File.separator
    if (destPath.contains(srcPath)) return false
    if (!this.exists() || !this.isDirectory) return false
    if (toDir.exists()) {
        if (replaceExtFileFilter == null || replaceExtFileFilter.invoke(toDir)) {// require delete the old directory
            if (!toDir.deleteAllInDir()) {// unsuccessfully delete then return false
                return false
            }
        } else {
            return true
        }
    }
    if (!toDir.createDir()) return false
    val files = this.listFiles()
    for (file in files) {
        val oneDestFile = File(destPath + file.name)
        if (file.isFile) {
            if (!file.copyOrMoveFile(oneDestFile, isMove, replaceExtFileFilter)) return false
        } else if (file.isDirectory) {
            if (!file.copyOrMoveDir(oneDestFile, isMove, replaceExtFileFilter)) return false
        }
    }
    return !isMove || this.deleteDir()
}

/**
 * 复制文件夹（需要使用文件夹过滤器的情况下）
 * @receiver File? 原文件夹
 * @param toDir 目标文件夹
 * @param replaceExtFileFilter 过滤器。当目标文件夹存在时，是否进行替换，'true'替换; 'false' 不替换
 * @return Boolean
 */
fun File?.copyDirTo(toDir: File, replaceExtFileFilter: ExtFileFilter? = null): Boolean {
    return copyOrMoveDir(toDir, false, replaceExtFileFilter)
}

/**
 * 复制文件夹
 * code Example :
 *      val oldFileDir = ……
 *      val newFileDir = ……
 *      oldFileDir copyDirTo newFileDir
 *
 * @receiver File? 原文件夹
 * @param toDir File 目标文件夹
 * @return Boolean
 */
infix fun File?.copyDirTo(toDir: File): Boolean {
    return copyOrMoveDir(toDir, false)
}

/**
 * 复制文件（需要使用文件夹过滤器的情况下）
 * @receiver File?
 * @param toFile File
 * @param replaceExtFileFilter ExtFileFilter?
 * @return Boolean
 */
fun File?.copyFileTo(toFile: File, replaceExtFileFilter: ExtFileFilter? = null): Boolean {
    return copyOrMoveFile(toFile, false, replaceExtFileFilter)
}

/**
 * 复制文件
 * code Example :
 *      val oldFile = ……
 *      val newFile = ……
 *      oldFile copyFileTo newFile
 *
 * @receiver File? 原文件
 * @param toFile File 目标文件
 * @return Boolean
 */
infix fun File.copyFileTo(toFile: File): Boolean {
    return copyOrMoveFile(toFile, false)
}

/**
 * 移动文件夹（需要使用文件夹过滤器的情况下）
 * @receiver File? 原文件夹
 * @param toDir 目标文件夹
 * @param replaceExtFileFilter 过滤器。当目标文件夹存在时，是否进行替换，'true'替换; 'false' 不替换
 * @return Boolean
 */
fun File?.moveDirTo(toDir: File, replaceExtFileFilter: ExtFileFilter? = null): Boolean {
    return copyOrMoveDir(toDir, true, replaceExtFileFilter)
}

/**
 * 移动文件夹
 * code Example :
 *      val oldFileDir = ……
 *      val newFileDir = ……
 *      oldFileDir moveDirTo newFileDir
 *
 * @receiver File? 原文件夹
 * @param toDir File 目标文件夹
 * @return Boolean
 */
infix fun File?.moveDirTo(toDir: File): Boolean {
    return copyOrMoveDir(toDir, true)
}

/**
 * 移动文件（需要使用文件夹过滤器的情况下）
 * @receiver File?
 * @param toFile File
 * @param replaceExtFileFilter ExtFileFilter?
 * @return Boolean
 */
fun File?.moveFileTo(toFile: File, replaceExtFileFilter: ExtFileFilter? = null): Boolean {
    return copyOrMoveFile(toFile, true, replaceExtFileFilter)
}

/**
 * 移动文件
 * code Example :
 *      val oldFile = ……
 *      val newFile = ……
 *      oldFile moveFileTo newFile
 *
 * @receiver File? 原文件
 * @param toFile File 目标文件
 * @return Boolean
 */
infix fun File.moveFileTo(toFile: File): Boolean {
    return copyOrMoveFile(toFile, true)
}

private fun writeFileFromIS(os: OutputStream,
                            `is`: InputStream): Boolean {
    try {
        `is`.copyTo(os)
        return true
    } catch (e: IOException) {
        e.printStackTrace()
        return false
    } finally {
        try {
            `is`.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            os.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}