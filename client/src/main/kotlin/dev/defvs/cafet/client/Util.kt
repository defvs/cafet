package dev.defvs.cafet.client

import java.nio.ByteBuffer
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import java.security.MessageDigest

object Util {
	/**
	 * Extension function for Map to filter out entries with null values.
	 *
	 * @param K the type of map keys.
	 * @param V the type of map values, nullable.
	 * @return Map<K, V> a new map with the same entries as the original map,
	 *         except entries with null values are omitted.
	 */
	fun <K, V> Map<K, V?>.filterNotNullValues(): Map<K, V> {
		val result = LinkedHashMap<K, V>()
		for ((key, value) in this) {
			if (value != null) result[key] = value
		}
		return result
	}

	/**
	 * Creates a MappedByteBuffer for the specified file path and range.
	 *
	 * The function maps a region of this file directly into memory. This can be much faster
	 * than reading or writing data from or to the disk because it avoids the overhead of copying
	 * data between kernel space and user space.
	 *
	 * @receiver the Path of the file to be read.
	 * @param range the LongRange specifying the part of the file to map into memory.
	 * @return MappedByteBuffer a direct byte buffer whose content is a memory-mapped region of the file.
	 */
	fun Path.createMappedByteBuffer(range: LongRange): MappedByteBuffer {
		FileChannel.open(this, StandardOpenOption.READ).use { fileChannel ->
			return fileChannel.map(FileChannel.MapMode.READ_ONLY, range.first, range.last - range.first + 1)
		}
	}

	object Crypto {
		/**
		 * An extension property for ByteBuffer to compute the SHA-1 hash.
		 *
		 * This property provides an easy way to calculate the SHA-1 hash of the data in a ByteBuffer.
		 * The hash computation is performed on the data from the current position to the limit of the ByteBuffer.
		 * The position of the buffer is marked before the calculation and reset to this mark after the calculation,
		 * ensuring that the state of the ByteBuffer remains unchanged after the hash is computed.
		 *
		 * @return ByteArray representing the SHA-1 hash of the ByteBuffer's contents.
		 */
		val ByteBuffer.sha1: ByteArray
			get() {
				val md = MessageDigest.getInstance("SHA-1")
				this.mark() // Mark the current position to reset after digest calculation
				md.update(this)
				this.reset() // Reset the position back to the marked position
				return md.digest()
			}

		/**
		 * Extension function for ByteBuffer to compute SHA-1 hashes for chunks of size [chunkSize] bytes.
		 *
		 * This function divides the buffer's remaining data (from its current position to its limit) into chunks of the specified size,
		 * computes the SHA-1 hash for each chunk, and returns a list of these hashes.
		 *
		 * Note: The position of the ByteBuffer will be at its limit after this operation.
		 *
		 * @param chunkSize The size of each chunk in bytes.
		 * @return A list of byte arrays, each representing the SHA-1 hash of a chunk.
		 */
		fun ByteBuffer.computeSha1Chunks(chunkSize: Int): List<ByteArray> {
			val messageDigest = MessageDigest.getInstance("SHA-1")
			val hashes = mutableListOf<ByteArray>()

			while (this.hasRemaining()) {
				val remaining = this.remaining()
				val size = if (remaining < chunkSize) remaining else chunkSize
				val chunk = ByteArray(size)
				this.get(chunk, 0, size)

				messageDigest.reset() // Reset the digest for each new chunk
				messageDigest.update(chunk)
				hashes.add(messageDigest.digest())
			}

			return hashes
		}
	}

}