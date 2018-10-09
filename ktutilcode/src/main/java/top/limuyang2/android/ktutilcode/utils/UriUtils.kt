package top.limuyang2.android.ktutilcode.utils

import android.content.ContentResolver
import android.content.CursorLoader
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.support.v4.content.FileProvider

import java.io.File

/**
 * <pre>
 * author: Blankj
 * blog  : http://blankj.com
 * time  : 2018/04/20
 * desc  : URI 相关
</pre> *
 */

/**
 * File to uri.
 *
 * @return uri
 */
fun File.toUri(): Uri {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        val authority = app().packageName + ".ktutilcode.provider"
        FileProvider.getUriForFile(app(), authority, this)
    } else {
        Uri.fromFile(this)
    }
}

/**
 * Uri to file.
 *
 * @param columnName The name of the target column.
 *
 * e.g. [MediaStore.Images.Media.DATA]
 * @return file
 */
fun Uri.toFile(columnName: String): File {
    if (ContentResolver.SCHEME_FILE == this.scheme) {
        return File(this.path!!)
    }
    val cl = CursorLoader(app())
    cl.uri = this
    cl.projection = arrayOf(columnName)
    var cursor: Cursor? = null
    try {
        cursor = cl.loadInBackground()
        val columnIndex = cursor!!.getColumnIndexOrThrow(columnName)
        cursor.moveToFirst()
        return File(cursor.getString(columnIndex))
    } finally {
        cursor?.close()
    }
}
