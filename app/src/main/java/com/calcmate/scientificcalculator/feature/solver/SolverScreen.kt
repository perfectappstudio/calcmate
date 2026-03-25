package com.calcmate.scientificcalculator.feature.solver

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.calcmate.scientificcalculator.core.model.SolverType

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
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = 16.dp),
        )

        // --- Segmented button row: Linear | Quadratic | System ---
        val options = listOf(
            SolverType.LINEAR to "Linear",
            SolverType.QUADRATIC to "Quadratic",
            SolverType.SYSTEM_2X2 to "System",
        )

        SingleChoiceSegmentedButtonRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
        ) {
            options.forEachIndexed { index, (type, label) ->
                SegmentedButton(
                    selected = when (type) {
                        SolverType.SYSTEM_2X2 -> state.solverType == SolverType.SYSTEM_2X2 ||
                            state.solverType == SolverType.SYSTEM_3X3
                        else -> state.solverType == type
                    },
                    onClick = { viewModel.onTypeChange(type) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size,
                    ),
                ) {
                    Text(label)
                }
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
            SolverType.SYSTEM_2X2, SolverType.SYSTEM_3X3 -> SystemSolverPanel(
                state = state,
                onTypeChange = viewModel::onTypeChange,
                on2x2CoefficientChange = viewModel::onSystem2x2CoefficientChange,
                on3x3CoefficientChange = viewModel::onSystem3x3CoefficientChange,
                onSolve = viewModel::onSolve,
            )
        }
    }
}
