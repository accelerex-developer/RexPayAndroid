@file:JvmSynthetic

package com.octacore.rexpay.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.octacore.rexpay.ui.theme.Purple80
import com.octacore.rexpay.ui.theme.PurpleGrey40

/***************************************************************************************************
 *                          Copyright (C) 2024,  Octacore Tech.
 ***************************************************************************************************
 * Project         : rexpay
 * Author          : Gideon Chukwu
 * Date            : 28/01/2024
 **************************************************************************************************/

@Composable
internal fun CustomDialog(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    positiveText: String? = null,
    negativeText: String? = null,
    onDismissRequest: () -> Unit,
    onPositiveClicked: () -> Unit,
    onNegativeClicked: () -> @Composable Unit,
    properties: DialogProperties = DialogProperties(),
    content: @Composable ColumnScope.() -> Unit,
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = properties,
        content = {
            Card(
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.padding(10.dp, 5.dp, 10.dp, 10.dp),
                elevation = 8.dp
            ) {
                Column(
                    verticalArrangement = verticalArrangement,
                    horizontalAlignment = horizontalAlignment,
                    modifier = modifier.background(color = Color.White),
                ) {
                    content()
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .background(Purple80),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        if (negativeText != null) {
                            TextButton(onClick = onNegativeClicked) {
                                Text(
                                    negativeText,
                                    fontWeight = FontWeight.W500,
                                    color = PurpleGrey40,
                                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                                )
                            }
                        }
                        if (positiveText != null) {
                            TextButton(onClick = onPositiveClicked) {
                                Text(
                                    positiveText,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    modifier = Modifier.padding(top = 5.dp, bottom = 5.dp)
                                )
                            }
                        }
                    }
                }
            }
        },
    )
}