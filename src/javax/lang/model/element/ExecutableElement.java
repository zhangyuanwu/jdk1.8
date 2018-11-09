package javax.lang.model.element;

import java.util.List;
import javax.lang.model.type.*;

public interface ExecutableElement extends Element, Parameterizable {
	List<? extends TypeParameterElement> getTypeParameters();

	TypeMirror getReturnType();

	List<? extends VariableElement> getParameters();

	/**
	 * Returns the receiver type of this executable, or
	 * {@link javax.lang.model.type.NoType NoType} with kind
	 * {@link javax.lang.model.type.TypeKind#NONE NONE} if the executable has no
	 * receiver type.
	 *
	 * An executable which is an instance method, or a constructor of an inner
	 * class, has a receiver type derived from the
	 * {@linkplain #getEnclosingElement declaring type}.
	 *
	 * An executable which is a static method, or a constructor of a non-inner
	 * class, or an initializer (static or instance), has no receiver type.
	 *
	 * @return the receiver type of this executable
	 * @since 1.8
	 */
	TypeMirror getReceiverType();

	boolean isVarArgs();

	/**
	 * Returns {@code true} if this method is a default method and returns
	 * {@code false} otherwise.
	 *
	 * @return {@code true} if this method is a default method and {@code false}
	 *         otherwise
	 * @since 1.8
	 */
	boolean isDefault();

	List<? extends TypeMirror> getThrownTypes();

	AnnotationValue getDefaultValue();

	@Override
	Name getSimpleName();
}
