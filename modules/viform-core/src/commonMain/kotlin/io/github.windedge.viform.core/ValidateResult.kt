package io.github.windedge.viform.core

public sealed class ValidateResult {

    public object None : ValidateResult()

    public object Success : ValidateResult()

    public class Failure(public val message: String) : ValidateResult()

    internal val isRealSuccess: Boolean get() = this is Success

    public val isSuccess: Boolean get() = this is None || this is Success

    public val isFailure: Boolean get() = this is Failure

    public val errorMessage: String?
        get() {
            if (this is Failure) {
                return this.message
            }
            return null
        }
}