package insaneio.insane.cryptography.internal

import kotlinx.serialization.Serializable

internal sealed interface IAesPadding {
    fun addPadding(data:ByteArray, blockSize: UInt):ByteArray
    fun removePadding(data:ByteArray):ByteArray
}