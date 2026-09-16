package mx.youteachtk.epsonrasimulator.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import mx.youteachtk.epsonrasimulator.runtime.SharedRuntime
import mx.youteachtk.epsonrasimulator.runtime.SharedRuntimeState

@Composable
fun rememberRuntimeState(runtime: SharedRuntime): SharedRuntimeState {
    var state by remember(runtime) { mutableStateOf(runtime.state) }

    DisposableEffect(runtime) {
        val subscription = runtime.subscribe { state = it }
        onDispose { subscription.cancel() }
    }

    return state
}
