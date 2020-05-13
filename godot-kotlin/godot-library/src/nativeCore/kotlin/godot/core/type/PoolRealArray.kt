@file:Suppress("unused", "MemberVisibilityCanBePrivate")

package godot.core

import godot.gdnative.*
import godot.internal.type.NativeCoreType
import kotlinx.cinterop.*

class PoolRealArray : NativeCoreType<godot_pool_real_array>, Iterable<RealT> {
    //CONSTRUCTOR
    constructor() {
        callNative {
            checkNotNull(Godot.gdnative.godot_pool_real_array_new)(it)
        }
    }

    constructor(other: PoolRealArray) {
        callNative {
            checkNotNull(Godot.gdnative.godot_pool_real_array_new_copy)(it, other._handle.ptr)
        }
    }


    internal constructor(mem: COpaquePointer) {
        this.setRawMemory(mem)
    }

    //INTEROP
    override fun getRawMemory(memScope: MemScope): COpaquePointer {
        return _handle.getPointer(memScope)
    }

    override fun setRawMemory(mem: COpaquePointer) {
        _handle = mem.reinterpret<godot_pool_real_array>().pointed.readValue()
    }


    //POOL ARRAY API SHARED
    /**
     * Appends an element at the end of the array (alias of push_back).
     */
    fun append(real: RealT) {
        callNative {
            checkNotNull(Godot.gdnative.godot_pool_real_array_append)(it, real.toFloat())
        }
    }


    /**
     * Appends a PoolRealArray at the end of this array.
     */
    fun appendArray(array: PoolRealArray) {
        callNative {
            checkNotNull(Godot.gdnative.godot_pool_real_array_append_array)(it, array._handle.ptr)
        }
    }

    /**
     * Returns true if the array is empty.
     */
    fun empty() {
        callNative {
            checkNotNull(Godot.gdnative12.godot_pool_real_array_empty)(it)
        }
    }

    /**
     *  Retrieve the element at the given index.
     */
    operator fun get(idx: Int): RealT {
        return callNative {
            checkNotNull(Godot.gdnative.godot_pool_real_array_get)(it, idx)
        }.toRealT()
    }

    /**
     * Inserts a new element at a given position in the array.
     * The position must be valid, or at the end of the array (idx == size()).
     */
    fun insert(idx: Int, data: RealT) {
        callNative {
            checkNotNull(Godot.gdnative.godot_pool_real_array_insert)(it, idx, data.toFloat())
        }
    }

    /**
     * Reverses the order of the elements in the array.
     */
    fun invert() {
        callNative {
            checkNotNull(Godot.gdnative.godot_pool_real_array_invert)(it)
        }
    }

    /**
     * Appends a value to the array.
     */
    fun pushBack(data: RealT) {
        callNative {
            checkNotNull(Godot.gdnative.godot_pool_real_array_push_back)(it, data.toFloat())
        }
    }

    /**
     * Removes an element from the array by index.
     */
    fun remove(idx: Int) {
        callNative {
            checkNotNull(Godot.gdnative.godot_pool_real_array_remove)(it, idx)
        }
    }

    /**
     * Sets the size of the array. If the array is grown, reserves elements at the end of the array.
     * If the array is shrunk, truncates the array to the new size.
     */
    fun resize(size: Int) {
        callNative {
            checkNotNull(Godot.gdnative.godot_pool_real_array_resize)(it, size)
        }
    }

    /**
     * Changes the real at the given index.
     */
    operator fun set(idx: Int, data: RealT) {
        callNative {
            checkNotNull(Godot.gdnative.godot_pool_real_array_set)(it, idx, data.toFloat())
        }
    }

    /**
     * Returns the size of the array.
     */
    fun size(): Int {
        return callNative {
            checkNotNull(Godot.gdnative.godot_pool_real_array_size)(it)
        }
    }

    //UTILITIES
    operator fun plus(other: RealT) {
        this.append(other)
    }

    operator fun plus(other: PoolRealArray) {
        this.appendArray(other)
    }

    override fun toString(): String {
        return "PoolRealArray(${size()})"
    }

    override fun iterator(): Iterator<RealT> {
        return IndexedIterator(size(), this::get)
    }

    /**
     * WARNING: no equals function is available in the Gdnative API for this Coretype.
     * This methods implementation works but is not the fastest one.
     */
    override fun equals(other: Any?): Boolean {
        return if (other is PoolRealArray) {
            val list1 = this.toList()
            val list2 = other.toList()
            list1 == list2
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return _handle.hashCode()
    }

    internal inline fun <T> callNative(block: MemScope.(CPointer<godot_pool_real_array>) -> T): T {
        return callNative(this, block)
    }
}
