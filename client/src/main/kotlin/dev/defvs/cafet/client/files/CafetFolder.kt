package dev.defvs.cafet.client.files

import java.nio.file.FileVisitResult
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes
import kotlin.io.path.isRegularFile

class CafetFileException(message: String, path: Path) : Exception("$message [$path]")

class CafetFolder(cafetFilePath: Path, allowCreateFile: Boolean = false) {
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

	companion object {
		fun Path.findSuitableCafetFolders(): List<Path> {
			val suitableFolders = mutableListOf<Path>()
			val unsuitableFolders = mutableSetOf<Path>()

			Files.walkFileTree(this, object : SimpleFileVisitor<Path>() {
				override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
					if (dir != this@findSuitableCafetFolders) {
						val hasFlacInSubfolders = Files.walk(dir, 1)
							.anyMatch { it.toString().endsWith(".flac") && Files.isDirectory(it.parent) }

						if (hasFlacInSubfolders) {
							suitableFolders.add(dir)
							return FileVisitResult.SKIP_SUBTREE
						}
					}
					return FileVisitResult.CONTINUE
				}

				override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
					if (file.toString().endsWith(".flac") || file.toString().endsWith(".cue") || file.toString()
							.endsWith(".log")
					) {
						if (Files.walk(file.parent, 1)
								.anyMatch { subFile ->
									Files.isDirectory(subFile) &&
											Files.walk(subFile)
												.anyMatch {
													it.toString().endsWith(".flac") || it.toString()
														.endsWith(".cue") || it.toString().endsWith(".log")
												}
								}
						) {
							unsuitableFolders.add(file.parent)
						} else {
							suitableFolders.add(file.parent)
						}
					}
					return FileVisitResult.CONTINUE
				}
			})

			return suitableFolders.filterNot { it in unsuitableFolders }.distinct()
		}

		fun initFolder(path: Path) = CafetFolder(path.resolve("./.cafet"), allowCreateFile = true)
	}
}