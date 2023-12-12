package io.github.windedge.viform.core

public interface ValidatorContainer<V : Any?> {

    public fun addValidator(validator: FieldValidator<V>)

    public fun getValidators(): List<FieldValidator<V>>

    public fun clearValidators()

}

