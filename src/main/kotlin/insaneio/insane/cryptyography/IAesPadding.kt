package insaneio.insane.cryptyography

internal interface IAesPadding {
    abstract fun addPadding(data:ByteArray, blockSize: UInt):ByteArray
    abstract fun removePadding(data:ByteArray):ByteArray
}