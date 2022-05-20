package nbridgekit.plugin

import nbridgekit.plugin.base.PluginBase


open class PluginManager {
    private var plugins: MutableMap<String, PluginBase> = HashMap()

    open fun addPlugin(service: String, plugin: PluginBase) {
        plugins[service] = plugin
    }

    open fun findPlugin(service: String): PluginBase? {
        return plugins[service]
    }
}