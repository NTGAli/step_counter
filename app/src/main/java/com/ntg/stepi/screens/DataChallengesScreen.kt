package com.ntg.stepi.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.ntg.stepi.R
import com.ntg.stepi.components.Appbar
import com.ntg.stepi.vm.StepViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DataChallengesScreen(
    navHostController: NavHostController,
    stepViewModel: StepViewModel,
    uid: String
) {

    val tabData = listOf(
        stringResource(id = R.string.participents),
        stringResource(id = R.string.winners),
    )

    val pagerState = rememberPagerState { 2 }

    val tabIndex = pagerState.currentPage
    val coroutineScope = rememberCoroutineScope()


    Scaffold(
        topBar = {

            Column {
                Appbar(
                    title = stringResource(id = R.string.challenge_monthly),
                    enableNavigation = true,
                    navigationOnClick = {
                        navHostController.popBackStack()
                    })

                TabRow(
                    selectedTabIndex = tabIndex,
                    backgroundColor = MaterialTheme.colors.background,
                    contentColor = MaterialTheme.colors.primary
                ) {
                    tabData.forEachIndexed { index, title ->
                        Tab(
                            modifier = Modifier.clip(RoundedCornerShape(8.dp)),
                            text = { Text(title) },
                            selected = tabIndex == index,
                            onClick = {
                                coroutineScope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            selectedContentColor = MaterialTheme.colors.primary,
                            unselectedContentColor = MaterialTheme.colors.secondary,
                        )
                    }
                }
            }
        },
        content = { paddingValues ->
            // PAGER
            HorizontalPager(
                modifier = Modifier.padding(paddingValues),
                state = pagerState
            ) { index ->
                Column(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    if (tabData[index] == stringResource(id = R.string.participents)) {
                        ParticipatesScreen(navController = navHostController, stepViewModel, uid)
                    } else {
                        WinnersScreen(navController = navHostController, stepViewModel)
                    }
                }
            }
        }
    )

}