package dev.defvs.cafet.client.files

import com.beust.klaxon.Json
import com.beust.klaxon.Klaxon
import dev.defvs.cafet.client.files.music.MusicFileParser
import java.nio.file.Path
import kotlin.io.path.listDirectoryEntries
import kotlin.io.path.name
import kotlin.io.path.nameWithoutExtension
import kotlin.io.path.writeText

object CafetFileParser {
	data class CafetMusicItem(
		val originalFileName: String,
		val originalTags: Map<String, String>,
		val audioHash: String,
	)

	data class CafetAlbumItem(
		val originalFolderName: String,
		val albumTags: Map<String, String>,
		val musicItems: List<CafetMusicItem>,
	)

	data class CafetFile(
		@Json(name = "version") val cafetVersion: Int,
		@Json(name = "item") val cafetAlbumItem: CafetAlbumItem,
	)

	fun parse(path: Path): CafetFile? = Klaxon().parse<CafetFile>(path.toFile())

	fun init(path: Path) = CafetFile(
		Constants.CAFET_FILE_VERSION,
		CafetAlbumItem(
			path.parent.name,
			mapOf(),
			path.parent.listDirectoryEntries().map {
				CafetMusicItem(
					it.nameWithoutExtension,
					MusicFileParser.getDefaultParser(it).getTags(it),
					MusicFileParser.getDefaultParser(it).getHash(it)
				)
			}
		)
	).also { path.writeText(Klaxon().toJsonString(it)) }
}