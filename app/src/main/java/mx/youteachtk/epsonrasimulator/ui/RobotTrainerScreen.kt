package mx.youteachtk.epsonrasimulator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import mx.youteachtk.epsonrasimulator.domain.EpsonRobotCatalog
import mx.youteachtk.epsonrasimulator.domain.JointDefinition
import mx.youteachtk.epsonrasimulator.domain.RobotDefinition

@Composable
fun RobotTrainerScreen() {
    val robot = EpsonRobotCatalog.C4_A601S
    val jointValues = remember(robot.id) {
        mutableStateListOf<Float>().apply {
            addAll(robot.zeroJointValues.map(Double::toFloat))
        }
    }

    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .weight(1.7f)
                .fillMaxHeight()
        ) {
            Header(robot = robot)

            Spacer(modifier = Modifier.height(12.dp))

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "C4-A601S • 3D VIEW",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Official articulated model slot prepared",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Next: STEP → optimized GLB → J1–J6 node mapping",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        Card(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Joint Jog",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "RC+ ranges for C4-A601S",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                robot.joints.forEachIndexed { index, joint ->
                    JointSlider(
                        joint = joint,
                        value = jointValues[index],
                        onValueChange = { jointValues[index] = it }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = {
                        robot.zeroJointValues.forEachIndexed { index, value ->
                            jointValues[index] = value.toFloat()
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("ZERO JOINTS")
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text("TCP", fontWeight = FontWeight.Bold)
                Text(
                    "X / Y / Z will be calculated after forward kinematics is calibrated.",
                    style = MaterialTheme.typography.bodySmall
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = {}, enabled = false) { Text("TOUCH") }
                    Button(onClick = {}, enabled = false) { Text("SAVE P1") }
                }
            }
        }
    }
}

@Composable
private fun Header(robot: RobotDefinition) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "EPSON RA SIMULATOR",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${robot.displayName} • Simulation / Learning",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Reach ${robot.reachMm?.toInt()} mm • Max payload ${robot.maxPayloadKg} kg",
                style = MaterialTheme.typography.bodySmall
            )
        }
        Text(text = "SIM", fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun JointSlider(
    joint: JointDefinition,
    value: Float,
    onValueChange: (Float) -> Unit
) {
    Column(modifier = Modifier.padding(bottom = 10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(joint.id, fontWeight = FontWeight.Bold)
                Text(joint.displayName, style = MaterialTheme.typography.bodySmall)
            }
            Text(String.format("%.1f°", value))
        }

        Slider(
            value = value,
            onValueChange = onValueChange,
            valueRange = joint.minValue.toFloat()..joint.maxValue.toFloat()
        )

        Text(
            text = "${joint.minValue.toInt()}° … ${joint.maxValue.toInt()}°  •  max ${joint.maxSpeedDegPerSec?.toInt()}°/s",
            style = MaterialTheme.typography.labelSmall
        )
    }
}
