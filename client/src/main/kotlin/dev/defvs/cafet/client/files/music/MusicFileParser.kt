package dev.defvs.cafet.client.files.music

import dev.defvs.cafet.client.Util.Crypto.computeSha1Chunks
import dev.defvs.cafet.client.Util.Crypto.sha1
import dev.defvs.cafet.client.Util.createMappedByteBuffer
import dev.defvs.cafet.client.Util.filterNotNullValues
import dev.defvs.cafet.client.files.Constants
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import java.nio.ByteBuffer
import java.nio.channels.FileChannel
import java.nio.file.Path
import java.nio.file.StandardOpenOption
import kotlin.io.path.extension

abstract class MusicFileParser {
	open fun getTags(path: Path): Map<String, String> {
		val f: AudioFile = AudioFileIO.read(path.toFile())
		return Constants.importantTags.associate {
			it.name to f.tag.getFirst(it)?.ifEmpty { null }
		}.filterNotNullValues()
	}

	abstract fun getSoundDataByteRange(path: Path): LongRange

	open fun getHash(path: Path): ByteArray = path.createMappedByteBuffer(getSoundDataByteRange(path)).sha1

	open fun getHashes(path: Path, pieceSize: Int): List<ByteArray> =
		path.createMappedByteBuffer(getSoundDataByteRange(path)).computeSha1Chunks(pieceSize)

	class FlacParser : MusicFileParser() {
		override fun getSoundDataByteRange(path: Path): LongRange {
			val buffer = ByteBuffer.allocate(4)  // Size for reading FLAC header

			FileChannel.open(path, StandardOpenOption.READ).use { fileChannel ->
				// Read the 'fLaC' marker at the start of the file
				fileChannel.read(buffer)
				buffer.flip()
				val marker = String(buffer.array())
				if (marker != "fLaC") {
					throw Exception("Invalid FLAC file")
				}

				// Continue reading metadata blocks to find the start of the audio frames
				var isLastBlock = false
				var audioStart = 4  // Starting after the 'fLaC' marker
				while (!isLastBlock) {
					buffer.clear()
					fileChannel.read(buffer)
					buffer.flip()

					val firstByte = buffer.get()
					isLastBlock = (firstByte.toInt() and 0x80) != 0  // Check if it's the last block
					val blockSize =
						(firstByte.toInt() and 0x7F) shl 24 or (buffer.get().toInt() and 0xFF shl 16) or (buffer.get()
							.toInt() and 0xFF shl 8) or (buffer.get().toInt() and 0xFF)

					audioStart += 4 + blockSize  // Skip over this block
					fileChannel.position(audioStart.toLong())
				}

				// At this point, audioStart is the beginning of the audio frames
				val audioEnd = fileChannel.size() - 1  // Assuming audio frames go till the end of the file

				return LongRange(audioStart.toLong(), audioEnd)
			}
		}
	}

	companion object {
		private val defaultParsers = mapOf(
			"flac" to FlacParser(),
		)

		fun getDefaultParser(path: Path) =
			defaultParsers.getOrElse(path.extension) { throw Exception("No parser for extension ${path.extension}.") }
	}
}