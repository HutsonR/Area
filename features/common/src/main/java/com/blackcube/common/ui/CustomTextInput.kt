package com.blackcube.common.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.blackcube.common.R

private val defaultErrorColor = Color(0xFFD32F2F)

/**
 * Кастомное текстовое поле с меткой, подсказкой, иконками и валидацией.
 *
 * @param value текущий текст в поле
 * @param onValueChange вызывается при изменении текста
 * @param modifier модификатор для внешнего расположения/отступов
 * @param label необязательный текст метки над полем
 * @param placeholder необязательный текст-подсказка внутри поля
 * @param enabled флаг, разрешён ли ввод
 * @param singleLine true — одно поле, false — многострочный ввод
 * @param textStyle стиль текста (размер, шрифт и т.п.)
 * @param colors цветовая схема для поля
 * @param leadingIcon необязательная иконка слева
 * @param trailingIcon необязательная иконка справа
 * @param clearEnabled показывать ли кнопку очистки
 * @param onClear вызывается при нажатии на кнопку очистки
 * @param capitalizeFirstLetter автоматически делать первую букву заглавной
 * @param validator лямбда для проверки (возвращает текст ошибки или null)
 * @param visualTransformation как отображать ввод (например, PasswordVisualTransformation())
 */
@Composable
fun CustomTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String? = null,
    placeholder: String? = null,
    enabled: Boolean = true,
    singleLine: Boolean = true,
    textStyle: TextStyle = TextStyle(fontSize = 16.sp),
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors(errorBorderColor = defaultErrorColor),
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    clearEnabled: Boolean = false,
    onClear: () -> Unit = {},
    capitalizeFirstLetter: Boolean = false,
    errorText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    visualTransformation: VisualTransformation = VisualTransformation.None
) {
    var savedErrorText by remember { mutableStateOf(errorText) }
    var isFocused by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxWidth()) {
        if (!label.isNullOrEmpty()) {
            Text(
                text = label,
                fontSize = 16.sp,
                color = colorResource(id = R.color.title_color),
                modifier = Modifier
                    .padding(bottom = 4.dp, start = 6.dp)
            )
        }
        OutlinedTextField(
            value = value,
            onValueChange = { new ->
                onValueChange(new)

                if (savedErrorText != null) {
                    savedErrorText = null
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { state ->
                    if (isFocused && !state.isFocused) {
                        savedErrorText = errorText
                    }
                    isFocused = state.isFocused
                },
            enabled = enabled,
            singleLine = singleLine,
            textStyle = textStyle,
            isError = !savedErrorText.isNullOrBlank(),
            colors = colors,
            placeholder = {
                if (!placeholder.isNullOrEmpty()) {
                    Text(text = placeholder)
                }
            },
            leadingIcon = leadingIcon,
            trailingIcon = run {
                when {
                    clearEnabled && value.isNotBlank() -> {
                        {
                            IconButton(onClick = {
                                onValueChange("")
                                onClear()
                                savedErrorText = null
                            }) {
                                Icon(
                                    imageVector = Icons.Filled.Close,
                                    contentDescription = stringResource(id = R.string.text_input_clear)
                                )
                            }
                        }
                    }
                    trailingIcon != null -> trailingIcon
                    else -> null
                }
            },
            keyboardOptions = keyboardOptions.copy(
                capitalization = if (capitalizeFirstLetter)
                    KeyboardCapitalization.Sentences
                else
                    keyboardOptions.capitalization
            ),
            visualTransformation = visualTransformation
        )

        savedErrorText?.let { msg ->
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 8.dp, top = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Error,
                    contentDescription = null,
                    tint = defaultErrorColor,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = msg,
                    color = defaultErrorColor
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewCustomTextInput() {
    var text by remember { mutableStateOf("Какой-то очень длинный текст, который не помещается в инпут") }
    CustomTextInput(
        value = text,
        onValueChange = { text = it },
        clearEnabled = false,
        label = "Введите логин",
        placeholder = "Username",
        colors = OutlinedTextFieldDefaults.colors(
            // Цвет текста
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,

            // Цвета контейнера (фон поля)
            focusedContainerColor = Color.White.copy(alpha = 0.1f),
            unfocusedContainerColor = Color.White.copy(alpha = 0.05f),
            disabledContainerColor = Color.White.copy(alpha = 0.05f),

            // Цвет курсора
            cursorColor = Color(0xFF6200EE),

            // Цвета placeholder
            focusedPlaceholderColor = Color.Gray,
            unfocusedPlaceholderColor = Color.Gray,
            disabledPlaceholderColor = Color.Gray.copy(alpha = 0.5f)
        ),
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "User icon"
            )
        },
        errorText = "Поле не должно быть пустым"
    )
}