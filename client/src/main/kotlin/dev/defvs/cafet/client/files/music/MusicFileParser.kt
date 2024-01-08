package dev.defvs.cafet.client.files.music

import dev.defvs.cafet.client.Util.filterNotNullValues
import dev.defvs.cafet.client.files.Constants
import org.jaudiotagger.audio.AudioFile
import org.jaudiotagger.audio.AudioFileIO
import java.nio.file.Path
import kotlin.io.path.extension

abstract class MusicFileParser {
	open fun getTags(path: Path): Map<String, String> {
		val f: AudioFile = AudioFileIO.read(path.toFile())
		return Constants.importantTags.associate {
			it.name to f.tag.getFirst(it)?.ifEmpty { null }
		}.filterNotNullValues()
	}

	abstract fun getHash(path: Path): String

	class FlacParser : MusicFileParser() {
		override fun getHash(path: Path): String {
			TODO("Not yet implemented")
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