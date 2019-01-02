package top.limuyang2.androidkotlinutilcodeapp

data class BaseBean<T>(val data: T? = null,
                       val resultCode: Int = 0,
                       val resultCount: String = "",
                       val resultMsg: String = "")