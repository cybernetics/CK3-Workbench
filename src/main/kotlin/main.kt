import androidx.compose.desktop.AppManager
import androidx.compose.desktop.AppWindow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Colors
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Menu
import androidx.compose.ui.window.MenuBar
import androidx.compose.ui.window.MenuItem
import com.github.xetra11.ck3workbench.ScriptValidator
import com.github.xetra11.ck3workbench.ScriptValidator.ValidationError
import com.github.xetra11.ck3workbench.module.character.Character
import com.github.xetra11.ck3workbench.module.character.CharacterTemplate
import com.github.xetra11.ck3workbench.module.character.importer.CharacterScriptReader
import com.github.xetra11.ck3workbench.module.character.importer.CharacterScriptValidator
import com.github.xetra11.ck3workbench.module.character.view.CharacterModuleView
import com.github.xetra11.ck3workbench.styles.WorkbenchButtons.BasicButton
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.awt.FileDialog
import java.io.File
import javax.swing.SwingUtilities.invokeLater

fun main() = invokeLater {
    val LOG: Logger = LoggerFactory.getLogger("Main")
    lateinit var window: AppWindow
    val hasAlert = mutableStateOf(false)
    val validationErrors = mutableStateListOf<ValidationError>()

    val characterState: SnapshotStateList<Character> =
        mutableStateListOf(
            CharacterTemplate.DEFAULT_CHARACTER,
            CharacterTemplate.DEFAULT_CHARACTER,
            CharacterTemplate.DEFAULT_CHARACTER
        )

    window = AppWindow(
        title = "CK3 Mod Workbench",
        menuBar = MenuBar(
            Menu("File", MenuItem("Exit", onClick = { AppManager.exit() })),
            Menu(
                "Characters",
                MenuItem("Import characters", onClick = {
                    val file = mutableStateOf(File(""))

                    val fileDialog = FileDialog(window.window)
                    fileDialog.mode = FileDialog.LOAD
                    fileDialog.isVisible = true
                    file.value = File(fileDialog.directory + fileDialog.file)

                    val characterScriptValidator = CharacterScriptValidator()
                    val validation = characterScriptValidator.validate(file.value.readText())
                    if (validation.hasErrors) {
                        LOG.error("Script to be imported has errors")
                        LOG.error("Errors found:")
                        validation.errors.forEach {
                            LOG.error(it.reason)
                        }
                        validationErrors.addAll(validation.errors)
                        hasAlert.value = true
                    } else {
                        val characterScriptReader = CharacterScriptReader()
                        val character = characterScriptReader.readCharacterScript(file.value.absoluteFile)
                        character?.let { characterState.add(it) }
                    }
                }),
                MenuItem("Dynasties", onClick = {})
            ),
        )
    )

    window.show {
        MaterialTheme(
            colors = workbenchLightColors(),
            shapes = workBenchShapes()
        ) {
            if (hasAlert.value) {
                CharacterValidationErrorAlert(hasAlert, validationErrors)
            }
            CharacterModuleView(characterState)
        }
    }
}

@Composable
private fun CharacterValidationErrorAlert(
    alertState: MutableState<Boolean>,
    validationErrors: SnapshotStateList<ValidationError>
) {
    AlertDialog(
        title = { Text("Validation Error") },
        text = {
            Row { Text("Following validation reasons occured: ") }
            validationErrors.forEach {
                Row { Text("- $it") }
            }
        },
        onDismissRequest = {},
        buttons = {
            BasicButton(
                onClick = { alertState.value = false }
            ) {
                Text("Ok")
            }
        }
    )

}

fun workbenchLightColors(
    primary: Color = Color.LightGray,
    primaryVariant: Color = Color.Red,
    secondary: Color = Color.Red,
    secondaryVariant: Color = Color.Red,
    background: Color = Color.DarkGray,
    surface: Color = Color.Red,
    error: Color = Color(0xFFB00020),
    onPrimary: Color = Color.Black,
    onSecondary: Color = Color.Black,
    onBackground: Color = Color.Black,
    onSurface: Color = Color.Black,
    onError: Color = Color.White
): Colors = Colors(
    primary,
    primaryVariant,
    secondary,
    secondaryVariant,
    background,
    surface,
    error,
    onPrimary,
    onSecondary,
    onBackground,
    onSurface,
    onError,
    true
)

fun workBenchShapes() = Shapes(
    CutCornerShape(0.dp),
    CutCornerShape(0.dp),
    CutCornerShape(0.dp)
)
