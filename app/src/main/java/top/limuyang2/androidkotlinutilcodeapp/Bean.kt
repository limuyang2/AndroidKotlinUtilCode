package top.limuyang2.androidkotlinutilcodeapp


data class Data(
        val firstLetter: String = "",
        val locationId: Long = 0,
        val locationLever: Int = 0,
        val locationName: String = "",
        val subordinateData: List<SubordinateData1> = listOf()
//        val superLocationId: Any = Any()
) {
    data class SubordinateData1(
            val firstLetter: String = "",
            val locationId: Long = 0,
            val locationLever: Int = 0,
            val locationName: String = "",
            val subordinateData: List<SubordinateData2> = listOf(),
            val superLocationId: Long = 0
    ) {
        data class SubordinateData2(
                val firstLetter: String = "",
                val locationId: Long = 0,
                val locationLever: Int = 0,
                val locationName: String = "",
                val subordinateData: Any? = null,
                val superLocationId: Long = 0
        )
    }
}






