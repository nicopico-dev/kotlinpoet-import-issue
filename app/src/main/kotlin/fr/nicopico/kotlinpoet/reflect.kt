package fr.nicopico.kotlinpoet

import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberFunctions

private val IGNORED_FUNCTIONS = listOf("toString", "equals", "hashCode")

internal val KClass<*>.methods: List<KFunction<*>>
    get() = memberFunctions
        .filter {
            it.visibility == KVisibility.PUBLIC
                    && it.name !in IGNORED_FUNCTIONS
        }

internal val KType.isGeneric: Boolean
    get() = arguments.isNotEmpty()
