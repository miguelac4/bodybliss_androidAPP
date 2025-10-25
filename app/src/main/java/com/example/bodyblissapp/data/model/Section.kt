package com.example.bodyblissapp.data.model

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class Section(
    @DrawableRes val imageRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val subtitleRes: Int
)