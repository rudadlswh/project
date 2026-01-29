package com.crossfit.app.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.crossfit.app.ui.screens.CalendarScreen
import com.crossfit.app.ui.screens.HomeScreen
import com.crossfit.app.ui.screens.LoginScreen
import com.crossfit.app.ui.screens.NoticeScreen
import com.crossfit.app.ui.screens.ProfileScreen
import com.crossfit.app.ui.screens.RecordScreen
import com.crossfit.app.ui.screens.ReserveScreen
import com.crossfit.app.ui.screens.TodayWodScreen
import com.crossfit.app.ui.screens.AdminToolsScreen
import com.crossfit.app.ui.screens.CreateNoticeScreen
import com.crossfit.app.ui.screens.WodEditScreen
import com.crossfit.app.ui.screens.RecordBulkScreen
import com.crossfit.app.ui.model.UserRole
import com.crossfit.app.ui.model.isAdmin
import com.crossfit.app.ui.model.isStaff
import com.crossfit.app.ui.model.label

sealed class Screen(val route: String, val label: String) {
    data object Login : Screen("login", "로그인")
    data object Home : Screen("home", "홈")
    data object Reserve : Screen("reserve", "예약")
    data object Calendar : Screen("calendar", "캘린더")
    data object Record : Screen("record", "기록")
    data object Notice : Screen("notice", "공지")
    data object Profile : Screen("profile", "프로필")
    data object TodayWod : Screen("today_wod", "오늘 와드")
    data object AdminTools : Screen("admin_tools", "관리자 기능")
    data object CreateNotice : Screen("create_notice", "공지 작성")
    data object WodEdit : Screen("wod_edit", "와드 수정")
    data object RecordBulk : Screen("record_bulk", "기록 일괄 등록")
}

@Composable
fun AppNav() {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route
    val showBottomBar = currentRoute != Screen.Login.route
    val currentUserRole = UserRole.ADMIN
    val roleLabel = currentUserRole.label()
    val isStaff = currentUserRole.isStaff()
    val isAdmin = currentUserRole.isAdmin()

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomBar(navController, isStaff)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Login.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(onLogin = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                })
            }
            composable(Screen.Home.route) {
                HomeScreen(
                    onTodayWodClick = { navController.navigate(Screen.TodayWod.route) },
                    onSettingsClick = { navigateToAdminTools(navController, isAdmin) },
                    onEditWodClick = { navController.navigate(Screen.WodEdit.route) },
                    onCreateNoticeClick = { navController.navigate(Screen.CreateNotice.route) },
                    isStaff = isStaff,
                    headerSubtitle = roleLabel,
                    isAdmin = isAdmin
                )
            }
            composable(Screen.Reserve.route) {
                ReserveScreen(
                    onSettingsClick = { navigateToAdminTools(navController, isAdmin) },
                    headerSubtitle = roleLabel,
                    isStaff = isStaff
                )
            }
            composable(Screen.Calendar.route) {
                CalendarScreen(
                    onSettingsClick = { navigateToAdminTools(navController, isAdmin) },
                    isStaff = isStaff,
                    headerSubtitle = roleLabel
                )
            }
            composable(Screen.Record.route) {
                RecordScreen(
                    onSettingsClick = { navigateToAdminTools(navController, isAdmin) },
                    headerSubtitle = roleLabel,
                    isStaff = isStaff,
                    onBulkRecordClick = { navController.navigate(Screen.RecordBulk.route) }
                )
            }
            composable(Screen.Notice.route) {
                NoticeScreen(
                    onSettingsClick = { navigateToAdminTools(navController, isAdmin) },
                    headerSubtitle = roleLabel,
                    isAdmin = isAdmin,
                    onCreateNoticeClick = { navController.navigate(Screen.CreateNotice.route) }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onSettingsClick = { navigateToAdminTools(navController, isAdmin) },
                    headerSubtitle = roleLabel
                )
            }
            composable(Screen.TodayWod.route) { TodayWodScreen() }
            composable(Screen.AdminTools.route) { AdminToolsScreen() }
            composable(Screen.CreateNotice.route) { CreateNoticeScreen() }
            composable(Screen.WodEdit.route) { WodEditScreen() }
            composable(Screen.RecordBulk.route) { RecordBulkScreen() }
        }
    }
}

private fun navigateToProfile(navController: NavHostController) {
    navController.navigate(Screen.Profile.route) {
        launchSingleTop = true
    }
}

private fun navigateToAdminTools(navController: NavHostController, isAdmin: Boolean) {
    if (isAdmin) {
        navController.navigate(Screen.AdminTools.route) {
            launchSingleTop = true
        }
    } else {
        navigateToProfile(navController)
    }
}

@Composable
private fun BottomBar(navController: NavHostController, isStaff: Boolean) {
    val items = if (isStaff) {
        listOf(
            Screen.Home,
            Screen.Reserve,
            Screen.Record,
            Screen.Notice,
            Screen.Profile
        )
    } else {
        listOf(
            Screen.Home,
            Screen.Reserve,
            Screen.Calendar,
            Screen.Record,
            Screen.Notice,
            Screen.Profile
        )
    }
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 0.dp
    ) {
        items.forEach { screen ->
            val icon = when (screen) {
                Screen.Home -> Icons.Default.Home
                Screen.Reserve -> Icons.Default.Event
                Screen.Calendar -> Icons.Default.CalendarToday
                Screen.Record -> Icons.Default.EmojiEvents
                Screen.Notice -> Icons.Default.NotificationsNone
                Screen.Profile -> Icons.Default.Person
                else -> Icons.Default.Home
            }
            NavigationBarItem(
                selected = currentRoute == screen.route,
                onClick = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(icon, contentDescription = screen.label) },
                label = null,
                colors = NavigationBarItemDefaults.colors(
                    indicatorColor = Color(0xFF111827),
                    selectedIconColor = Color.White,
                    unselectedIconColor = Color(0xFF9CA3AF)
                )
            )
        }
    }
}
