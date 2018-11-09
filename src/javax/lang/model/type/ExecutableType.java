package javax.lang.model.type;

import java.util.List;
import javax.lang.model.element.ExecutableElement;

public interface ExecutableType extends TypeMirror {
	List<? extends TypeVariable> getTypeVariables();

	TypeMirror getReturnType();

	List<? extends TypeMirror> getParameterTypes();

	/**
	 * Returns the receiver type of this executable, or
	 * {@link javax.lang.model.type.NoType NoType} with kind
	 * {@link javax.lang.model.type.TypeKind#NONE NONE} if the executable has no
	 * receiver type.
	 *
	 * An executable which is an instance method, or a constructor of an inner
	 * class, has a receiver type derived from the
	 * {@linkplain ExecutableElement#getEnclosingElement declaring type}.
	 *
	 * An executable which is a static method, or a constructor of a non-inner
	 * class, or an initializer (static or instance), has no receiver type.
	 *
	 * @return the receiver type of this executable
	 * @since 1.8
	 */
	TypeMirror getReceiverType();

	List<? extends TypeMirror> getThrownTypes();
}
