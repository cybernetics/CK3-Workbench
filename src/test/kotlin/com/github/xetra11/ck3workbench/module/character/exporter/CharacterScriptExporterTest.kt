package com.github.xetra11.ck3workbench.module.character.exporter

import com.github.xetra11.ck3workbench.app.StateManager
import com.github.xetra11.ck3workbench.module.character.CharacterTemplate
import io.kotest.core.spec.style.ShouldSpec
import io.kotest.matchers.shouldBe
import java.io.File

class CharacterScriptExporterTest : ShouldSpec({
    val exportFile = File("characters.txt")
    val expectedExportFile = File("src/test/resources/fixtures/character/export/expectedCharacterExport.txt")

    val characterScriptExporter = CharacterScriptExporter()

    // TODO: disabled but needs to be fixed for windows environment
    should("export characters from state manager to script txt file") {
        StateManager.characters.addAll(
            listOf(
                CharacterTemplate.DEFAULT_CHARACTER,
                CharacterTemplate.DEFAULT_CHARACTER
            )
        )
        characterScriptExporter.export()

        exportFile.readText(Charsets.UTF_8) shouldBe expectedExportFile.readText(Charsets.UTF_8)
    }
})
