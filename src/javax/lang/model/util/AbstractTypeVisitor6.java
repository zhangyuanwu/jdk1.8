package javax.lang.model.util;

import javax.lang.model.type.*;

public abstract class AbstractTypeVisitor6<R, P> implements TypeVisitor<R, P> {
	protected AbstractTypeVisitor6() {
	}

	public final R visit(TypeMirror t, P p) {
		return t.accept(this, p);
	}

	public final R visit(TypeMirror t) {
		return t.accept(this, null);
	}

	public R visitUnion(UnionType t, P p) {
		return visitUnknown(t, p);
	}

	/**
	 * Visits an {@code IntersectionType} element by calling {@code
	 * visitUnknown}.
	 *
	 * @param t
	 *            {@inheritDoc}
	 * @param p
	 *            {@inheritDoc}
	 * @return the result of {@code visitUnknown}
	 *
	 * @since 1.8
	 */
	public R visitIntersection(IntersectionType t, P p) {
		return visitUnknown(t, p);
	}

	public R visitUnknown(TypeMirror t, P p) {
		throw new UnknownTypeException(t, p);
	}
}
