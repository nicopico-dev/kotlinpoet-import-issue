package fr.nicopico.kotlinpoet

import kotlin.String
import kotlin.Unit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.rx3.await

public class CoroutineStub(
  public val rxStub: RxStub,
) {
  public suspend fun someCompletable(): Unit {
    rxStub.someCompletable().await()
  }

  public suspend fun someFlowable(): Flow<String> {
    rxStub.someFlowable().asFlow()
  }

  public suspend fun someObservable(): Flow<String> {
    rxStub.someObservable().kotlinx.coroutines.rx3.asFlow()
  }

  public suspend fun someSingle(): String {
    rxStub.someSingle().await()
  }
}
