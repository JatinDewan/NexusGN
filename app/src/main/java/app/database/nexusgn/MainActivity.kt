package app.database.nexusgn

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.ui.platform.LocalConfiguration
import app.database.nexusgn.Composables.MainView
import app.database.nexusgn.ViewModel.NexusGNViewModel
import app.database.nexusgn.ui.theme.NexusGNTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NexusGNTheme {
                val configuration = LocalConfiguration.current
                val viewModel: NexusGNViewModel by viewModels()
                MainView(
                    viewModel = viewModel,
                    configuration = configuration,
                    allGamesApi = viewModel.apiHandler.allGamesApiResponse,
                    screenShots = viewModel.apiHandler.screenshotsApiResponse,
                    gameDetails = viewModel.apiHandler.gameDetailsApiResponse,
                    searchApiResponse = viewModel.apiHandler.searchApiResponse
                )
            }
        }
    }
}
