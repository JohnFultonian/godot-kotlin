package godot.codegen

import com.beust.klaxon.Klaxon
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import java.io.File


object Generator {

    fun generate(source: File, outputDir: File) {
        val classes: List<Class> = Klaxon().parseArray(source.readText())!!

        val tree = classes.buildTree()
        val icalls = mutableSetOf<ICall>()

        classes.forEach { clazz ->
            clazz.generate(outputDir, tree, icalls)
        }

        val iCallFileSpec = FileSpec
            .builder("godot.icalls", "__icalls")
            .addFunction(generateICallsVarargsFunction())
            .addImport("kotlinx.cinterop", "set", "get")
            .addImport("godot.core", "getRawMemory")
            .addImport("godot.core", "String")

        icalls.forEach { iCallFileSpec.addFunction(it.iCallSpec) }

        outputDir.parentFile.mkdirs()

        iCallFileSpec
            .build()
            .writeTo(outputDir)

        val pseudoConstructorsFileSpec = FileSpec.builder("godot", "PseudoConstructors")
        generatePseudoConstructors(pseudoConstructorsFileSpec, classes)
        pseudoConstructorsFileSpec.build().writeTo(outputDir)
    }

    private fun generatePseudoConstructors(pseudoConstructorsFileSpec: FileSpec.Builder, classes: List<Class>) {
        classes.filter { it.isInstanciable }.forEach {
            pseudoConstructorsFileSpec.addFunction(
                FunSpec.builder(it.name)
                    .returns(ClassName("godot", it.name))
                    .addStatement(
                        "return ${it.name}Impl(%M(\"${it.oldName}\")?.reinterpret<%T<() -> %T>>()()) ?: \n throw NotImplementedError(\"No constructor for ${it.name} in Godot\")",
                        MemberName("godot.gdnative", "godot_get_class_constructor"),
                        ClassName("kotlinx.cinterop", "CFunction"),
                        ClassName("kotlinx.cinterop", "COpaquePointer")
                    )
                    .build()
            )
        }
    }

    private fun generateICallsVarargsFunction(): FunSpec {
        return FunSpec
            .builder("_icall_varargs")
            .addModifiers(KModifier.INTERNAL)
            .returns(ClassName("godot.core", "Variant"))
            .addParameter(
                "mb",
                ClassName("kotlinx.cinterop", "CPointer")
                    .parameterizedBy(ClassName("godot.gdnative", "godot_method_bind"))
            )
            .addParameter(
                "inst",
                ClassName("kotlinx.cinterop", "COpaquePointer")
            )
            .addParameter(
                "arguments",
                ClassName("kotlin", "Array").parameterizedBy(STAR)
            )
            .addStatement(
                """%M {
                            |    val args = %M<%T<%M>>(arguments.size)
                            |    for ((i,arg) in arguments.withIndex()) args[i] = %N.from(arg).nativeValue.ptr
                            |    val result = %M(mb, inst, args, arguments.size, null)
                            |    for (i in arguments.indices) %M(args[i])
                            |    return %N(result)
                            |}
                            |""".trimMargin(),
                MemberName("kotlinx.cinterop", "memScoped"),
                MemberName("kotlinx.cinterop", "allocArray"),
                ClassName("kotlinx.cinterop", "CPointerVar"),
                MemberName("godot.gdnative", "godot_variant"),
                MemberName("godot.core", "Variant"),
                MemberName("godot.gdnative", "godot_method_bind_call"),
                MemberName("godot.gdnative", "godot_variant_destroy"),
                MemberName("godot.core", "Variant")
            )
            .build()
    }
}