package insaneio.insane.cryptyography

import insaneio.insane.AES_BLOCK_SIZE_LENGTH

internal object AesZerosPadding : IAesPadding {
    override fun addPadding(data: ByteArray, blockSize: UInt): ByteArray {
        val paddingLength = AES_BLOCK_SIZE_LENGTH.toInt() - data.size % AES_BLOCK_SIZE_LENGTH.toInt()
        return data.plus(ByteArray(paddingLength))
    }

    override fun removePadding(data: ByteArray): ByteArray {
        var i: Int = data.size
        while (i > 0 && data[i - 1] == 0.toByte()) {
            i--
        }
        return data.take(i).toByteArray()
    }
}