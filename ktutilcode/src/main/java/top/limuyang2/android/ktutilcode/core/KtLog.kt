package top.limuyang2.android.ktutilcode.core

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.support.annotation.IntRange
import android.support.annotation.RequiresApi
import android.support.v4.util.SimpleArrayMap
import android.util.Log
import com.google.gson.GsonBuilder
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import top.limuyang2.android.ktutilcode.KtUtilCode
import java.io.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type
import java.net.UnknownHostException
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import javax.xml.transform.OutputKeys
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * @author: limuyang
 * @date: 2019/1/30
 * @Description: 日志相关
 */

private typealias IFormatter = (t: Any) -> String

enum class LogType {
    V {
        override fun value(): Int = Log.VERBOSE
    },
    D {
        override fun value(): Int = Log.DEBUG
    },
    I {
        override fun value(): Int = Log.INFO
    },
    W {
        override fun value(): Int = Log.WARN
    },
    E {
        override fun value(): Int = Log.ERROR
    },
    A {
        override fun value(): Int = Log.ASSERT
    };

    abstract fun value(): Int
}

private const val LOG_FILE = 0x10
private const val LOG_JSON = 0x20
private const val LOG_XML = 0x30

private val FILE_SEP = System.getProperty("file.separator")
private val LINE_SEP = System.getProperty("line.separator")
private const val TOP_CORNER = "┌"
private const val MIDDLE_CORNER = "├"
private const val LEFT_BORDER = "│ "
private const val BOTTOM_CORNER = "└"
private const val SIDE_DIVIDER = "────────────────────────────────────────────────────────"
private const val MIDDLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"
private const val TOP_BORDER = TOP_CORNER + SIDE_DIVIDER + SIDE_DIVIDER
private const val MIDDLE_BORDER = MIDDLE_CORNER + MIDDLE_DIVIDER + MIDDLE_DIVIDER
private const val BOTTOM_BORDER = BOTTOM_CORNER + SIDE_DIVIDER + SIDE_DIVIDER
private const val MAX_LEN = 3000
private const val NOTHING = "log nothing"
private const val NULL = "null"
private const val ARGS = "args"
private const val PLACEHOLDER = " "
private val config = LogConfig()
private val gson = GsonBuilder()
        .setPrettyPrinting().serializeNulls().create()

private val SDF_THREAD_LOCAL = ThreadLocal<SimpleDateFormat>()

private val EXECUTOR = Executors.newSingleThreadExecutor()

private val I_FORMATTER_MAP = SimpleArrayMap<Class<*>, IFormatter>()

private val typeArray = charArrayOf('V', 'D', 'I', 'W', 'E', 'A')

/**
 * 日志配置类
 * @property dir String?  文件存储目录
 * @property filePrefix String  文件前缀
 * @property logSwitch Boolean  总开关
 * @property log2ConsoleSwitch Boolean  控制台开关
 * @property globalTag String  全局 tag
 * @property logHeadSwitch Boolean  头部信息开关
 * @property log2FileSwitch Boolean  文件开关
 * @property logBorderSwitch Boolean  边框开关
 * @property singleTagSwitch Boolean  单一 tag 开关
 * @property consoleFilter LogType  控制台过滤器
 * @property fileFilter LogType  文件过滤器
 * @property stackDeep Int  栈深度
 * @property stackOffset Int  栈偏移
 * @property saveDays Int  可保留天数（设置值必须大于1天）
 */
class LogConfig {
    // The default storage directory of log.
    internal var defaultDir: String? = null
        private set

    // The storage directory of log.
    var dir: String? = null
        set(value) {
            if (value.isNullOrBlank()) {
                field = null
            } else {
                field = if (value.endsWith(FILE_SEP)) value else value + FILE_SEP
            }
        }
        get() = if (field == null) defaultDir else field

    // The file prefix of log.
    var filePrefix = "util"
        set(value) {
            if (value.isNotBlank()) {
                field = value
            }
        }

    var logSwitch = true  // The switch of log.

    var log2ConsoleSwitch = true // The logcat's switch of log.

    internal var tagIsSpace = true  // The global tag is space.

    var globalTag = ""  // The global tag of log.
        set(value) {
            if (value.isBlank()) {
                field = ""
                tagIsSpace = true
            } else {
                field = value
                tagIsSpace = false
            }
        }

    var logHeadSwitch = true  // The head's switch of log.

    var log2FileSwitch = false // The file's switch of log.
    var logBorderSwitch = true // The border's switch of log.
    var singleTagSwitch = true  // The single tag of log.
    var consoleFilter = LogType.V     // The console's filter of log.
    var fileFilter = LogType.V     // The file's filter of log.
    @IntRange(from = 1)
    var stackDeep = 1     // The stack's deep of log.
    @IntRange(from = 0)
    var stackOffset = 0     // The stack's offset of log.
    var saveDays = -1    // The save days of log.

    init {
        if (defaultDir.isNullOrBlank()) {
            defaultDir = if (Environment.MEDIA_MOUNTED == Environment.getExternalStorageState() && KtUtilCode.app.externalCacheDir != null)
                KtUtilCode.app.externalCacheDir.path + FILE_SEP + "log" + FILE_SEP
            else {
                KtUtilCode.app.cacheDir.path + FILE_SEP + "log" + FILE_SEP
            }
        }
    }

    fun addFormatter(iFormatter: IFormatter): LogConfig {
        I_FORMATTER_MAP.put(getTypeClassFromParadigm(iFormatter), iFormatter)
        return this
    }

    override fun toString(): String {
        return ("switch: " + logSwitch
                + LINE_SEP + "console: " + log2ConsoleSwitch
                + LINE_SEP + "tag: " + globalTag
                + LINE_SEP + "head: " + logHeadSwitch
                + LINE_SEP + "file: " + log2FileSwitch
                + LINE_SEP + "dir: " + dir
                + LINE_SEP + "filePrefix: " + filePrefix
                + LINE_SEP + "border: " + logBorderSwitch
                + LINE_SEP + "singleTag: " + singleTagSwitch
                + LINE_SEP + "consoleFilter: " + typeArray[consoleFilter.value() - LogType.V.value()]
                + LINE_SEP + "fileFilter: " + typeArray[fileFilter.value() - LogType.V.value()]
                + LINE_SEP + "stackDeep: " + stackDeep
                + LINE_SEP + "stackOffset: " + stackOffset
                + LINE_SEP + "saveDays: " + saveDays
                + LINE_SEP + "formatter: " + I_FORMATTER_MAP)
    }
}

private class TagHead internal constructor(internal var tag: String, internal var consoleHead: Array<String?>?, internal var fileHead: String)

private object LogFormatter {

    internal fun object2String(`object`: Any): String {
        return if (`object`.javaClass.isArray) array2String(`object`) else (`object` as? Throwable)?.let { throwable2String(it) }
                ?: ((`object` as? Bundle)?.let { bundle2String(it) }
                        ?: ((`object` as? Intent)?.let { intent2String(it) }
                                ?: `object`.toString()))
    }

    internal fun object2Json(`object`: Any): String {
        if (`object` is CharSequence) {
            return formatJson(`object`.toString())
        }
        return try {
            gson.toJson(`object`)
        } catch (t: Throwable) {
            `object`.toString()
        }
    }

    internal fun formatXml(x: String): String {
        var xml = x
        try {
            val xmlInput = StreamSource(StringReader(xml))
            val xmlOutput = StreamResult(StringWriter())
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(xmlInput, xmlOutput)
            xml = xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">" + LINE_SEP!!)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return xml
    }

    private fun throwable2String(e: Throwable): String {
        var t: Throwable? = e
        while (t != null) {
            if (t is UnknownHostException) {
                return ""
            }
            t = t.cause
        }
        val sw = StringWriter()
        val pw = PrintWriter(sw)
        e.printStackTrace(pw)
        var cause: Throwable? = e.cause
        while (cause != null) {
            cause.printStackTrace(pw)
            cause = cause.cause
        }
        pw.flush()
        return sw.toString()
    }

    private fun bundle2String(bundle: Bundle): String {
        val iterator = bundle.keySet().iterator()
        if (!iterator.hasNext()) {
            return "Bundle {}"
        }
        val sb = StringBuilder(128)
        sb.append("Bundle { ")
        while (true) {
            val key = iterator.next()
            val value = bundle.get(key)
            sb.append(key).append('=')
            if (value is Bundle) {
                sb.append(if (value === bundle) "(this Bundle)" else bundle2String(value))
            } else {
                sb.append(formatObject(value))
            }
            if (!iterator.hasNext()) return sb.append(" }").toString()
            sb.append(',').append(' ')
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    private fun intent2String(intent: Intent): String {
        val sb = StringBuilder(128)
        sb.append("Intent { ")
        var first = true
        val mAction = intent.action
        if (mAction != null) {
            sb.append("act=").append(mAction)
            first = false
        }
        val mCategories = intent.categories
        if (mCategories != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("cat=[")
            var firstCategory = true
            for (c in mCategories) {
                if (!firstCategory) {
                    sb.append(',')
                }
                sb.append(c)
                firstCategory = false
            }
            sb.append("]")
        }
        val mData = intent.data
        if (mData != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("dat=").append(mData)
        }
        val mType = intent.type
        if (mType != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("typ=").append(mType)
        }
        val mFlags = intent.flags
        if (mFlags != 0) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("flg=0x").append(Integer.toHexString(mFlags))
        }
        val mPackage = intent.getPackage()
        if (mPackage != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("pkg=").append(mPackage)
        }
        val mComponent = intent.component
        if (mComponent != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("cmp=").append(mComponent.flattenToShortString())
        }
        val mSourceBounds = intent.sourceBounds
        if (mSourceBounds != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("bnds=").append(mSourceBounds.toShortString())
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            val mClipData = intent.clipData
            if (mClipData != null) {
                if (!first) {
                    sb.append(' ')
                }
                first = false
                clipData2String(mClipData, sb)
            }
        }
        val mExtras = intent.extras
        if (mExtras != null) {
            if (!first) {
                sb.append(' ')
            }
            first = false
            sb.append("extras={")
            sb.append(bundle2String(mExtras))
            sb.append('}')
        }
        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            val mSelector = intent.selector
            if (mSelector != null) {
                if (!first) {
                    sb.append(' ')
                }
                first = false
                sb.append("sel={")
                sb.append(if (mSelector === intent) "(this Intent)" else intent2String(mSelector))
                sb.append("}")
            }
        }
        sb.append(" }")
        return sb.toString()
    }

    private fun formatJson(json: String): String {
        try {
            var i = 0
            val len = json.length
            while (i < len) {
                val c = json[i]
                if (c == '{') {
                    return JSONObject(json).toString(2)
                } else if (c == '[') {
                    return JSONArray(json).toString(2)
                } else if (!Character.isWhitespace(c)) {
                    return json
                }
                i++
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return json
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private fun clipData2String(clipData: ClipData, sb: StringBuilder) {
        val item = clipData.getItemAt(0)
        if (item == null) {
            sb.append("ClipData.Item {}")
            return
        }
        sb.append("ClipData.Item { ")
        val mHtmlText = item.htmlText
        if (mHtmlText != null) {
            sb.append("H:")
            sb.append(mHtmlText)
            sb.append("}")
            return
        }
        val mText = item.text
        if (mText != null) {
            sb.append("T:")
            sb.append(mText)
            sb.append("}")
            return
        }
        val uri = item.uri
        if (uri != null) {
            sb.append("U:").append(uri)
            sb.append("}")
            return
        }
        val intent = item.intent
        if (intent != null) {
            sb.append("I:")
            sb.append(intent2String(intent))
            sb.append("}")
            return
        }
        sb.append("NULL")
        sb.append("}")
    }

    private fun array2String(`object`: Any): String {
        return when (`object`) {
            is Array<*> -> Arrays.deepToString(`object`)
            is BooleanArray -> Arrays.toString(`object`)
            is ByteArray -> Arrays.toString(`object`)
            is CharArray -> Arrays.toString(`object`)
            is DoubleArray -> Arrays.toString(`object`)
            is FloatArray -> Arrays.toString(`object`)
            is IntArray -> Arrays.toString(`object`)
            is LongArray -> Arrays.toString(`object`)
            is ShortArray -> Arrays.toString(`object`)
            else -> throw IllegalArgumentException("Array has incompatible type: " + `object`.javaClass)
        }
    }
}


private fun processTagAndHead(mTag: String): TagHead {
    var tag = mTag
    if (!config.tagIsSpace && !config.logHeadSwitch) {
        tag = config.globalTag
    } else {
        val stackTrace = Throwable().stackTrace
        val stackIndex = 3 + config.stackOffset
        if (stackIndex >= stackTrace.size) {
            val targetElement = stackTrace[3]
            val fileName = getFileName(targetElement)
            if (config.tagIsSpace && tag.isBlank()) {
                val index = fileName.indexOf('.')// Use proguard may not find '.'.
                tag = if (index == -1) fileName else fileName.substring(0, index)
            }
            return TagHead(tag, null, ": ")
        }
        var targetElement = stackTrace[stackIndex]
        //适配kotlin
        for (i in stackIndex until stackTrace.size) {
            if (targetElement.className.contains("KtLog")) {
                targetElement = if (i + 1 <= stackTrace.size - 1) {
                    stackTrace[i + 1]
                } else {
                    stackTrace[stackTrace.size - 1]
                }
            } else {
                break
            }
        }

        val fileName = getFileName(targetElement)
        if (config.tagIsSpace && tag.isBlank()) {
            val index = fileName.indexOf('.')// Use proguard may not find '.'.
            tag = if (index == -1) fileName else fileName.substring(0, index)
        }
        if (config.logHeadSwitch) {
            val tName = Thread.currentThread().name
            val head = Formatter()
                    .format("%s, %s.%s(%s:%d)",
                            tName,
                            targetElement.className,
                            targetElement.methodName,
                            fileName,
                            targetElement.lineNumber)
                    .toString()
            val fileHead = " [$head]: "
            if (config.stackDeep <= 1) {
                return TagHead(tag, arrayOf(head), fileHead)
            } else {
                val consoleHead = arrayOfNulls<String>(Math.min(
                        config.stackDeep,
                        stackTrace.size - stackIndex
                ))

                consoleHead[0] = head
                val spaceLen = tName.length + 2
                val space = Formatter().format("%" + spaceLen + "s", "").toString()
                var i = 1
                val len = consoleHead.size
                while (i < len) {
                    targetElement = stackTrace[i + stackIndex]
                    consoleHead[i] = Formatter()
                            .format("%s%s.%s(%s:%d)",
                                    space,
                                    targetElement.className,
                                    targetElement.methodName,
                                    getFileName(targetElement),
                                    targetElement.lineNumber)
                            .toString()
                    ++i
                }
                return TagHead(tag, consoleHead, fileHead)
            }
        }
    }
    return TagHead(tag, null, ": ")
}

private fun getFileName(targetElement: StackTraceElement): String {
    val fileName = targetElement.fileName
    if (fileName != null) return fileName
    // If name of file is null, should add
    // "-keepattributes SourceFile,LineNumberTable" in proguard file.
    var className = targetElement.className
    val classNameInfo = className.split("\\.".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    if (classNameInfo.isNotEmpty()) {
        className = classNameInfo[classNameInfo.size - 1]
    }
    val index = className.indexOf('$')
    if (index != -1) {
        className = className.substring(0, index)
    }
    return "$className.java"
}

private fun processBody(type: Int, vararg contents: Any?): String {
    var body: String = NULL
    if (!contents.isNullOrEmpty())
        if (contents.size == 1) {
            body = formatObject(type, contents[0])
        } else {
            val sb = StringBuilder()
            var i = 0
            val len = contents.size
            while (i < len) {
                val content = contents[i]
                sb.append(ARGS)
                        .append("[")
                        .append(i)
                        .append("]")
                        .append(" = ")
                        .append(formatObject(content))
                        .append(LINE_SEP)
                ++i
            }
            body = sb.toString()
        }
    return if (body.isEmpty()) NOTHING else body
}

private fun formatObject(type: Int, `object`: Any?): String {
    if (`object` == null) return NULL
    if (type == LOG_JSON) return LogFormatter.object2Json(`object`)
    return if (type == LOG_XML) LogFormatter.formatXml(`object`.toString()) else formatObject(`object`)
}

private fun formatObject(`object`: Any?): String {
    if (`object` == null) return NULL
    if (!I_FORMATTER_MAP.isEmpty) {
        val iFormatter = I_FORMATTER_MAP.get(getClassFromObject(`object`))
        if (iFormatter != null) {
            return iFormatter.invoke(`object`)
        }
    }
    return LogFormatter.object2String(`object`)
}

private fun print2Console(type: Int,
                          tag: String,
                          head: Array<String?>?,
                          msg: String) {
    if (config.singleTagSwitch) {
        printSingleTagMsg(type, tag, processSingleTagMsg(head, msg))
    } else {
        printBorder(type, tag, true)
        printHead(type, tag, head)
        printMsg(type, tag, msg)
        printBorder(type, tag, false)
    }
}

private fun printBorder(type: Int, tag: String, isTop: Boolean) {
    if (config.logBorderSwitch) {
        Log.println(type, tag, if (isTop) TOP_BORDER else BOTTOM_BORDER)
    }
}

private fun printHead(type: Int, tag: String, head: Array<String?>?) {
    if (head != null) {
        for (aHead in head) {
            Log.println(type, tag, if (config.logBorderSwitch) LEFT_BORDER + aHead else aHead)
        }
        if (config.logBorderSwitch) Log.println(type, tag, MIDDLE_BORDER)
    }
}

private fun printMsg(type: Int, tag: String, msg: String) {
    val len = msg.length
    val countOfSub = len / MAX_LEN
    if (countOfSub > 0) {
        var index = 0
        for (i in 0 until countOfSub) {
            printSubMsg(type, tag, msg.substring(index, index + MAX_LEN))
            index += MAX_LEN
        }
        if (index != len) {
            printSubMsg(type, tag, msg.substring(index, len))
        }
    } else {
        printSubMsg(type, tag, msg)
    }
}

private fun printSubMsg(type: Int, tag: String, msg: String) {
    if (!config.logBorderSwitch) {
        Log.println(type, tag, msg)
        return
    }
    val lines = msg.split(LINE_SEP.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    for (line in lines) {
        Log.println(type, tag, LEFT_BORDER + line)
    }
}

private fun processSingleTagMsg(head: Array<String?>?,
                                msg: String): String {
    val sb = StringBuilder()
    sb.append(PLACEHOLDER).append(LINE_SEP)
    if (config.logBorderSwitch) {
        sb.append(TOP_BORDER).append(LINE_SEP)
        if (head != null) {
            for (aHead in head) {
                sb.append(LEFT_BORDER).append(aHead).append(LINE_SEP)
            }
            sb.append(MIDDLE_BORDER).append(LINE_SEP)
        }
        for (line in msg.split(LINE_SEP.toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()) {
            sb.append(LEFT_BORDER).append(line).append(LINE_SEP)
        }
        sb.append(BOTTOM_BORDER)
    } else {
        if (head != null) {
            for (aHead in head) {
                sb.append(aHead).append(LINE_SEP)
            }
        }
        sb.append(msg)
    }
    return sb.toString()
}

private fun printSingleTagMsg(type: Int, tag: String, msg: String) {
    val len = msg.length
    val countOfSub = len / MAX_LEN
    if (countOfSub > 0) {
        if (config.logBorderSwitch) {
            Log.println(type, tag, msg.substring(0, MAX_LEN) + LINE_SEP + BOTTOM_BORDER)
            var index = MAX_LEN
            for (i in 1 until countOfSub) {
                Log.println(type, tag, PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP
                        + LEFT_BORDER + msg.substring(index, index + MAX_LEN)
                        + LINE_SEP + BOTTOM_BORDER)
                index += MAX_LEN
            }
            if (index != len) {
                Log.println(type, tag, PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP
                        + LEFT_BORDER + msg.substring(index, len))
            }
        } else {
            Log.println(type, tag, msg.substring(0, MAX_LEN))
            var index = MAX_LEN
            for (i in 1 until countOfSub) {
                Log.println(type, tag,
                        PLACEHOLDER + LINE_SEP + msg.substring(index, index + MAX_LEN))
                index += MAX_LEN
            }
            if (index != len) {
                Log.println(type, tag, PLACEHOLDER + LINE_SEP + msg.substring(index, len))
            }
        }
    } else {
        Log.println(type, tag, msg)
    }
}

private fun print2File(type: Int, tag: String, msg: String) {
    val now = Date(System.currentTimeMillis())
    val format = sdf.format(now)
    val date = format.substring(0, 10)
    val time = format.substring(11)
    val fullPath = ((if (config.dir == null) config.defaultDir else config.dir)
            + config.filePrefix + "-" + date + ".txt")
    if (!createOrExistsFile(fullPath)) {
        Log.e("LogUtils", "create $fullPath failed!")
        return
    }
    val sb = StringBuilder()
    sb.append(time)
            .append(typeArray[type - LogType.V.value()])
            .append("/")
            .append(tag)
            .append(msg)
            .append(LINE_SEP)
    val content = sb.toString()
    input2File(content, fullPath)
}

private val sdf: SimpleDateFormat
    get() {
        var simpleDateFormat = SDF_THREAD_LOCAL.get()
        if (simpleDateFormat == null) {
            simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            SDF_THREAD_LOCAL.set(simpleDateFormat)
        }
        return simpleDateFormat
    }

private fun createOrExistsFile(filePath: String): Boolean {
    val file = File(filePath)
    if (file.exists()) return file.isFile
    if (!createOrExistsDir(file.parentFile)) return false
    return try {
        deleteDueLogs(filePath)
        val isCreate = file.createNewFile()
        if (isCreate) {
            printDeviceInfo(filePath)
        }
        isCreate
    } catch (e: IOException) {
        e.printStackTrace()
        false
    }
}

private fun deleteDueLogs(filePath: String) {
    val file = File(filePath)
    val parentFile = file.parentFile
    val files = parentFile.listFiles { _, name -> name.matches(("^" + config.filePrefix + "-[0-9]{4}-[0-9]{2}-[0-9]{2}.txt$").toRegex()) }
    if (files.isEmpty()) return
    val length = filePath.length
    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    try {
        val curDay = filePath.substring(length - 14, length - 4)
        val dueMillis = sdf.parse(curDay).time - config.saveDays * 86400000L
        for (aFile in files) {
            val name = aFile.name
            val l = name.length
            val logDay = name.substring(l - 14, l - 4)
            if (sdf.parse(logDay).time <= dueMillis) {
                EXECUTOR.execute {
                    val delete = aFile.delete()
                    if (!delete) {
                        Log.e("LogUtils", "delete $aFile failed!")
                    }
                }
            }
        }
    } catch (e: ParseException) {
        e.printStackTrace()
    }
}

private fun printDeviceInfo(filePath: String) {
    var versionName = ""
    var versionCode = 0
    try {
        val pi = KtUtilCode.app
                .packageManager
                .getPackageInfo(KtUtilCode.app.packageName, 0)
        if (pi != null) {
            versionName = pi.versionName
            versionCode = pi.versionCode
        }
    } catch (e: PackageManager.NameNotFoundException) {
        e.printStackTrace()
    }

    val time = filePath.substring(filePath.length - 14, filePath.length - 4)
    val head = "************* Log Head ****************" +
            "\nDate of Log        : " + time +
            "\nDevice Manufacturer: " + Build.MANUFACTURER +
            "\nDevice Model       : " + Build.MODEL +
            "\nAndroid Version    : " + Build.VERSION.RELEASE +
            "\nAndroid SDK        : " + Build.VERSION.SDK_INT +
            "\nApp VersionName    : " + versionName +
            "\nApp VersionCode    : " + versionCode +
            "\n************* Log Head ****************\n\n"
    input2File(head, filePath)
}

private fun createOrExistsDir(file: File?): Boolean {
    return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
}

private fun input2File(input: String, filePath: String) {
    EXECUTOR.execute {
        var bw: BufferedWriter? = null
        try {
            bw = BufferedWriter(FileWriter(filePath, true))
            bw.write(input)
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("LogUtils", "log to $filePath failed!")
        } finally {
            try {
                bw?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}

private fun getTypeClassFromParadigm(formatter: IFormatter): Class<*>? {
    val genericInterfaces = formatter.javaClass.genericInterfaces
    var type: Type?
    type = if (genericInterfaces.size == 1) {
        genericInterfaces[0]
    } else {
        formatter.javaClass.genericSuperclass
    }
    type = (type as ParameterizedType).actualTypeArguments[0]
    while (type is ParameterizedType) {
        type = type.rawType
    }
    var className = type!!.toString()
    if (className.startsWith("class ")) {
        className = className.substring(6)
    } else if (className.startsWith("interface ")) {
        className = className.substring(10)
    }
    return try {
        Class.forName(className)
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
        null
    }
}

private fun getClassFromObject(obj: Any): Class<*> {
    val objClass = obj.javaClass
    if (objClass.isAnonymousClass || objClass.isSynthetic) {
        val genericInterfaces = objClass.genericInterfaces
        var className: String
        if (genericInterfaces.size == 1) {// interface
            var type = genericInterfaces[0]
            while (type is ParameterizedType) {
                type = type.rawType
            }
            className = type.toString()
        } else {// abstract class or lambda
            var type = objClass.genericSuperclass
            while (type is ParameterizedType) {
                type = type.rawType
            }
            className = type!!.toString()
        }

        if (className.startsWith("class ")) {
            className = className.substring(6)
        } else if (className.startsWith("interface ")) {
            className = className.substring(10)
        }
        try {
            return Class.forName(className)
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }
    return objClass
}


/**
 * 外部获取LogConfig
 * @return LogConfig
 */
fun getLogConfig(): LogConfig = config

///////////////////////////////////////////////////////////////////////////
// log方法
///////////////////////////////////////////////////////////////////////////
fun logV(vararg contents: Any?) {
    log(LogType.V.value(), config.globalTag, *contents)
}

fun logVTag(tag: String, vararg contents: Any?) {
    log(LogType.V.value(), tag, *contents)
}

fun logD(vararg contents: Any?) {
    log(LogType.D.value(), config.globalTag, *contents)
}

fun logDTag(tag: String, vararg contents: Any?) {
    log(LogType.D.value(), tag, *contents)
}

fun logI(vararg contents: Any?) {
    log(LogType.I.value(), config.globalTag, *contents)
}

fun logITag(tag: String, vararg contents: Any?) {
    log(LogType.I.value(), tag, *contents)
}

fun logW(vararg contents: Any?) {
    log(LogType.W.value(), config.globalTag, *contents)
}

fun logWTag(tag: String, vararg contents: Any?) {
    log(LogType.W.value(), tag, *contents)
}

fun logE(vararg contents: Any?) {
    log(LogType.E.value(), config.globalTag, *contents)
}

fun logETag(tag: String, vararg contents: Any?) {
    log(LogType.E.value(), tag, *contents)
}

fun logA(vararg contents: Any?) {
    log(LogType.A.value(), config.globalTag, *contents)
}

fun logATag(tag: String, vararg contents: Any?) {
    log(LogType.A.value(), tag, *contents)
}

fun logFile(content: Any?) {
    log(LOG_FILE or LogType.D.value(), config.globalTag, content)
}

fun logFile(type: LogType, content: Any?) {
    log(LOG_FILE or type.value(), config.globalTag, content)
}

fun logFile(tag: String, content: Any?) {
    log(LOG_FILE or LogType.D.value(), tag, content)
}

fun logFile(type: LogType, tag: String, content: Any?) {
    log(LOG_FILE or type.value(), tag, content)
}

fun logJson(content: Any?) {
    log(LOG_JSON or LogType.D.value(), config.globalTag, content)
}

fun logjson(type: LogType, content: Any?) {
    log(LOG_JSON or type.value(), config.globalTag, content)
}

fun logJson(tag: String, content: Any?) {
    log(LOG_JSON or LogType.D.value(), tag, content)
}

fun logJson(type: LogType, tag: String, content: Any?) {
    log(LOG_JSON or type.value(), tag, content)
}

fun logXml(content: String?) {
    log(LOG_XML or LogType.D.value(), config.globalTag, content)
}

fun logXml(type: LogType, content: String?) {
    log(LOG_XML or type.value(), config.globalTag, content)
}

fun logXml(tag: String, content: String?) {
    log(LOG_XML or LogType.D.value(), tag, content)
}

fun logXml(type: LogType, tag: String, content: String?) {
    log(LOG_XML or type.value(), tag, content)
}

fun log(type: Int, tag: String, vararg contents: Any?) {
    if (!config.logSwitch || !config.log2ConsoleSwitch && !config.log2FileSwitch) return
    val typeLow = type and 0x0f
    val typeHigh = type and 0xf0
    if (typeLow < config.consoleFilter.value() && typeLow < config.fileFilter.value()) return
    val tagHead = processTagAndHead(tag)
    val body = processBody(typeHigh, *contents)
    if (config.log2ConsoleSwitch && typeLow >= config.consoleFilter.value() && typeHigh != LOG_FILE) {
        print2Console(typeLow, tagHead.tag, tagHead.consoleHead, body)
    }
    if ((config.log2FileSwitch || typeHigh == LOG_FILE) && typeLow >= config.fileFilter.value()) {
        print2File(typeLow, tagHead.tag, tagHead.fileHead + body)
    }
}


///////////////////////////////////////////////////////////////////////////
// 扩展方法
///////////////////////////////////////////////////////////////////////////

fun Any?.logV(tag: String = getLogConfig().globalTag) {
    logVTag(tag, this)
}

fun Any?.logD(tag: String = getLogConfig().globalTag) {
    logDTag(tag, this)
}

fun Any?.logI(tag: String = getLogConfig().globalTag) {
    logITag(tag, this)
}

fun Any?.logW(tag: String = getLogConfig().globalTag) {
    logW(tag, this)
}

fun Any?.logE(tag: String = getLogConfig().globalTag) {
    logE(tag, this)
}

fun Any?.logA(tag: String = getLogConfig().globalTag) {
    logA(tag, this)
}

fun Any?.logFile(type: LogType = LogType.D, tag: String = getLogConfig().globalTag) {
    logFile(type, tag, this)
}

fun Any?.logJson(type: LogType = LogType.D, tag: String = getLogConfig().globalTag) {
    logJson(type, tag, this)
}

fun String?.logXml(type: LogType = LogType.D, tag: String = getLogConfig().globalTag) {
    logXml(type, tag, this)
}