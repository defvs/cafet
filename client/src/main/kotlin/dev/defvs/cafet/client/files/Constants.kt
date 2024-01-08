package dev.defvs.cafet.client.files

import org.jaudiotagger.tag.FieldKey
import org.jaudiotagger.tag.Tag

object Constants {
	const val CAFET_FILE_VERSION = 1
	val musicExtensions = listOf(
		"flac",
	)
	val importantTags = listOf(
		FieldKey.YEAR,
		FieldKey.ALBUM,
		FieldKey.ALBUM_ARTIST,
		FieldKey.ALBUM_YEAR,
		FieldKey.ARTIST,
		FieldKey.TITLE,
		FieldKey.BPM,
		FieldKey.TRACK,
		FieldKey.DISC_NO,
		FieldKey.CATALOG_NO,
		FieldKey.GENRE,
	)
}