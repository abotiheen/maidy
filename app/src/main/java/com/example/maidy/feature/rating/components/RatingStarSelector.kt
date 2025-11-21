package com.example.maidy.feature.rating.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.R
import com.example.maidy.ui.theme.*

@Composable
fun RatingStarSelector(
    selectedRating: Int,
    onRatingSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val ratingLabels = listOf("", "Poor", "Fair", "Good", "Great!", "Excellent!")

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Stars
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (i in 1..5) {
                AnimatedStar(
                    index = i,
                    isSelected = i <= selectedRating,
                    onClick = { onRatingSelected(i) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Rating Label
        androidx.compose.animation.AnimatedVisibility(
            visible = selectedRating > 0,
            enter = androidx.compose.animation.fadeIn() + androidx.compose.animation.expandVertically(),
            exit = androidx.compose.animation.fadeOut() + androidx.compose.animation.shrinkVertically()
        ) {
            Text(
                text = ratingLabels.getOrNull(selectedRating) ?: "",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = RatingMaidNameText
            )
        }
    }
}

@Composable
private fun AnimatedStar(
    index: Int,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Animation state for the bouncy scale effect
    var triggered by remember { mutableStateOf(false) }

    // Scale animation with spring effect
    val scale by animateFloatAsState(
        targetValue = if (triggered) 1.3f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        finishedListener = { triggered = false },
        label = "scale"
    )

    // Rotation animation for extra flair
    val rotation by animateFloatAsState(
        targetValue = if (triggered) 360f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMedium
        ),
        label = "rotation"
    )

    // Color animation
    val color by animateColorAsState(
        targetValue = if (isSelected) RatingStarSelected else RatingStarUnselected,
        animationSpec = tween(300),
        label = "color"
    )

    Icon(
        painter = painterResource(R.drawable.star_special),
        contentDescription = "Star $index",
        tint = color,
        modifier = modifier
            .size(56.dp)
            .scale(scale)
            .graphicsLayer {
                rotationZ = if (triggered) rotation else 0f
            }
            .clickable(
                onClick = {
                    triggered = true
                    onClick()
                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    )
}

@Preview(name = "No Rating", showBackground = true)
@Composable
fun RatingStarSelectorPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            RatingStarSelector(
                selectedRating = 0,
                onRatingSelected = {}
            )
        }
    }
}

@Preview(name = "3 Stars Selected", showBackground = true)
@Composable
fun RatingStarSelectorSelectedPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            RatingStarSelector(
                selectedRating = 3,
                onRatingSelected = {}
            )
        }
    }
}

@Preview(name = "5 Stars Selected", showBackground = true)
@Composable
fun RatingStarSelectorExcellentPreview() {
    MaidyTheme {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            RatingStarSelector(
                selectedRating = 5,
                onRatingSelected = {}
            )
        }
    }
}
