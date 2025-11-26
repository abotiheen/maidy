package com.example.maidy.feature.maidlist.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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
import com.example.maidy.core.model.Maid
import com.example.maidy.ui.theme.*

@Composable
fun MaidListCard(
    maid: Maid,
    onSelectClick: () -> Unit,
    onViewDetailsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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
                
                // Maid Info - Name and Rating vertically stacked, centered with image
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Name
                    Text(
                        text = maid.fullName,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaidListMaidName
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
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Select Button (or Not Available)
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
                            Color(0xFFE0E0E0) // Light gray for disabled
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
                            Color(0xFF9E9E9E) // Gray text for disabled
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
    }
}

/**
 * Returns the background and text colors for a given specialty tag (Maid Class)
 */
@Composable
private fun getSpecialtyTagColors(specialtyTag: String): Pair<Color, Color> {
    return when (specialtyTag) {
        "Gold" -> Pair(
            Color(0xFFFFF9E6),  // Light gold/cream background
            Color(0xFFD4AF37)   // Rich gold text
        )

        "Silver" -> Pair(
            Color(0xFFF5F5F5),  // Light silver/gray background
            Color(0xFF9E9E9E)   // Silver gray text
        )

        "Bronze" -> Pair(
            Color(0xFFFFF3E0),  // Light bronze/peach background
            Color(0xFFCD7F32)   // Bronze text
        )

        else -> Pair(
            Color(0xFFF5F5F5),  // Default light gray
            Color(0xFF757575)   // Default dark gray
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
        Text("Gold Class", fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
            onViewDetailsClick = {}
        )

        Text("Silver Class", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        MaidListCard(
            maid = Maid(
                id = "2",
                fullName = "Sara Mohammed",
                averageRating = 4.5,
                reviewCount = 85,
                specialtyTag = "Silver",
                profileImageUrl = "",
                available = true
            ),
            onSelectClick = {},
            onViewDetailsClick = {}
        )

        Text("Bronze Class", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        MaidListCard(
            maid = Maid(
                id = "3",
                fullName = "Fatima Ali",
                averageRating = 4.2,
                reviewCount = 50,
                specialtyTag = "Bronze",
                profileImageUrl = "",
                available = true
            ),
            onSelectClick = {},
            onViewDetailsClick = {}
        )

        Text("Gold Class - Not Available", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        MaidListCard(
            maid = Maid(
                id = "4",
                fullName = "Aisha Rahman",
                averageRating = 4.8,
                reviewCount = 92,
                specialtyTag = "Gold",
                profileImageUrl = "",
                available = false
            ),
            onSelectClick = {},
            onViewDetailsClick = {}
        )
    }
}

