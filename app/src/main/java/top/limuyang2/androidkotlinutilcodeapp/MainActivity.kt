package top.limuyang2.androidkotlinutilcodeapp

import android.graphics.Color
import android.os.Bundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_main.*
import top.limuyang2.android.ktutilcode.core.*
import top.limuyang2.android.ktutilcode.widget.onTextChange
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type


class MainActivity : AppCompatActivity() {

    data class A(val name: String = "22", val age: Int = 2)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setStatusBarColor(resources.getColor(R.color.colorPrimary), true)

        getSP().edit {
            putBoolean("test", true)

        }
        var c = false
        getSP().read {
            c = getBoolean("test", false)
            println("---------->>>  &$c")
        }

        val path = "/sdcard/jeejen/ttt2.txt".toFile()


        val newFile = "/sdcard/ttt.txt".toFile()
        println("path2.createFile()  --->  " + newFile.createFile())
//        path2.rename("rrr.txt")

        val et = EditText(this)


        println("-------> md5 :  " + "123456".md5())
        println("-------> sha1 :  " + "123456".sha1())
        println("-------> sha256 :  " + "123456".sha256())
        println("-------> sha384 :  " + "123456".sha384())
        println("-------> sha512 :  " + "123456".sha512())

        println("-------> file :  " + newFile.md5())

        val encryptDES = "123456".encryptDES("12345678")
        println("-------> encryptDES : $encryptDES")
        println("-------> decryptDES : " + encryptDES.decryptDES("12345678"))

        val encryptAES = "123456".encryptAES("1234567812345678", "AES/CBC/PKCS5Padding")
        println("-------> encryptAES : $encryptAES")
        println("-------> decryptAES : " + encryptAES.decryptAES("1234567812345678", "AES/CBC/PKCS5Padding"))

        val e = EditText(this)
        e.onTextChange {
            onTextChanged { s, start, before, count -> }
            afterTextChanged {

            }
            beforeTextChanged { s, start, count, after -> }
        }

        ArrayList<String>().apply {
            add("23")
            add("34")
            add("55")
        }.logV()

        dip(2)


        tv.text = "test".setBackgroundColor(Color.LTGRAY).setForegroundColor(Color.RED)
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

    }


}
