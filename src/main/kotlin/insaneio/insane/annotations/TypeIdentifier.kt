package insaneio.insane.annotations

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class TypeIdentifier(val identifier: String)
