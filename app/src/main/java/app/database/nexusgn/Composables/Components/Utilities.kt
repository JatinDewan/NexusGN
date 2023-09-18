package app.database.nexusgn.Composables.Components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.database.nexusgn.Data.Api.GameInformation
import app.database.nexusgn.Data.Utilities.rangeFinder
import app.database.nexusgn.R
import app.database.nexusgn.ViewModel.NexusGNViewModel

@Composable
fun LimitedTextView(
    text: String,
    maxWords: Int,
) {
    val words = text.split(" ")
    val limitedText = if (words.size > maxWords) {
        val limitedWords = words.take(maxWords)
        val limitedText = limitedWords.joinToString(" ")
        "$limitedText..."
    } else {
        text
    }

    Text(
        text = AnnotatedString(limitedText),
        fontSize = 12.sp,
        color = MaterialTheme.colorScheme.tertiary,
        lineHeight = 16.sp,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun Tags(
    viewModel: NexusGNViewModel,
    genre: GameInformation,
    textSize: Int,
    showAll: Boolean = false,
){
    val animateState by animateDpAsState(
        targetValue = if(showAll) 35.dp else 20.dp,
        animationSpec = tween(300),
        label = ""
    )

    AnimatedContent(
        targetState = showAll,
        transitionSpec = {
            slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Up, tween(300)) togetherWith fadeOut(
                tween(100)
            )
        },
        label = ""
    ) { animateThis ->
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(2.dp),
            horizontalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            val rememberTotal = remember {
                derivedStateOf { viewModel.totalLength(genre) }
            }

            val anotherState = remember {
                derivedStateOf{ viewModel.totalNotShown(genre, rememberTotal.value) }
            }

            genre.genres?.rangeFinder(rememberTotal.value, animateThis)?.forEach { text ->
                Card(
                    modifier = Modifier
                        .heightIn(max = animateState)
                        .padding(top = 2.dp),
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.onSecondary
                    ),
                    shape = RoundedCornerShape(5.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    text.name?.let { name ->
                        Text(
                            modifier = Modifier.padding(vertical = 3.dp, horizontal = 6.dp),
                            text = name,
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = textSize.sp,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            AnimatedVisibility(visible = anotherState.value > 0 && !showAll){
                Card(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(top = 2.dp),
                    colors = CardDefaults.cardColors(
                        MaterialTheme.colorScheme.primary
                    ),
                    shape = RoundedCornerShape(5.dp),
                    elevation = CardDefaults.cardElevation(10.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ){
                        Text(
                            modifier = Modifier.padding(1.dp),
                            text = "+${anotherState.value}",
                            color = MaterialTheme.colorScheme.secondary,
                            fontSize = textSize.sp,
                            letterSpacing = 0.2.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TagsSearch(
    genre: GameInformation,
    textSize: Int,
){
    FlowRow(
        verticalArrangement = Arrangement.spacedBy(2.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        genre.genres?.forEach { text ->
            Card(
                modifier = Modifier
                    .heightIn(max = 45.dp)
                    .padding(end = 2.dp, top = 2.dp),
                colors = CardDefaults.cardColors(
                    MaterialTheme.colorScheme.onTertiary
                ),
                shape = RoundedCornerShape(5.dp),
            ) {
                text.name?.let {
                    Text(
                        modifier = Modifier
                            .padding(vertical = 3.dp, horizontal = 6.dp),
                        text = it,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontSize = textSize.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
fun NoGamesFound(viewModel: NexusGNViewModel){
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(100.dp))
        Text(
            text = viewModel.stringProvider(R.string.NotFound),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 100.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = viewModel.stringProvider(R.string.NoResults),
            color = MaterialTheme.colorScheme.onTertiary,
            fontSize = 20.sp,
        )
    }
}

@Composable
fun LoadingPage(
    paddingValues: PaddingValues = PaddingValues(top = 90.dp)
){
    Column(
        modifier = Modifier
            .padding(paddingValues)
            .fillMaxWidth(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .padding(10.dp)
                .size(50.dp),
            color = MaterialTheme.colorScheme.tertiary,
            trackColor = MaterialTheme.colorScheme.primary
        )
    }
}