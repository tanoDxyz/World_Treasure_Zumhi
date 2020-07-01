package com.geocache.zumhi.worldtreasure.tanveer

import com.geocache.zumhi.worldtreasure.tanveer.Repository
import org.koin.dsl.module

val applicationModule = module {
    single { Repository() }
    single {TinyDB(get())}
}