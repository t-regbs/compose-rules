// Copyright 2023 Nacho Lopez
// SPDX-License-Identifier: Apache-2.0
package io.nlopez.compose.rules.detekt

import io.gitlab.arturbosch.detekt.api.Config
import io.gitlab.arturbosch.detekt.api.SourceLocation
import io.gitlab.arturbosch.detekt.test.assertThat
import io.gitlab.arturbosch.detekt.test.lint
import io.nlopez.compose.rules.CompositionLocalNaming
import org.intellij.lang.annotations.Language
import org.junit.jupiter.api.Test

class CompositionLocalNamingCheckTest {

    private val rule = CompositionLocalNamingCheck(Config.empty)

    @Test
    fun `error when a CompositionLocal has a wrong name`() {
        @Language("kotlin")
        val code =
            """
                val AppleLocal = staticCompositionLocalOf<String> { "Apple" }
                val Plum: String = staticCompositionLocalOf { "Plum" }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors)
            .hasStartSourceLocations(
                SourceLocation(1, 5),
                SourceLocation(2, 5),
            )
        for (error in errors) {
            assertThat(error).hasMessage(CompositionLocalNaming.CompositionLocalNeedsLocalPrefix)
        }
    }

    @Test
    fun `passes when a CompositionLocal is well named`() {
        @Language("kotlin")
        val code =
            """
                val LocalBanana = staticCompositionLocalOf<String> { "Banana" }
                val LocalPotato = compositionLocalOf { "Potato" }
            """.trimIndent()
        val errors = rule.lint(code)
        assertThat(errors).isEmpty()
    }
}
