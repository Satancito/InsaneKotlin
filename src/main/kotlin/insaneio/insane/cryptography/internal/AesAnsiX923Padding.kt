package insaneio.insane.cryptography.internal

internal data object AesAnsiX923Padding : IAesPadding {
    override fun addPadding(data: ByteArray, blockSize: UInt):ByteArray {
        val paddingLength = blockSize.toInt() - data.size % blockSize.toInt()
        val paddedData = data.plus(ByteArray(paddingLength))
        paddedData[paddedData.size - 1] = paddingLength.toByte()
        return paddedData
    }

    override fun removePadding(data: ByteArray):ByteArray {
        return data.take(data.size - data[data.size-1].toInt()).toByteArray()
    }
}