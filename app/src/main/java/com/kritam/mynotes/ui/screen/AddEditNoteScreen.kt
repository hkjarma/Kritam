package com.kritam.mynotes.ui.screen

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.kritam.mynotes.data.Note
import com.kritam.mynotes.ui.component.formatDateTime
import com.kritam.mynotes.ui.viewmodel.NoteViewModel
import com.kritam.mynotes.work.ReminderWorker
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    noteId: Int,
    viewModel: NoteViewModel,
    navController: NavController
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val existingNote = remember(uiState.notes, noteId) {
        uiState.notes.find { it.id == noteId }
    }

    var title by remember(existingNote) { mutableStateOf(existingNote?.title ?: "") }
    var description by remember(existingNote) { mutableStateOf(existingNote?.description ?: "") }
    var dueDateMillis by remember(existingNote) {
        mutableLongStateOf(existingNote?.dueDate ?: System.currentTimeMillis() + 60 * 60 * 1000)
    }

    val calendar = remember(dueDateMillis) {
        Calendar.getInstance().apply { timeInMillis = dueDateMillis }
    }

    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }

    val datePickerDialog = remember(context) {
        DatePickerDialog(
            context,
            { _, year, month, dayOfMonth ->
                val updatedCalendar = Calendar.getInstance().apply {
                    timeInMillis = dueDateMillis
                    set(Calendar.YEAR, year)
                    set(Calendar.MONTH, month)
                    set(Calendar.DAY_OF_MONTH, dayOfMonth)
                }
                dueDateMillis = updatedCalendar.timeInMillis
                showDatePicker = false
                showTimePicker = true
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).apply {
            setOnCancelListener { showDatePicker = false }
        }
    }

    LaunchedEffect(showDatePicker) {
        if (showDatePicker) {
            datePickerDialog.show()
        } else {
            datePickerDialog.dismiss()
        }
    }

    val timePickerDialog = remember(context) {
        TimePickerDialog(
            context,
            { _, hourOfDay, minute ->
                val updatedCalendar = Calendar.getInstance().apply {
                    timeInMillis = dueDateMillis
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }
                dueDateMillis = updatedCalendar.timeInMillis
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).apply {
            setOnCancelListener { showTimePicker = false }
        }
    }

    LaunchedEffect(showTimePicker) {
        if (showTimePicker) {
            timePickerDialog.show()
        } else {
            timePickerDialog.dismiss()
        }
    }

    fun saveNote() {
        val trimmedTitle = title.trim()
        if (trimmedTitle.isEmpty()) return

        val noteToSave = Note(
            id = existingNote?.id ?: 0,
            title = trimmedTitle,
            description = description.trim(),
            createdAt = existingNote?.createdAt ?: System.currentTimeMillis(),
            dueDate = dueDateMillis,
            isDone = existingNote?.isDone ?: false,
            doneAt = existingNote?.doneAt
        )

        viewModel.saveNote(noteToSave)

        if (dueDateMillis > System.currentTimeMillis()) {
            val reminderRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(dueDateMillis - System.currentTimeMillis())
                .setInputData(
                    workDataOf(
                        "note_title" to noteToSave.title,
                        "note_message" to noteToSave.description.ifBlank { "Reminder" },
                        "note_id" to noteToSave.id
                    )
                )
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                "reminder_${noteToSave.id}",
                ExistingWorkPolicy.REPLACE,
                reminderRequest
            )
        }

        navController.popBackStack()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = if (noteId > 0) "Edit Note" else "Add Note") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Title") },
                singleLine = true,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Description") },
                minLines = 6,
                keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Reminder",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = formatDateTime(dueDateMillis),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = { showDatePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Pick Date")
                        }
                        Button(
                            onClick = { showTimePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Pick Time")
                        }
                    }
                }
            }

            Button(
                onClick = { saveNote() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = if (noteId > 0) "Save Changes" else "Create Note")
            }
        }
    }
}
