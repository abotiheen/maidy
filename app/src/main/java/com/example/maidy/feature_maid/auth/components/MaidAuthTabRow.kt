package com.example.maidy.feature_maid.auth.components

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature_maid.auth.*
import com.example.maidy.ui.theme.*

/**
 * Tab row for switching between Login and Register with sliding indicator animation - Green themed with more rounded corners
 *
 * @param selectedTab Index of currently selected tab (0 = Login, 1 = Register)
 * @param onTabSelected Callback when a tab is selected
 * @param modifier Modifier to be applied to the tab row
 */
@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun MaidAuthTabRow(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)) // More rounded than customer app (12dp)
            .background(MaidAppTabBackground)
            .padding(4.dp)
    ) {
        val tabWidth = (maxWidth - 4.dp) / 2

        // Animated indicator offset
        val indicatorOffset by animateDpAsState(
            targetValue = if (selectedTab == 0) 0.dp else tabWidth + 4.dp,
            animationSpec = tween(durationMillis = 300),
            label = "tab_indicator"
        )

        // Sliding white background indicator
        Box(
            modifier = Modifier
                .offset(x = indicatorOffset)
                .width(tabWidth)
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp)) // Inner rounded corners
                .background(MaidAppTabSelected)
        )

        // Tab content
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            MaidTabItem(
                text = "Login",
                isSelected = selectedTab == 0,
                onClick = { onTabSelected(0) },
                modifier = Modifier.weight(1f)
            )
            MaidTabItem(
                text = "Register",
                isSelected = selectedTab == 1,
                onClick = { onTabSelected(1) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun MaidTabItem(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .height(44.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(
                onClick = onClick,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
            color = if (isSelected) MaidAppTextPrimary else MaidAppTextSecondary
        )
    }
}

@Preview(name = "Login Selected", showBackground = true)
@Composable
private fun MaidAuthTabRowLoginPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            MaidAuthTabRow(selectedTab = 0, onTabSelected = {})
        }
    }
}

@Preview(name = "Register Selected", showBackground = true)
@Composable
private fun MaidAuthTabRowRegisterPreview() {
    MaidyTheme {
        Column(Modifier.padding(16.dp)) {
            MaidAuthTabRow(selectedTab = 1, onTabSelected = {})
        }
    }
}
