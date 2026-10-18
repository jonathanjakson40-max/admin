package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.NavTab
import com.example.ui.MultiplicationViewModel
import com.example.ui.screens.LearnScreen
import com.example.ui.screens.MasteryStatsScreen
import com.example.ui.screens.PracticeScreen
import com.example.ui.screens.PythagoreanGridScreen
import com.example.ui.screens.VisualArrayScreen
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.MultiplyMasterTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MultiplyMasterTheme {
                MainAppScreen()
            }
        }
    }
}

@Composable
fun MainAppScreen(
    viewModel: MultiplicationViewModel = viewModel()
) {
    val currentTab by viewModel.currentTab.collectAsState()
    val quizState by viewModel.quizState.collectAsState()

    // BackHandler: Return to LEARN tab if on a secondary tab
    BackHandler(enabled = currentTab != NavTab.LEARN && !quizState.isActive) {
        viewModel.setTab(NavTab.LEARN)
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            // Hide bottom bar when a duel or active quiz is running to maximize screen space
            if (!quizState.isActive) {
                NavigationBar(
                    modifier = Modifier
                        .testTag("main_navigation_bar")
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 4.dp
                ) {
                    NavigationBarItem(
                        selected = currentTab == NavTab.LEARN,
                        onClick = { viewModel.setTab(NavTab.LEARN) },
                        icon = { Icon(Icons.Default.MenuBook, contentDescription = "Learn Tables") },
                        label = { Text("Tables", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_item_learn")
                    )

                    NavigationBarItem(
                        selected = currentTab == NavTab.PRACTICE,
                        onClick = { viewModel.setTab(NavTab.PRACTICE) },
                        icon = { Icon(Icons.Default.Bolt, contentDescription = "Practice") },
                        label = { Text("Practice", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_item_practice")
                    )

                    NavigationBarItem(
                        selected = currentTab == NavTab.VISUAL,
                        onClick = { viewModel.setTab(NavTab.VISUAL) },
                        icon = { Icon(Icons.Default.GridOn, contentDescription = "Visual Array") },
                        label = { Text("Visual", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_item_visual")
                    )

                    NavigationBarItem(
                        selected = currentTab == NavTab.PYTHAGORAS,
                        onClick = { viewModel.setTab(NavTab.PYTHAGORAS) },
                        icon = { Icon(Icons.Default.TableChart, contentDescription = "Pythagoras Grid") },
                        label = { Text("Pythagoras", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_item_pythagoras")
                    )

                    NavigationBarItem(
                        selected = currentTab == NavTab.MASTERY,
                        onClick = { viewModel.setTab(NavTab.MASTERY) },
                        icon = { Icon(Icons.Default.EmojiEvents, contentDescription = "Mastery & Tips") },
                        label = { Text("Mastery", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = IndigoPrimary,
                            selectedTextColor = IndigoPrimary,
                            indicatorColor = MaterialTheme.colorScheme.primaryContainer
                        ),
                        modifier = Modifier.testTag("nav_item_mastery")
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentTab) {
                NavTab.LEARN -> LearnScreen(viewModel = viewModel)
                NavTab.PRACTICE -> PracticeScreen(viewModel = viewModel)
                NavTab.VISUAL -> VisualArrayScreen(viewModel = viewModel)
                NavTab.PYTHAGORAS -> PythagoreanGridScreen(viewModel = viewModel)
                NavTab.MASTERY -> MasteryStatsScreen(viewModel = viewModel)
            }
        }
    }
}
