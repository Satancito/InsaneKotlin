package insaneio.insane.cryptyography

import insaneio.insane.AES_BLOCK_SIZE_LENGTH

internal object AesAnsiX923Padding : IAesPadding {
    override fun addPadding(data: ByteArray, blockSize: UInt):ByteArray {
        val paddingLength = AES_BLOCK_SIZE_LENGTH.toInt() - data.size % AES_BLOCK_SIZE_LENGTH.toInt()
        val paddedData = data.plus(ByteArray(paddingLength))
        paddedData[paddedData.size - 1] = paddingLength.toByte()
        return paddedData
    }

    override fun removePadding(data: ByteArray):ByteArray {
        return data.take(data.size - data[data.size-1].toInt()).toByteArray()
    }
}