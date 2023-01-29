package com.swancompany.journal.ui.presentation.homeScreen

import  com.swancompany.journal.R
import android.util.Log
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material3.FloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.swancompany.journal.data.models.NoteModel
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.DismissDirection.EndToStart
import androidx.compose.material.DismissDirection.StartToEnd
import androidx.compose.material.DismissValue.Default
import androidx.compose.material.DismissValue.DismissedToEnd
import androidx.compose.material.DismissValue.DismissedToStart
import androidx.compose.material.Scaffold
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import com.swancompany.journal.ui.theme.JournalTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    onFabClicked: () -> Unit,
    navigateToUpdateNoteScreen: (noteId: Int) -> Unit,
) {
    val viewModel: HomeViewModel = viewModel()
    val notesModel = viewModel.notesModel
    LaunchedEffect(Unit) {
        viewModel.getAllNotes()
    }
    Scaffold(
        topBar = { HomeTopBar() },
        floatingActionButton = {
            FloatingActionButton(onClick = { onFabClicked() }) {
                Icon(Icons.Filled.Add,
                    contentDescription = "add")
            }
        },
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            if (notesModel.isNotEmpty()) {
                items(notesModel) { noteModel ->
                    NoteSwappable(noteModel, viewModel, navigateToUpdateNoteScreen)
                }
            } else {
                item {
                    ShowNoNotes()
                }
            }
        }
    }
}


@ExperimentalMaterialApi
@Composable
fun NoteSwappable(
    noteModel: NoteModel,
    viewModel: HomeViewModel,
    navigateToUpdateNoteScreen: (noteId: Int) -> Unit
    ) {
    val dismissState = rememberDismissState(
        confirmStateChange = {
            if (it == DismissedToEnd)
                viewModel.deleteNote(noteModel)
            it != DismissedToEnd
        }
    )
    SwipeToDismiss(directions = setOf(StartToEnd), state = dismissState, background = {
        val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
        val color by animateColorAsState(
            when (dismissState.targetValue) {
                Default -> Color.LightGray
                DismissedToEnd -> Color.Red
                DismissedToStart -> return@SwipeToDismiss
            }
        )
        val alignment = when (direction) {
            StartToEnd -> Alignment.CenterStart
            EndToStart -> return@SwipeToDismiss
        }
        val scale by animateFloatAsState(
            if (dismissState.targetValue == Default) 0.75f else 1f
        )
        Box(
            Modifier
                .fillMaxSize()
                .background(color)
                .padding(horizontal = 20.dp),
            contentAlignment = alignment
        ) {
            Icon(
                Icons.Default.Delete,
                contentDescription = "",
                modifier = Modifier.scale(scale)
            )
        }
    }, dismissThresholds = {
        androidx.compose.material.FractionalThreshold(0.25f)
    }) {
        NotesCard(noteModel, navigateToUpdateNoteScreen)
    }
}


@Composable
fun ShowNoNotes() {
    Column(modifier = Modifier.fillMaxSize()
        .padding(0.dp,120.dp,0.dp,0.dp)) {
        Image(
            painter = painterResource(id = R.drawable.img),
            contentDescription = "empty",
            modifier = Modifier.fillMaxWidth(),
            alignment = Alignment.Center
        )
        Text(text = "Your notes will show here",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun NotesCard(
    noteModel: NoteModel,
    navigateToUpdateNoteScreen: (noteId: Int) -> Unit
) {
    Card(
        modifier = Modifier
            .heightIn(0.dp ,188.dp)
            .fillMaxWidth()
            .padding(8.dp, 4.dp)
            .clickable {
                navigateToUpdateNoteScreen(noteModel.id)
                Log.i("HomeScreen", "onCardClicked")
            },
        elevation = 4.dp,
        ) {
            Column(modifier = Modifier.fillMaxWidth()
                .padding(12.dp,4.dp)) {
                Text(
                    text = noteModel.title,
                    fontSize = 24.sp
                )
                Text(
                    text = noteModel.notes,
                    Modifier.alpha(.7f),
                    lineHeight = 17.sp
                )
            }
    }
}