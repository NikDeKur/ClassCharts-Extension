package dev.nikdekur.classcharts.ext

import dev.nikdekur.classcharts.ext.cc.ClassChartsService
import dev.nikdekur.classcharts.ext.cc.WebClassChartsService
import dev.nikdekur.classcharts.ext.login.ClassChartsLoginService
import dev.nikdekur.classcharts.ext.login.LoginService
import dev.nikdekur.classcharts.ext.ui.TimeProvider
import dev.nikdekur.ndkore.service.manager.RuntimeServicesManager
import dev.nikdekur.ndkore.service.manager.ServicesManager
import dev.nikdekur.ornament.AbstractApplication
import dev.nikdekur.ornament.environment.Environment

class ClassChartsExtension(
    override val environment: Environment,
    override val time: TimeProvider
) : AbstractApplication(), Extension {

    override suspend fun createServicesManager(): ServicesManager {
        return RuntimeServicesManager {}.also {
            it.registerService(
                ClassChartsLoginService(this),
                LoginService::class
            )

            it.registerService(
                // DataSetClassChartsService(this),
                WebClassChartsService(this),
                ClassChartsService::class
            )
        }
    }
}