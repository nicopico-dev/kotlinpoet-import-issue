package fr.nicopico.kotlinpoet

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single

class RxStub {
    fun someCompletable() : Completable = TODO()
    fun someSingle() : Single<String> = TODO()
    fun someObservable() : Observable<String> = TODO()
    fun someFlowable() : Flowable<String> = TODO()
}
