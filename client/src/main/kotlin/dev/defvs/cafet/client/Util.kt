package dev.defvs.cafet.client

object Util {
	fun <K, V> Map<K, V?>.filterNotNullValues(): Map<K, V> {
		val result = LinkedHashMap<K, V>()
		for ((key, value) in this) {
			if (value != null) result[key] = value
		}
		return result
	}
}