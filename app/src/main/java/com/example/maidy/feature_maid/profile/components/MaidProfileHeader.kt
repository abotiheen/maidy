package com.example.maidy.feature_maid.profile.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
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
import com.example.maidy.feature_maid.auth.MaidAppGreen
import com.example.maidy.feature_maid.auth.MaidAppLightGreen
import com.example.maidy.feature_maid.auth.MaidAppTextPrimary
import com.example.maidy.feature_maid.auth.MaidAppTextSecondary
import com.example.maidy.ui.theme.*

/**
 * Maid profile header component - Green themed
 */
@Composable
fun MaidProfileHeader(
    profileImageUri: String?,
    fullName: String,
    email: String,
    onEditProfileImageClick: () -> Unit,
    isLoading: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(vertical = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Profile Image with Edit Icon
        Box(
            modifier = Modifier.size(120.dp),
            contentAlignment = Alignment.BottomEnd
        ) {
            // Profile Image Circle
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8F5E9)) // Light green background
                    .clickable(onClick = onEditProfileImageClick),
                contentAlignment = Alignment.Center
            ) {
                if (!profileImageUri.isNullOrEmpty()) {
                    AsyncImage(
                        model = profileImageUri,
                        contentDescription = "Profile picture of $fullName",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Profile Image",
                        modifier = Modifier.size(64.dp),
                        tint = MaidAppGreen
                    )
                }

                // Loading indicator overlay
                if (isLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFE8F5E9).copy(alpha = 0.7f)),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaidAppGreen
                        )
                    }
                }
            }

            // Edit Icon Button - Green themed
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(MaidAppGreen)
                    .clickable(onClick = onEditProfileImageClick),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit Profile Image",
                    modifier = Modifier.size(18.dp),
                    tint = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Maid Name
        Text(
            text = fullName,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaidAppTextPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Phone/Email
        Text(
            text = email,
            fontSize = 14.sp,
            color = MaidAppTextSecondary
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun MaidProfileHeaderPreview() {
    MaidProfileHeader(
        profileImageUri = null,
        fullName = "Sarah Johnson",
        email = "+964 770 123 4567",
        onEditProfileImageClick = {}
    )
}
