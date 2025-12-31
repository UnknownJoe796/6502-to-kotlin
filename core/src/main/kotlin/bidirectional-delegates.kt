package com.ivieleague.decompiler6502tokotlin.hand

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Bidirectional property delegate system for maintaining graph consistency.
 *
 * This ensures that when you set A.forward = B, it automatically adds A to B.backward collection,
 * preventing the common bug of forgetting to update both sides of a bidirectional reference.
 *
 * Example usage:
 * ```
 * class Node {
 *     private val childrenDelegate = BackwardCollectionDelegate<Node>()
 *     val children: List<Node> by childrenDelegate
 *
 *     var parent: Node? by BidirectionalDelegate { it.childrenDelegate.getMutable() }
 * }
 *
 * val parent = Node()
 * val child = Node()
 *
 * child.parent = parent  // Automatically adds child to parent.children
 * assert(child in parent.children)  // ✓ true
 * ```
 */

/**
 * Delegate for the backward collection side of a bidirectional relationship.
 * Exposes the collection as read-only to prevent manual corruption.
 */
class BackwardCollectionDelegate<T> {
    private val collection = mutableListOf<T>()

    operator fun getValue(thisRef: Any?, property: KProperty<*>): List<T> = collection

    /**
     * Internal accessor for BidirectionalDelegate to modify the collection.
     * This is intentionally not exposed publicly to prevent manual manipulation.
     */
    internal fun getMutable(): MutableCollection<T> = collection
}

/**
 * Delegate for the forward side of a bidirectional relationship.
 * When the property is set, it automatically updates the backward collection.
 *
 * @param backwardGetter Function that retrieves the mutable backward collection from the target object
 * @param allowReset If true, allows the property to be reset (used for analysis that may run multiple times)
 */
class BidirectionalDelegate<Owner, Target>(
    private val backwardGetter: (Target) -> MutableCollection<Owner>,
    private val allowReset: Boolean = false
) : ReadWriteProperty<Owner, Target?> {
    private var value: Target? = null
    private var isSet = false

    override fun getValue(thisRef: Owner, property: KProperty<*>): Target? = value

    override fun setValue(thisRef: Owner, property: KProperty<*>, newValue: Target?) {
        if (!allowReset) {
            check(!isSet) { "${property.name} can only be set once (current value: $value, attempted: $newValue)" }
        }

        // Remove old reverse reference if resetting
        if (isSet && value != null && value != newValue) {
            val oldBackwardCollection = backwardGetter(value!!)
            oldBackwardCollection.remove(thisRef)
        }

        if (newValue != null) {
            // Add new reverse reference
            val backwardCollection = backwardGetter(newValue)
            backwardCollection.add(thisRef)
        }

        value = newValue
        isSet = true
    }
}

/**
 * Simple single-set delegate without bidirectional linking.
 * Useful for properties that don't have a reverse relationship.
 */
class SetOnceDelegate<T> : ReadWriteProperty<Any?, T?> {
    private var value: T? = null
    private var isSet = false

    override fun getValue(thisRef: Any?, property: KProperty<*>): T? = value

    override fun setValue(thisRef: Any?, property: KProperty<*>, newValue: T?) {
        check(!isSet) { "${property.name} can only be set once (current value: $value, attempted: $newValue)" }
        value = newValue
        isSet = true
    }
}

/**
 * Extension function to create a setOnce delegate with less boilerplate.
 */
fun <T> setOnce(): ReadWriteProperty<Any?, T?> = SetOnceDelegate()
