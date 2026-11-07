package com.kritam.mynotes

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.kritam.mynotes.data.NoteDatabase
import com.kritam.mynotes.repository.NoteRepository
import com.kritam.mynotes.ui.component.NoteItem
import com.kritam.mynotes.ui.screen.AddEditNoteScreen
import com.kritam.mynotes.ui.screen.NoteListScreen
import com.kritam.mynotes.ui.theme.MyNotesTheme
import com.kritam.mynotes.ui.viewmodel.NoteViewModel
import com.kritam.mynotes.ui.viewmodel.NoteViewModelFactory

class MainActivity : ComponentActivity() {

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { }

    override fun onCreate(savedInstanceState: android.os.Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }

        setContent {
            val context = LocalContext.current
            val database = remember { NoteDatabase.getDatabase(context) }
            val repository = remember { NoteRepository(database.noteDao()) }
            val viewModel: NoteViewModel = viewModel(factory = NoteViewModelFactory(repository))
            val navController = rememberNavController()

            MyNotesTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = "note_list"
                    ) {
                        composable("note_list") {
                            NoteListScreen(
                                viewModel = viewModel,
                                navController = navController
                            )
                        }

                        composable(
                            route = "add_edit_note?noteId={noteId}",
                            arguments = listOf(
                                navArgument("noteId") {
                                    type = NavType.IntType
                                    defaultValue = -1
                                }
                            )
                        ) { backStackEntry ->
                            val noteId = backStackEntry.arguments?.getInt("noteId") ?: -1
                            AddEditNoteScreen(
                                noteId = noteId,
                                viewModel = viewModel,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}
