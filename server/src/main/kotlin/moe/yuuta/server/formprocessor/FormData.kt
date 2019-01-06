package moe.yuuta.server.formprocessor


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class FormData(val name: String,
                          val urlEncode: Boolean = false,
                          val ignorable: Boolean = true)