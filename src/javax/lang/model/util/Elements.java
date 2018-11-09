package javax.lang.model.util;

import java.util.List;
import java.util.Map;
import javax.lang.model.element.*;

public interface Elements {
	PackageElement getPackageElement(CharSequence name);

	TypeElement getTypeElement(CharSequence name);

	Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(AnnotationMirror a);

	String getDocComment(Element e);

	boolean isDeprecated(Element e);

	Name getBinaryName(TypeElement type);

	PackageElement getPackageOf(Element type);

	List<? extends Element> getAllMembers(TypeElement type);

	List<? extends AnnotationMirror> getAllAnnotationMirrors(Element e);

	boolean hides(Element hider, Element hidden);

	boolean overrides(ExecutableElement overrider, ExecutableElement overridden, TypeElement type);

	String getConstantExpression(Object value);

	void printElements(java.io.Writer w, Element... elements);

	Name getName(CharSequence cs);

	/**
	 * Returns {@code true} if the type element is a functional interface,
	 * {@code false} otherwise.
	 *
	 * @param type
	 *            the type element being examined
	 * @return {@code true} if the element is a functional interface,
	 *         {@code false} otherwise
	 * @jls 9.8 Functional Interfaces
	 * @since 1.8
	 */
	boolean isFunctionalInterface(TypeElement type);
}
