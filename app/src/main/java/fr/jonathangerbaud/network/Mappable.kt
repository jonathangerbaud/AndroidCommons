package fr.jonathangerbaud.network


interface Mappable<out T : Any> {
    fun mapToResult(): Result<T>
}