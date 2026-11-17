package io.github.alxiw.punkapp.presentation.common

import android.view.View
import android.view.Window
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.DialogWindowProvider
import androidx.core.view.WindowCompat

/**
 * A dialog is shown in its own window that does not inherit the system bar appearance of the
 * activity window: the icons fall back to the light ones and disappear on a light background.
 * This aligns the icons of the dialog window with the app background color.
 */
@Composable
internal fun DialogSystemBarIcons() {
    val view = LocalView.current
    val isLightBackground = MaterialTheme.colorScheme.background.luminance() > 0.5f
    SideEffect {
        // No dialog window, e.g. a preview.
        val window = view.findDialogWindow() ?: return@SideEffect
        WindowCompat.getInsetsController(window, view).apply {
            isAppearanceLightStatusBars = isLightBackground
            isAppearanceLightNavigationBars = isLightBackground
        }
    }
}

/** The window of the dialog the view belongs to; the dialog layout is somewhere above the view. */
private fun View.findDialogWindow(): Window? {
    var currentView: View? = this
    while (currentView != null) {
        if (currentView is DialogWindowProvider) return currentView.window
        currentView = currentView.parent as? View
    }
    return null
}
