import dev.defvs.cafet.client.files.CafetFolder.Companion.findSuitableCafetFolders
import io.kotest.core.spec.style.FunSpec
import kotlin.io.path.Path

class CafetFilesTests : FunSpec({
	test("Find compatible folders") {
		val rootFolder = Path("""C:\Users\danie\Desktop\testmusic\""")
		rootFolder.findSuitableCafetFolders().also {
			println(it.joinToString())
		}
	}
})