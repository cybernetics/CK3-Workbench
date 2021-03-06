package com.github.xetra11.ck3workbench.module.character.view

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import com.github.xetra11.ck3workbench.app.StateManager
import com.github.xetra11.ck3workbench.module.character.ui.CharacterList

@Composable
fun CharacterModuleView() {
    val characterState = StateManager.characters
    Row {
        CharacterList(characterState)
    }
}
