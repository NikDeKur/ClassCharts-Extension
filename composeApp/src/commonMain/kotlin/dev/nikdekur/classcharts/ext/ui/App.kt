@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)

package dev.nikdekur.classcharts.ext.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabPosition
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import dev.nikdekur.classcharts.ext.Extension
import dev.nikdekur.classcharts.ext.nav.NavigatorTabs
import dev.nikdekur.classcharts.ext.nav.SimpleNavigator
import dev.nikdekur.classcharts.ext.rememberField
import dev.nikdekur.classcharts.ext.ui.error.ExceptionScreen
import dev.nikdekur.classcharts.ext.ui.lesson.LessonsScreen
import dev.nikdekur.classcharts.ext.ui.lesson.LessonsViewModel
import dev.nikdekur.classcharts.ext.ui.load.CircularLoadingScreen
import dev.nikdekur.classcharts.ext.ui.login.AuthenticationViewModel
import dev.nikdekur.classcharts.ext.ui.login.LoginScreen
import dev.nikdekur.classcharts.ext.ui.login.LoginState
import dev.nikdekur.classcharts.ext.ui.profile.ProfileScreen

@Composable
fun App(extension: Extension, viewModel: AuthenticationViewModel) {
    var selectedTab by rememberField { NavigatorTabs.LESSONS }

    val navigator = SimpleNavigator<NavigatorTabs> { tab ->
        when (tab) {
            NavigatorTabs.LESSONS -> {
                val model = LessonsViewModel(extension)

                val time = extension.time
                val today = time.today()

                LessonsScreen(time, model, today)
            }

//            NavigatorTabs.DETENTIONS -> {
//                val detentionsViewModel = DetentionsViewModel(extension)
//                detentionsViewModel.loadDetentions()
//                DetentionsScreen(detentionsViewModel)
//            }

            NavigatorTabs.PROFILE -> {
                viewModel.loadProfile()
                ProfileScreen(viewModel)
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
        contentColor = MaterialTheme.colorScheme.onBackground,
    ) {

        Scaffold(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(
                    bottom = 8.dp,
                    start = 8.dp,
                    end = 8.dp
                ),
            topBar = {
                if (viewModel.loginState.collectAsState().value !is LoginState.LoggedIn)
                    return@Scaffold

                NavigationTopBar(
                    selectedTab = selectedTab,
                    onTabSelected = { selectedTab = it }
                )
            }
        ) { padding ->

            Surface(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .consumeWindowInsets(padding)
            ) {

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 10.dp)
                ) {

                    val loginState = viewModel.loginState.collectAsState()

                    val screen = try {
                        when (loginState.value) {
                            is LoginState.Loading -> CircularLoadingScreen()
                            is LoginState.LoggedIn -> navigator.getScreen(selectedTab)
                            is LoginState.LoggedOut, is LoginState.Error -> LoginScreen(viewModel)
                        }
                    } catch (e: Throwable) {
                        ExceptionScreen(e)
                    }


                    screen.render()

                    LaunchedEffect(Unit) {
                        viewModel.trySavedLogin()
                    }
                }
            }
        }
    }
}


@Composable
fun NavigatorTabsIndicator(tabs: List<TabPosition>, selected: Int) {
    TabRowDefaults.SecondaryIndicator(
        Modifier.let {
            val current = tabs[selected]
            it.composed(
                inspectorInfo = debugInspectorInfo {
                    name = "tabIndicatorOffset"
                    value = current
                }
            ) {
                val currentTabWidth by animateDpAsState(
                    targetValue = current.width,
                    animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
                )
                val indicatorOffset by animateDpAsState(
                    targetValue = current.left,
                    animationSpec = tween(durationMillis = 250, easing = FastOutSlowInEasing)
                )

                val indicatorWidth = currentTabWidth * 0.75f
                val leftWidth = currentTabWidth - indicatorWidth
                val offset = leftWidth / 2
                val leftStartOffset = indicatorOffset + offset

                fillMaxWidth()
                    .wrapContentSize(Alignment.BottomStart)
                    .offset { IntOffset(x = leftStartOffset.roundToPx(), y = 0) }
                    .width(indicatorWidth)
            }
        },
        color = MaterialTheme.colorScheme.secondary
    )
}

@Composable
fun SelectableTab(
    modifier: Modifier = Modifier,
    tab: NavigatorTabs,
    isSelected: Boolean,
    onSelected: (NavigatorTabs) -> Unit
) {
    Tab(
        modifier = modifier,
        selected = isSelected,
        onClick = { onSelected(tab) },
        text = {
            Text(
                text = tab.name,
                color =
                if (isSelected)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    )
}

@Composable
fun NavigationTopBar(
    modifier: Modifier = Modifier,
    selectedTab: NavigatorTabs,
    onTabSelected: (NavigatorTabs) -> Unit
) {
    var isHovered by rememberField { false }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .requiredHeightIn(min = 32.dp)
            .onPointerEvent(PointerEventType.Enter) { isHovered = true }
            .onPointerEvent(PointerEventType.Exit) { isHovered = false },
        contentAlignment = Alignment.Center
    ) {
        AnimatedVisibility(
            visible = isHovered,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            TabRow(
                selectedTabIndex = selectedTab.ordinal,
                indicator = { tabs -> NavigatorTabsIndicator(tabs, selectedTab.ordinal) },
                modifier = Modifier
                    .fillMaxWidth()
            ) {
                NavigatorTabs.entries.forEach { tab ->
                    val selected = selectedTab == tab
                    SelectableTab(
                        tab = tab,
                        isSelected = selected,
                        onSelected = { onTabSelected(it) }
                    )
                }
            }
        }

        AnimatedVisibility(
            visible = !isHovered,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Icon(
                modifier = Modifier
                    .size(32.dp)
                    .padding(top = 8.dp)
                    .border(2.dp, MaterialTheme.colorScheme.secondary),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "TabRow indicator",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}
