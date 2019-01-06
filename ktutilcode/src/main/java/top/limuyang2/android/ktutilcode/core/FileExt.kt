package top.limuyang2.android.ktutilcode.core

import java.io.File

/**
 * file path to File
 * @receiver String?
 * @return File?
 */
fun String?.toFile(): File? {
    return if (this.isNullOrEmpty()) null else File(this)
}

fun String?.isFileExists(): Boolean {
    return this.toFile().isFileExists()
}

fun File?.isFileExists(): Boolean {
    return this != null && this.exists()
}

/**
 * Rename the file.
 *
 * @param newName The new name of file.
 * @return `true`: success<br></br>`false`: fail
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

fun String?.rename(newName: String): Boolean {
    return this.toFile().rename(newName)
}

/**
 * Return whether it is a directory.
 *
 * @return `true`: yes<br></br>`false`: no
 */
inline val File?.isDir: Boolean
    get() {
        return this != null && this.exists() && this.isDirectory
    }

inline val String?.isDir: Boolean
    get() {
        return this.toFile().isDir
    }

