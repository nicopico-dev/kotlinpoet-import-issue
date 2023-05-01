package fr.nicopico.kotlinpoet

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.MemberName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asClassName
import com.squareup.kotlinpoet.asTypeName
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import kotlinx.coroutines.flow.Flow
import kotlin.reflect.KFunction
import kotlin.reflect.jvm.jvmErasure
import kotlin.reflect.jvm.jvmName

private val FLOW_CLASS = Flow::class.asClassName()
private val AWAIT_MEMBER = MemberName(
    "kotlinx.coroutines.rx3",
    "await",
    isExtension = true,
)
private val AS_FLOW_MEMBER_OBSERVABLE = MemberName(
    "kotlinx.coroutines.rx3",
    "asFlow",
    isExtension = true,
)
private val AS_FLOW_MEMBER_FLOWABLE = MemberName(
    "kotlinx.coroutines.reactive",
    "asFlow",
    isExtension = true,
)

internal fun generateFunSpecs(rxStubName: String, methods: List<KFunction<*>>): List<FunSpec> {
    return methods.map { method ->
        FunSpec.builder(method.name)
            .addModifiers(KModifier.SUSPEND)
            .returns(getReturnTypeParameter(method))
            .apply {
                // Transform Rx to Coroutines
                val rxConversionMember =
                    when (val r = method.returnType.jvmErasure.jvmName) {
                        Completable::class.qualifiedName -> AWAIT_MEMBER
                        Single::class.qualifiedName -> AWAIT_MEMBER
                        Observable::class.qualifiedName -> AS_FLOW_MEMBER_OBSERVABLE
                        Flowable::class.qualifiedName -> AS_FLOW_MEMBER_FLOWABLE
                        else -> throw UnsupportedOperationException("Unsupported type $r")
                    }

                val rxMethodCall = "$rxStubName.${method.name}()"

                addCode("$rxMethodCall.%M()", rxConversionMember)
            }
            .build()
    }
}

private fun getReturnTypeParameter(method: KFunction<*>): TypeName {
    val returnType = method.returnType
    val rawTypeName: String = returnType.jvmErasure.jvmName
    val argumentType: TypeName = if (returnType.isGeneric) {
        returnType.arguments[0].type!!.asTypeName()
    } else {
        Unit::class.asTypeName()
    }

    return when (rawTypeName) {
        Completable::class.qualifiedName -> Unit::class.asTypeName()
        Single::class.qualifiedName -> argumentType
        Observable::class.qualifiedName ->
            FLOW_CLASS.parameterizedBy(argumentType)

        Flowable::class.qualifiedName ->
            FLOW_CLASS.parameterizedBy(argumentType)

        else -> returnType.asTypeName()
    }
}
