package com.example.maidy.feature.maidlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.maidy.core.components.ConfirmationDialog
import com.example.maidy.core.model.Maid
import com.example.maidy.ui.theme.*

@Composable
fun MaidListCard(
    maid: Maid,
    onSelectClick: () -> Unit,
    onViewDetailsClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showConfirmationDialog by remember { mutableStateOf(false) }

    ConfirmationDialog(
        isVisible = showConfirmationDialog,
        icon = Icons.Default.Delete,
        iconBackgroundColor = MaidyErrorRed.copy(alpha = 0.1f),
        iconTint = MaidyErrorRed,
        title = "Delete Maid?",
        description = "Are you sure you want to delete ${maid.fullName}? This action cannot be undone.",
        confirmButtonText = "Yes, Delete",
        confirmButtonColor = MaidyErrorRed,
        onConfirm = {
            onDeleteClick()
            showConfirmationDialog = false
        },
        onDismiss = {
            showConfirmationDialog = false
        }
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaidListCardBackground
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        // Wrapper Box to allow absolute positioning for the Delete button
        Box(modifier = Modifier.fillMaxWidth()) {

            // Main Content Column
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Profile Image
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(BookingStatusMapPlaceholder)
                            .border(
                                width = 2.dp,
                                color = MaidListCardBorder,
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (maid.profileImageUrl.isNotEmpty()) {
                            AsyncImage(
                                model = maid.profileImageUrl,
                                contentDescription = "Profile picture of ${maid.fullName}",
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "Maid Profile",
                                tint = BookingStatusServiceLabel,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }

                    // Maid Info - Name and Rating
                    // Note: Removed the delete button from this Row
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Name
                        Text(
                            text = maid.fullName,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaidListMaidName,
                            modifier = Modifier.padding(end = 24.dp) // Extra padding so long names don't hit the delete button
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Rating
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = MaidListStarIcon,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%.1f", maid.averageRating),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaidListMaidRating
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "(${maid.reviewCount})",
                                fontSize = 14.sp,
                                color = MaidListRatingCount
                            )
                        }
                    }
                }

                // Specialty Tag
                if (maid.specialtyTag.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(16.dp))

                    val (backgroundColor, textColor) = getSpecialtyTagColors(maid.specialtyTag)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(backgroundColor)
                                .border(
                                    width = 1.dp,
                                    color = textColor.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = maid.specialtyTag,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = textColor
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Select Button
                    Button(
                        onClick = onSelectClick,
                        enabled = maid.available,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (maid.available) {
                                MaidListSelectButtonBackground
                            } else {
                                Color(0xFFE0E0E0)
                            },
                            disabledContainerColor = Color(0xFFE0E0E0),
                            disabledContentColor = Color(0xFF9E9E9E)
                        )
                    ) {
                        Text(
                            text = if (maid.available) "Select" else "Not Available",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (maid.available) {
                                MaidListSelectButtonText
                            } else {
                                Color(0xFF9E9E9E)
                            }
                        )
                    }

                    // View Details Button
                    OutlinedButton(
                        onClick = onViewDetailsClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.outlinedButtonColors(
                            containerColor = MaidListViewDetailsButtonBackground
                        ),
                        border = null
                    ) {
                        Text(
                            text = "View Details",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaidListViewDetailsButtonText
                        )
                    }
                }
            }

            // --- FLOATING DELETE BUTTON ---
            // Placed here, inside the Box but outside the Column
            IconButton(
                onClick = { showConfirmationDialog = true },
                modifier = Modifier
                    .align(Alignment.TopEnd) // Sticks to top right corner
                    .padding(8.dp) // Slight offset from the very edge
                    .size(40.dp) // Touch target size
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Maid",
                    // Using alpha 0.6f makes it look softer/greyer until clicked
                    tint = MaidyErrorRed.copy(alpha = 0.6f),
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

/**
 * Returns the background and text colors for a given specialty tag (Maid Class)
 */
@Composable
private fun getSpecialtyTagColors(specialtyTag: String): Pair<Color, Color> {
    return when (specialtyTag) {
        "Gold" -> Pair(
            Color(0xFFFFF9E6),
            Color(0xFFD4AF37)
        )

        "Silver" -> Pair(
            Color(0xFFF5F5F5),
            Color(0xFF9E9E9E)
        )

        "Bronze" -> Pair(
            Color(0xFFFFF3E0),
            Color(0xFFCD7F32)
        )

        else -> Pair(
            Color(0xFFF5F5F5),
            Color(0xFF757575)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MaidListCardPreview() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaidListBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Preview logic remains the same...
        MaidListCard(
            maid = Maid(
                id = "1",
                fullName = "Hala Al-Fahad",
                averageRating = 4.9,
                reviewCount = 120,
                specialtyTag = "Gold",
                profileImageUrl = "",
                available = true
            ),
            onSelectClick = {},
            onViewDetailsClick = {},
            onDeleteClick = {}
        )
    }
}