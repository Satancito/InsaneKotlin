package insaneio.insane.cryptography.internal

internal sealed interface IAesPadding {
    fun addPadding(data:ByteArray, blockSize: UInt):ByteArray
    fun removePadding(data:ByteArray):ByteArray
}



