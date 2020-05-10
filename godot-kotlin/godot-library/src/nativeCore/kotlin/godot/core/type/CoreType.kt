package godot.core

import godot.gdnative.godot_pool_byte_array
import kotlinx.cinterop.*


internal interface CoreType {
    fun getRawMemory(memScope: MemScope): COpaquePointer
    fun setRawMemory(mem: COpaquePointer)
}

abstract class AbstractCoreType(internal var handle: CValue<godot_pool_byte_array>): CoreType {
    internal inline fun <T> callNative(block: MemScope.(CPointer<godot_pool_byte_array>) -> T): T {
        return memScoped {
            val ptr = handle.ptr
            val ret: T = block(ptr)
            handle = ptr.pointed.readValue()
            ret
        }
    }
}

internal fun Long.getRawMemory(memScope: MemScope): COpaquePointer {
    return memScope.alloc<LongVar>().apply {
        this.value = this@getRawMemory
    }.ptr
}

internal fun Double.getRawMemory(memScope: MemScope): COpaquePointer {
    return memScope.alloc<DoubleVar>().apply {
        this.value = this@getRawMemory
    }.ptr
}

internal fun Boolean.getRawMemory(memScope: MemScope): COpaquePointer {
    return memScope.alloc<BooleanVar>().apply {
        this.value = this@getRawMemory
    }.ptr
}
