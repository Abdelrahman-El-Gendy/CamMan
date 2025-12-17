package com.gndy.camman.presentation.screens.review

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.gndy.camman.domain.model.Review

/**
 * Dialog for photographers to respond to a review
 */
@Composable
fun PhotographerReviewResponseDialog(
    review: Review,
    onDismiss: () -> Unit,
    onSubmitResponse: (String) -> Unit,
    isSubmitting: Boolean = false
) {
    var responseText by remember { mutableStateOf("") }
    val maxLength = Review.MAX_REVIEW_TEXT_LENGTH

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Respond to Review",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                // Review Summary
                ReviewSummaryCard(review = review)

                Spacer(modifier = Modifier.height(16.dp))

                // Response Input
                Text(
                    text = "Your Response",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = responseText,
                    onValueChange = {
                        if (it.length <= maxLength) {
                            responseText = it
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = { Text("Thank the client and address any concerns professionally...") },
                    shape = RoundedCornerShape(12.dp),
                    supportingText = {
                        Text(
                            text = "${responseText.length}/$maxLength",
                            textAlign = TextAlign.End,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Tips
                Text(
                    text = "Tips: Be professional and courteous. Thank the client for their feedback and address any specific concerns mentioned.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmitResponse(responseText) },
                enabled = responseText.isNotBlank() && !isSubmitting
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Submit Response")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun ReviewSummaryCard(review: Review) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp)
    ) {
        // Rating and client info
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Stars
            repeat(5) { index ->
                Icon(
                    Icons.Filled.Star,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = if (index < review.rating) {
                        Color(0xFFFFB800)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                    }
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Text(
                text = "by ${if (review.isAnonymous) "Anonymous" else (review.clientName ?: "Guest")}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // Review text preview
        if (review.reviewText.isNotEmpty()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = review.reviewText,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 3
            )
        }
    }
}

/**
 * Dialog for confirming review deletion or hiding
 */
@Composable
fun ReviewActionConfirmDialog(
    action: ReviewAction,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = when (action) {
                    ReviewAction.HIDE -> "Hide Review"
                    ReviewAction.FLAG -> "Flag Review"
                    ReviewAction.DELETE -> "Delete Review"
                },
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Text(
                text = when (action) {
                    ReviewAction.HIDE -> "This review will be hidden from public view. Are you sure?"
                    ReviewAction.FLAG -> "This review will be flagged for admin review. Are you sure?"
                    ReviewAction.DELETE -> "This action cannot be undone. Are you sure you want to delete this review?"
                }
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = if (action == ReviewAction.DELETE) {
                    androidx.compose.material3.ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                } else {
                    androidx.compose.material3.ButtonDefaults.buttonColors()
                }
            ) {
                Text(
                    when (action) {
                        ReviewAction.HIDE -> "Hide"
                        ReviewAction.FLAG -> "Flag"
                        ReviewAction.DELETE -> "Delete"
                    }
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

enum class ReviewAction {
    HIDE,
    FLAG,
    DELETE
}
