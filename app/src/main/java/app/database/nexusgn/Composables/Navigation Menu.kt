package app.database.nexusgn.Composables

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.NextWeek
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.database.nexusgn.R
import app.database.nexusgn.ViewModel.NexusGNViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
        /**Refactored for drawer content in [MainView]*/
fun NavigationMain(
    viewModel: NexusGNViewModel,
    closeNavigation:() -> Unit,
    listState: LazyListState
) {

    Column(
        modifier = Modifier.padding(vertical = 15.dp, horizontal = 25.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(15.dp)
    ) {

        Column(
            modifier = Modifier.padding(vertical = 15.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {

            CategoryHeaders(
                viewModel = viewModel,
                header = R.string.Trending,
                navigateTo = {
                    viewModel.navigateToPage(
                        header = viewModel.stringProvider(R.string.Trending),
                        date = viewModel.date.bestRecently()
                    )
                    closeNavigation()
                },
                entryIcon = Icons.Filled.TrendingUp,
                listState = listState
            )

            CategoryHeaders(
                viewModel = viewModel,
                header = R.string.AllGames,
                navigateTo = {
                    viewModel.navigateToPage(
                        header =  viewModel.stringProvider(R.string.AllGames)
                    )
                    closeNavigation()
                },
                entryIcon = Icons.Filled.ClearAll,
                listState = listState
            )

            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){
                NewReleases(
                    viewModel = viewModel,
                    thirtyDays = {
                        viewModel.navigateToPage(
                            header = viewModel.stringProvider(R.string.ThirtyDays),
                            date = viewModel.date.last30Days()
                        )
                        closeNavigation()
                    },
                    thisWeek = {
                        viewModel.navigateToPage(
                            header = viewModel.stringProvider(R.string.ThisWeek),
                            date = viewModel.date.weekSoFar()
                        )
                        closeNavigation()
                    },
                    nextWeek = {
                        viewModel.navigateToPage(
                            header = viewModel.stringProvider(R.string.NextWeek),
                            date = viewModel.date.nextWeek()
                        )
                        closeNavigation()
                    },
                    comingThisYear = {
                        viewModel.navigateToPage(
                            header = viewModel.stringProvider(R.string.Upcoming),
                            date = viewModel.date.comingThisYear()
                        )
                        closeNavigation()
                    },
                    listState = listState
                )

                Top(
                    bestOfYear = {
                        viewModel.navigateToPage(
                            header = "${viewModel.stringProvider(R.string.BestOfYear)} ${viewModel.date.current()}",
                            date = viewModel.date.bestOfThisYear(),
                            ordering = "-metacritic",
                            exclusion = true
                        )
                        closeNavigation()
                    },
                    bestOfLastYear = {
                        viewModel.navigateToPage(
                            header = "${viewModel.stringProvider(R.string.PopularLastYear)} ${viewModel.date.last()}",
                            date = viewModel.date.bestOfLastYear(),
                            ordering = "-metacritic",
                            exclusion = true
                        )
                        closeNavigation()
                    },
                    bestOfAllTime = {
                        viewModel.navigateToPage(
                            header = viewModel.stringProvider(R.string.AllTime),
                            ordering = "-metacritic",
                            exclusion = true,
                        )
                        closeNavigation()
                    },
                    viewModel = viewModel,
                    listState = listState
                )

                Platforms(
                    viewModel = viewModel,
                    windows = {
                        viewModel.navigateToPage(
                            header = viewModel.stringProvider(R.string.Pc),
                            ordering = "-metacritic",
                            platforms = "4"
                        )
                        closeNavigation()
                    },
                    playstation = {
                        viewModel.navigateToPage(
                            header = viewModel.stringProvider(R.string.Playstation4),
                            ordering = "-metacritic",
                            platforms = "18"
                        )
                        closeNavigation()
                    },
                    xbox = {
                        viewModel.navigateToPage(
                            header = viewModel.stringProvider(R.string.XboxOne),
                            ordering = "-metacritic",
                            platforms = "1"
                        )
                        closeNavigation()
                    },
                    nintendo = {
                        viewModel.navigateToPage(
                            header = viewModel.stringProvider(R.string.NintendoSwitch),
                            ordering = "-metacritic",
                            platforms = "7"
                        )
                        closeNavigation()
                    },
                    iOS = {
                        viewModel.navigateToPage(
                            header = viewModel.stringProvider(R.string.iOS),
                            ordering = "-metacritic",
                            platforms = "3"
                        )
                        closeNavigation()
                    },
                    android = {
                        viewModel.navigateToPage(
                            header = viewModel.stringProvider(R.string.Android),
                            ordering = "-metacritic",
                            platforms = "21"
                        )
                        closeNavigation()
                    },
                    listState = listState
                )
            }
        }
    }
}

@Composable
        /**Refactored for [NavigationMain]*/
fun NewReleases(
    thirtyDays:() -> Unit,
    thisWeek:() -> Unit,
    nextWeek:() -> Unit,
    comingThisYear:() -> Unit,
    viewModel: NexusGNViewModel,
    listState: LazyListState
) {
    Column {
        EntryHeaders(header = R.string.NewRelease)
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ){
            SubEntries(
                entryIcon = Icons.Default.Star,
                entryHeaderString = viewModel.stringProvider(R.string.ThirtyDays),
                onClick = { thirtyDays() },
                viewModel = viewModel,
                listState = listState
            )
            SubEntries(
                entryIcon = Icons.Default.CalendarViewWeek,
                entryHeaderString = viewModel.stringProvider(R.string.ThisWeek),
                onClick = { thisWeek() },
                viewModel = viewModel,
                listState = listState
            )
            SubEntries(
                entryIcon = Icons.Default.NextWeek,
                entryHeaderString = viewModel.stringProvider(R.string.NextWeek),
                onClick = { nextWeek() },
                viewModel = viewModel,
                listState = listState
            )
            SubEntries(
                entryIcon = Icons.Default.CalendarMonth,
                entryHeaderString = viewModel.stringProvider(R.string.Upcoming),
                onClick = { comingThisYear() },
                viewModel = viewModel,
                listState = listState
            )
        }
    }
}


@Composable
        /**Refactored for [NavigationMain]*/
fun Top(
    bestOfYear:() -> Unit,
    bestOfLastYear:() -> Unit,
    bestOfAllTime:() -> Unit,
    viewModel: NexusGNViewModel,
    listState: LazyListState
) {
    Column {
        EntryHeaders(header = R.string.Top)
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ){
            SubEntries(
                entryIcon = Icons.Default.EmojiEvents,
                entryHeaderString = "${viewModel.stringProvider(R.string.BestOfYear)} ${viewModel.date.current()}",
                onClick = { bestOfYear() },
                viewModel = viewModel,
                listState = listState
            )
            SubEntries(
                entryIcon = Icons.Default.TrendingUp,
                entryHeaderString = "${viewModel.stringProvider(R.string.PopularLastYear)} ${viewModel.date.last()}",
                onClick = { bestOfLastYear() },
                viewModel = viewModel,
                listState = listState
            )
            SubEntries(
                entryIcon = Icons.Default.MilitaryTech,
                entryHeaderString = viewModel.stringProvider(R.string.AllTime),
                onClick = { bestOfAllTime() },
                viewModel = viewModel,
                listState = listState
            )
        }
    }
}

@Composable
        /**Refactored for [NavigationMain]*/
fun Platforms(
    windows:() -> Unit,
    playstation:() -> Unit,
    xbox:() -> Unit,
    nintendo:() -> Unit,
    iOS:() -> Unit,
    android:() -> Unit,
    viewModel: NexusGNViewModel,
    listState: LazyListState,
) {
    Column {
        EntryHeaders(header = R.string.Platforms)
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ){
            SubEntries(
                entryIcon = ImageVector.vectorResource(id = R.drawable.windows),
                entryHeaderString = viewModel.stringProvider(R.string.Pc),
                onClick = { windows() },
                viewModel = viewModel,
                listState = listState
            )
            SubEntries(
                entryIcon = ImageVector.vectorResource(id = R.drawable.playstation),
                entryHeaderString = viewModel.stringProvider(R.string.Playstation4),
                onClick = { playstation() },
                viewModel = viewModel,
                listState = listState
            )
            SubEntries(
                entryIcon = ImageVector.vectorResource(id = R.drawable.xbox),
                entryHeaderString = viewModel.stringProvider(R.string.XboxOne),
                onClick = { xbox() },
                viewModel = viewModel,
                listState = listState
            )
            SubEntries(
                entryIcon = ImageVector.vectorResource(id = R.drawable.nintendo),
                entryHeaderString = viewModel.stringProvider(R.string.NintendoSwitch),
                onClick = { nintendo() },
                viewModel = viewModel,
                listState = listState
            )
            SubEntries(
                entryIcon = ImageVector.vectorResource(id = R.drawable.apple),
                entryHeaderString = viewModel.stringProvider(R.string.iOS),
                onClick = { iOS() },
                viewModel = viewModel,
                listState = listState
            )
            SubEntries(
                entryIcon = ImageVector.vectorResource(id = R.drawable.android),
                entryHeaderString = viewModel.stringProvider(R.string.Android),
                onClick = { android() },
                viewModel = viewModel,
                listState = listState
            )
        }
    }
}

@Composable
        /**Refactored for [NavigationMain]*/
fun CategoryHeaders(
    header: Int,
    navigateTo:() -> Unit,
    viewModel: NexusGNViewModel,
    entryIcon: ImageVector,
    listState: LazyListState
) {

    val gameUiState by viewModel.uiStateGameDetails.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val animateBackgroundColour = animateColorAsState(
        targetValue = if(gameUiState.pageHeader == viewModel.stringProvider(header))
            MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.secondary,
        label = ""
    )

    val animateTextColour = animateColorAsState(
        targetValue = if(gameUiState.pageHeader == viewModel.stringProvider(header))
            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary, label = ""
    )

    Row(
        modifier = Modifier
            .width(170.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        navigateTo()
                        coroutineScope.launch {
                            delay(250)
                            listState.scrollToItem(0)
                        }
                    }
                )
            },
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.height(35.dp),
            shape = RoundedCornerShape(5.dp),
            colors = CardDefaults.cardColors(animateBackgroundColour.value)
        ){

            Row {
                Icon(
                    modifier = Modifier
                        .padding(5.dp),
                    imageVector = entryIcon,
                    contentDescription = null,
                    tint = animateTextColour.value
                )

                Text(
                    modifier = Modifier
                        .padding(5.dp),
                    text = stringResource(id = header),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = animateTextColour.value
                )
                Spacer(modifier = Modifier.width(3.dp))
            }
        }
    }
}


@Composable
        /**Refactored for [NavigationMain]*/
fun EntryHeaders(
    header: Int,
) {
    Text(
        modifier = Modifier.padding(3.dp),
        text = stringResource(id = header),
        fontSize = 16.sp,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSecondary
    )
}

@Composable
        /**Refactored for [NavigationMain]*/
fun SubEntries(
    entryIcon: ImageVector,
    entryHeaderString: String? = null,
    onClick:() -> Unit,
    listState: LazyListState,
    viewModel: NexusGNViewModel
){
    val gameUiState by viewModel.uiStateGameDetails.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val animateBackgroundColour = animateColorAsState(
        targetValue = if(gameUiState.pageHeader == entryHeaderString)
            MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.background,
        label = ""
    )

    val animateComponentsColour = animateColorAsState(
        targetValue = if(gameUiState.pageHeader == entryHeaderString)
            MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.tertiary, label = ""
    )

    val animateTextBackground = animateColorAsState(
        targetValue = if(gameUiState.pageHeader == entryHeaderString)
            MaterialTheme.colorScheme.background else Color.Transparent, label = ""
    )

    Row(
        modifier = Modifier
            .width(170.dp)
            .pointerInput(Unit) {
                detectTapGestures(
                    onPress = {
                        onClick()
                        coroutineScope.launch {
                            delay(400)
                            listState.scrollToItem(0)
                        }
                    }
                )
            },
        horizontalArrangement = Arrangement.spacedBy(7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Card(
            modifier = Modifier.size(28.dp),
            shape = RoundedCornerShape(5.dp),
            colors = CardDefaults.cardColors(animateBackgroundColour.value)
        ) {
            Icon(
                modifier = Modifier
                    .padding(5.dp)
                    .fillMaxSize(),
                imageVector = entryIcon,
                contentDescription = null,
                tint = animateComponentsColour.value
            )
        }

        Card(
            shape = RoundedCornerShape(5.dp),
            colors = CardDefaults.cardColors(animateTextBackground.value)
        ){
            entryHeaderString?.let {
                Text(
                    modifier = Modifier.padding(5.dp),
                    text = it,
                    color = animateComponentsColour.value,
                    fontSize = 14.sp
                )
            }
        }
    }
}