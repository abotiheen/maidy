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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.maidy.feature.maidlist.MaidProfile
import com.example.maidy.feature.maidlist.ServiceTag
import com.example.maidy.ui.theme.*

@Composable
fun MaidListCard(
    maidProfile: MaidProfile,
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
                // Profile Image Placeholder
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
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Maid Profile",
                        tint = BookingStatusServiceLabel,
                        modifier = Modifier.size(48.dp)
                    )
                }
                
                // Maid Info - Name and Rating vertically stacked, centered with image
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.Center
                ) {
                    // Name
                    Text(
                        text = maidProfile.name,
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
                            text = String.format("%.1f", maidProfile.rating),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaidListMaidRating
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "(${maidProfile.reviewCount})",
                            fontSize = 14.sp,
                            color = MaidListRatingCount
                        )
                    }
                }
            }
            
            // Service Tags/Chips
            if (maidProfile.services.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                // Simple row layout for service chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    maidProfile.services.take(3).forEach { service ->
                        ServiceChip(serviceTag = service)
                    }
                    if (maidProfile.services.size > 3) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaidListServiceChipDeepCleaningBg)
                                .border(
                                    width = 1.dp,
                                    color = MaidListServiceChipDeepCleaningText.copy(alpha = 0.3f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+${maidProfile.services.size - 3}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaidListServiceChipDeepCleaningText
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Select Button
                Button(
                    onClick = onSelectClick,
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaidListSelectButtonBackground
                    )
                ) {
                    Text(
                        text = "Select",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaidListSelectButtonText
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
        Text("Single Service", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        MaidListCard(
            maidProfile = MaidProfile(
                id = "1",
                name = "Maria S.",
                rating = 4.9f,
                reviewCount = 120,
                services = listOf(ServiceTag.DEEP_CLEANING)
            ),
            onSelectClick = {},
            onViewDetailsClick = {}
        )
        
        Text("Multiple Services", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        MaidListCard(
            maidProfile = MaidProfile(
                id = "2",
                name = "Sophie M.",
                rating = 4.8f,
                reviewCount = 103,
                services = listOf(
                    ServiceTag.PET_FRIENDLY,
                    ServiceTag.ECO_FRIENDLY,
                    ServiceTag.LAUNDRY
                )
            ),
            onSelectClick = {},
            onViewDetailsClick = {}
        )
        
        Text("No Services", fontWeight = FontWeight.Bold, fontSize = 16.sp)
        MaidListCard(
            maidProfile = MaidProfile(
                id = "3",
                name = "Isabella R.",
                rating = 4.7f,
                reviewCount = 88,
                services = emptyList()
            ),
            onSelectClick = {},
            onViewDetailsClick = {}
        )
    }
}

