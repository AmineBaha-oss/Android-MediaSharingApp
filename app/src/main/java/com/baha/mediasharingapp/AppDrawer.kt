package com.baha.mediasharingapp

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.baha.mediasharingapp.viewmodel.UserViewModel

@Composable
fun AppDrawer(
    navController: NavController,
    userViewModel: UserViewModel,
    currentRoute: String,
    closeDrawer: () -> Unit,
    onLogout: () -> Unit
) {
    val user = userViewModel.currentUser.value

    Column(
        modifier = Modifier
            .fillMaxHeight()
            .width(300.dp)
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 48.dp)
    ) {
        // User info header
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile picture
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = user?.username?.firstOrNull()?.uppercase() ?: "U",
                    color = MaterialTheme.colorScheme.onPrimary,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Username
            Text(
                text = user?.username ?: "User",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            // Email
            Text(
                text = user?.email ?: "Email",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Divider(modifier = Modifier.padding(vertical = 16.dp))

        // Navigation items
        DrawerItem(
            icon = Icons.Default.Home,
            label = "Feed",
            isSelected = currentRoute == "feed",
            onClick = {
                navController.navigate("feed") {
                    launchSingleTop = true
                }
                closeDrawer()
            }
        )

        DrawerItem(
            icon = Icons.Default.Add,
            label = "Create Post",
            isSelected = currentRoute == "post",
            onClick = {
                navController.navigate("post")
                closeDrawer()
            }
        )

        DrawerItem(
            icon = Icons.Default.Person,
            label = "Profile",
            isSelected = currentRoute == "profile",
            onClick = {
                navController.navigate("profile")
                closeDrawer()
            }
        )

        DrawerItem(
            icon = Icons.Default.Map,
            label = "Map View",
            isSelected = currentRoute == "map",
            onClick = {
                navController.navigate("map")
                closeDrawer()
            }
        )

        Spacer(modifier = Modifier.weight(1f))

        // Logout button
        DrawerItem(
            icon = Icons.Default.Logout,
            label = "Logout",
            isSelected = false,
            onClick = {
                onLogout()
                closeDrawer()
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun DrawerItem(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.background
    }

    val contentColor = if (isSelected) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.onSurface
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .background(backgroundColor)
            .padding(horizontal = 24.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = contentColor
        )

        Spacer(modifier = Modifier.width(24.dp))

        Text(
            text = label,
            color = contentColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}