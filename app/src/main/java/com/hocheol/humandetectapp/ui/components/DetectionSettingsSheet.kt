package com.hocheol.humandetectapp.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.hocheol.humandetectapp.R
import com.hocheol.humandetectapp.model.DelegateType
import com.hocheol.humandetectapp.model.ModelType
import com.hocheol.humandetectapp.ui.viewmodel.CameraViewModel

@Composable
fun DetectionSettingsSheet(
    viewModel: CameraViewModel
) {
    val detectionConfig = viewModel.detectionConfig

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Inference Time",
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = "${viewModel.inferenceTime}ms",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        AdjustmentRow(
            label = "Confidence Threshold",
            value = "%.2f".format(detectionConfig.threshold),
            onMinusClick = { viewModel.updateThreshold((detectionConfig.threshold - 0.1f).coerceAtLeast(0.1f)) },
            onPlusClick = { viewModel.updateThreshold((detectionConfig.threshold + 0.1f).coerceAtMost(1.0f)) }
        )

        AdjustmentRow(
            label = "Max Results",
            value = detectionConfig.maxResults.toString(),
            onMinusClick = { viewModel.updateMaxResults((detectionConfig.maxResults - 1).coerceAtLeast(1)) },
            onPlusClick = { viewModel.updateMaxResults((detectionConfig.maxResults + 1).coerceAtMost(10)) }
        )

        AdjustmentRow(
            label = "Threads",
            value = detectionConfig.numThreads.toString(),
            onMinusClick = { viewModel.updateNumThreads((detectionConfig.numThreads - 1).coerceAtLeast(1)) },
            onPlusClick = { viewModel.updateNumThreads((detectionConfig.numThreads + 1).coerceAtMost(4)) }
        )

        SelectionRow(
            label = "Delegate",
            items = DelegateType.entries.map { it.displayName },
            selectedIndex = detectionConfig.delegate.ordinal,
            onSelectionChange = viewModel::updateDelegate
        )

        SelectionRow(
            label = "Model",
            items = ModelType.entries.map { it.displayName },
            selectedIndex = detectionConfig.model.ordinal,
            onSelectionChange = viewModel::updateModel
        )
    }
}

@Composable
fun AdjustmentRow(
    label: String,
    value: String,
    onMinusClick: () -> Unit,
    onPlusClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onMinusClick) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_remove_24),
                    contentDescription = "Decrease"
                )
            }

            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(horizontal = 8.dp)
            )

            IconButton(onClick = onPlusClick) {
                Icon(
                    painter = painterResource(id = R.drawable.baseline_add_24),
                    contentDescription = "Increase"
                )
            }
        }
    }
}

@Composable
fun SelectionRow(
    label: String,
    items: List<String>,
    selectedIndex: Int,
    onSelectionChange: (Int) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge
        )

        Box {
            TextButton(onClick = { expanded = true }) {
                Text(text = items[selectedIndex])
            }

            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false }
            ) {
                items.forEachIndexed { index, item ->
                    DropdownMenuItem(
                        text = { Text(text = item) },
                        onClick = {
                            onSelectionChange(index)
                            expanded = false
                        }
                    )
                }
            }
        }
    }
}