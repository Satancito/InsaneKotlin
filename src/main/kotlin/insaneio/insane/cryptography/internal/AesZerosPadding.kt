package insaneio.insane.cryptography.internal

internal data object AesZerosPadding : IAesPadding {
    override fun addPadding(data: ByteArray, blockSize: UInt): ByteArray {
        val paddingLength = blockSize.toInt() - data.size % blockSize.toInt()
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