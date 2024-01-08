package dev.defvs.cafet.client.files

import java.nio.file.Path
import kotlin.io.path.isRegularFile

class CafetFileException(message: String, path: Path) : Exception(message)

class CafetFolder(private val cafetFilePath: Path, allowCreateFile: Boolean = false) {
	private val definition: CafetFileParser.CafetAlbumItem

	init {
		definition = when {
			!cafetFilePath.isRegularFile() -> {
				if (!allowCreateFile) throw CafetFileException("Cafet file does not exist.", cafetFilePath)
				CafetFileParser.init(cafetFilePath).cafetAlbumItem
			}

			else -> CafetFileParser.parse(cafetFilePath)?.cafetAlbumItem ?: throw CafetFileException(
				"Cafet file is invalid.",
				cafetFilePath
			)
		}
	}
}