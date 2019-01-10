package top.limuyang2.androidkotlinutilcodeapp

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.EditText
import com.alibaba.fastjson.JSON
import com.google.gson.Gson
import top.limuyang2.android.ktutilcode.core.*
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setStatusBarColor(resources.getColor(R.color.colorPrimary), 100)

        getSP().edit {
            putBoolean("test", true)

        }
        var c = false
        getSP().read {
            c = getBoolean("test", false)
            println("---------->>>  &$c")
        }

        val path = "/sdcard/jeejen/ttt2.txt".toFile()


        val path2 = "/sdcard/ttt.txt".toFile()
        println("path2.createFile()  --->  " + path2.createFile())
//        path2.rename("rrr.txt")

        val et = EditText(this)


        println("-------> md5 :  " + "123456".md5())
        println("-------> sha1 :  " + "123456".sha1())
        println("-------> sha256 :  " + "123456".sha256())
        println("-------> sha384 :  " + "123456".sha384())
        println("-------> sha512 :  " + "123456".sha512())

        println("-------> file :  " + path2.md5())

//        getSP().apply {
//            c = getBoolean("test", false)
//        }

//        val newstringBuilder = StringBuilder()
//        var inputStream: InputStream? = null
//        try {
//            inputStream = resources.assets.open("citys.json")
//            val isr = InputStreamReader(inputStream)
//            val reader = BufferedReader(isr)
//            var jsonLine: String? = reader.readLine()
//            while (jsonLine != null) {
//                newstringBuilder.append(jsonLine)
//                jsonLine = reader.readLine()
//            }
//            reader.close()
//            isr.close()
//            inputStream!!.close()
//        } catch (e: IOException) {
//            e.printStackTrace()
//        }
//
//
//        val result = newstringBuilder.toString()
//
////        val objectType = type(BeanBean::class.java, clazz)
////        val common = Gson().fromJson(result, Data::class.java)
//
//        val a = fJson<BaseBean<String>>(result)
//        val c = fJson<Array<Data>>(a.data.toString())
////        val c = a.data as ArrayList<Data>
//        println("--------> ${c.size}")
////        println("--------> ${c.get(0).locationId}")
//
//        val group = fJson2<BaseBean<String>>(result)
//        println("-------->fast ${group.data!!}")
    }

    inline fun <reified T> fJson(jsonStr: String): T {
        return Gson().fromJson(jsonStr, T::class.java)
    }

    inline fun <reified T> fJson2(jsonStr: String): T {
        return JSON.parseObject(jsonStr, T::class.java)
    }

    fun type(raw: Class<*>, vararg args: Type): ParameterizedType {
        return object : ParameterizedType {
            override fun getRawType(): Type = raw
            override fun getOwnerType(): Type? = null

            override fun getActualTypeArguments(): Array<Type> {
                val a = emptyArray<Type>()
                for (n in 0 until args.size) {
                    a[n] = args[n]
                }
                return a
            }

        }
    }
}
