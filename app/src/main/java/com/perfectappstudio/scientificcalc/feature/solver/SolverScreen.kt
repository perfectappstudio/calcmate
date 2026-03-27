package com.perfectappstudio.scientificcalc.feature.solver

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.perfectappstudio.scientificcalc.core.model.SolverType
import com.perfectappstudio.scientificcalc.ui.theme.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SolverScreen(
    modifier: Modifier = Modifier,
    viewModel: SolverViewModel = viewModel(),
) {
    val state by viewModel.state.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
    ) {
        Text(
            text = "Equation Solver",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // --- Glass pill segmented row: Linear | Quadratic | Cubic | System | Solve ---
        val options = listOf(
            SolverType.LINEAR to "Linear",
            SolverType.QUADRATIC to "Quadratic",
            SolverType.CUBIC to "Cubic",
            SolverType.SYSTEM_2X2 to "System",
            SolverType.NEWTON to "Solve",
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            options.forEach { (type, label) ->
                val isSelected = when (type) {
                    SolverType.SYSTEM_2X2 -> state.solverType == SolverType.SYSTEM_2X2 ||
                        state.solverType == SolverType.SYSTEM_3X3
                    else -> state.solverType == type
                }
                FilterChip(
                    selected = isSelected,
                    onClick = { viewModel.onTypeChange(type) },
                    label = {
                        Text(
                            text = label,
                            color = if (isSelected) Color.Black else Color.White,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                    shape = RoundedCornerShape(20.dp),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = GlassBorder,
                        selectedBorderColor = Color.Transparent,
                    ),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = GlassLight,
                        selectedContainerColor = AmberAccent,
                        labelColor = Color.White,
                        selectedLabelColor = Color.Black,
                    ),
                )
            }
        }

        // --- Show appropriate panel ---
        when (state.solverType) {
            SolverType.LINEAR -> LinearSolverPanel(
                state = state,
                onCoefficientChange = viewModel::onLinearCoefficientChange,
                onSolve = viewModel::onSolve,
            )
            SolverType.QUADRATIC -> QuadraticSolverPanel(
                state = state,
                onCoefficientChange = viewModel::onQuadCoefficientChange,
                onSolve = viewModel::onSolve,
                onToggleSteps = viewModel::onToggleSteps,
            )
            SolverType.CUBIC -> CubicSolverPanel(
                state = state,
                onCoefficientChange = viewModel::onCubicCoefficientChange,
                onSolve = viewModel::onSolve,
                onToggleSteps = viewModel::onToggleSteps,
            )
            SolverType.SYSTEM_2X2, SolverType.SYSTEM_3X3 -> SystemSolverPanel(
                state = state,
                onTypeChange = viewModel::onTypeChange,
                on2x2CoefficientChange = viewModel::onSystem2x2CoefficientChange,
                on3x3CoefficientChange = viewModel::onSystem3x3CoefficientChange,
                onSolve = viewModel::onSolve,
            )
            SolverType.NEWTON -> NewtonSolverPanel(
                state = state,
                onExpressionChange = viewModel::onNewtonExpressionChange,
                onGuessChange = viewModel::onNewtonGuessChange,
                onSolve = viewModel::onSolve,
            )
        }
    }
}
