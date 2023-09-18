package app.database.nexusgn.Composables.Components

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalMinimumInteractiveComponentEnforcement
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.database.nexusgn.Data.Api.GameDetails
import app.database.nexusgn.Data.Api.Platform
import app.database.nexusgn.Data.Api.Platforms
import app.database.nexusgn.R
import app.database.nexusgn.ViewModel.NexusGNViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlatformsTabs(
    list: List<Platforms>,
    viewModel: NexusGNViewModel
){
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Headers(text = viewModel.stringProvider(R.string.Platforms))
        FlowRow(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            list.forEach { game ->
                game.platform?.name?.let { platformName -> IndividualTags( name = platformName) }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GenreTabs(
    list: List<Platform>,
    viewModel: NexusGNViewModel
){
    if(list.isNotEmpty()){
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Headers(text = viewModel.stringProvider(R.string.genre))

            FlowRow(
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                list.forEach { name -> IndividualTags(name = name.name) }
            }
        }
    }
}

@Composable
fun Rating(
    rating: String,
    viewModel: NexusGNViewModel
){
    Column(
        verticalArrangement = Arrangement.spacedBy(5.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Headers(text = viewModel.stringProvider(R.string.Rating))
        IndividualTags(
            name = rating,
            colour = MaterialTheme.colorScheme.onSecondary,
            textColour = MaterialTheme.colorScheme.secondary,
            padding = 5.dp
        )
    }
}


@Composable
fun ReleaseDates(
    date: String,
    viewModel: NexusGNViewModel
){
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp),
    ) {

        Headers(text = viewModel.stringProvider(R.string.releaseDate))
        IndividualTags(
            name = date,
            colour = MaterialTheme.colorScheme.onSecondary,
            textColour = MaterialTheme.colorScheme.secondary,
            padding = 5.dp
        )
    }
}

@Composable
fun Metacritic(
    score: Int,
    colour: Color,
    modifier: Modifier = Modifier
){
    Card(
        modifier = Modifier
            .padding(15.dp)
            .size(35.dp),
        colors = CardDefaults.cardColors(colour),
        shape = RoundedCornerShape(5.dp),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ){
            Text(
                text = score.toString(),
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.tertiary,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AllLinks(
    gameDetails: GameDetails,
    viewModel: NexusGNViewModel,
    context: Context
){
    val isLinkAvailable =
        !gameDetails.website.isNullOrEmpty() ||
        !gameDetails.redditUrl.isNullOrEmpty() ||
        !gameDetails.metacriticUrl.isNullOrEmpty()

    if(isLinkAvailable){
        Column(
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {

            Headers(text = viewModel.stringProvider(R.string.Relevant_links))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                if (!gameDetails.website.isNullOrEmpty()) {
                    Link(
                        backgroundColour = Color.DarkGray,
                        icon = R.drawable.web,
                        tint = MaterialTheme.colorScheme.tertiary,
                        text = viewModel.stringProvider(R.string.Website),
                        textColour = MaterialTheme.colorScheme.tertiary,
                        onClick = {
                            viewModel.usefulLink(context, gameDetails.website)
                        }
                    )
                }

                if (!gameDetails.redditUrl.isNullOrEmpty()) {
                    Link(
                        backgroundColour = Color(0xFFFF4500),
                        icon = R.drawable.reddit_2,
                        tint = Color.Unspecified,
                        text = viewModel.stringProvider(R.string.Reddit),
                        textColour = Color(0xFFffffff),
                        onClick = {
                            viewModel.usefulLink(context, gameDetails.redditUrl)
                        }
                    )
                }

                if (!gameDetails.metacriticUrl.isNullOrEmpty()) {
                    Link(
                        backgroundColour = Color(0xFF333333),
                        icon = R.drawable.metacritic,
                        tint = Color.Unspecified,
                        text = viewModel.stringProvider(R.string.Metacritic),
                        textColour = Color(0xFFffcc34),
                        onClick = {
                            viewModel.usefulLink(context, gameDetails.metacriticUrl)
                        }
                    )
                }
            }
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Link(
    backgroundColour: Color,
    icon: Int,
    tint: Color,
    text: String,
    textColour: Color,
    onClick:() -> Unit
){
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentEnforcement provides false,
    ){
        Card(
            modifier = Modifier.padding(top = 1.dp, end = 1.dp, bottom = 5.dp),
            colors = CardDefaults.cardColors(backgroundColour),
            shape = RoundedCornerShape(50.dp),
            onClick = { onClick() }
        ) {
            Row(
                modifier = Modifier.padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Card(
                    shape = RoundedCornerShape(50.dp),
                    elevation = CardDefaults.elevatedCardElevation(10.dp),
                    colors = CardDefaults.cardColors(Color.Transparent)
                ) {
                    Icon(
                        modifier = Modifier.size(16.dp),
                        imageVector = ImageVector.vectorResource(icon),
                        contentDescription = "",
                        tint = tint
                    )
                }
                Text(
                    modifier = Modifier.padding(end = 4.dp),
                    text = text,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = textColour
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IndividualTags(
    name: String,
    colour: Color = MaterialTheme.colorScheme.primary,
    textColour : Color = MaterialTheme.colorScheme.secondary,
    padding: Dp = 1.dp,
    fontWeight: FontWeight = FontWeight.W600
){
    CompositionLocalProvider(
        LocalMinimumInteractiveComponentEnforcement provides false,
    ){
        Card(
            modifier = Modifier.padding(padding),
            colors = CardDefaults.cardColors(colour),
            shape = RoundedCornerShape(5.dp)
        ) {
            Text(
                modifier = Modifier.padding(vertical = 3.dp, horizontal = 6.dp),
                text = name,
                color = textColour,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                fontWeight = fontWeight
            )
        }
    }
}


@Composable
fun Headers(
    text: String,
    padding: Dp = 0.dp,
    fontSize: TextUnit = 15.sp
){
    Text(
        modifier = Modifier.padding(horizontal = padding),
        text = text,
        fontSize = fontSize,
        color = MaterialTheme.colorScheme.onSecondary,
        fontWeight = FontWeight.Bold
    )
}