package moe.yuuta.server.formprocessor


@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class FormData(val name: String,
                          val urlEncode: Boolean = false,
                          // If it is true, the fields with default values (String: "", Number: 0) will be removed.
                          // If it is false, the fields with default values will be kept as 'key=' or 'key=0', etc.
                          // Nulls are always removed.
                          val ignorable: Boolean = true)