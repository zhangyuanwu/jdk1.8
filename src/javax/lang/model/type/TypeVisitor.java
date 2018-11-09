package javax.lang.model.type;

public interface TypeVisitor<R, P> {
	R visit(TypeMirror t, P p);

	R visit(TypeMirror t);

	R visitPrimitive(PrimitiveType t, P p);

	R visitNull(NullType t, P p);

	R visitArray(ArrayType t, P p);

	R visitDeclared(DeclaredType t, P p);

	R visitError(ErrorType t, P p);

	R visitTypeVariable(TypeVariable t, P p);

	R visitWildcard(WildcardType t, P p);

	R visitExecutable(ExecutableType t, P p);

	R visitNoType(NoType t, P p);

	R visitUnknown(TypeMirror t, P p);

	R visitUnion(UnionType t, P p);

	/**
	 * Visits an intersection type.
	 *
	 * @param t
	 *            the type to visit
	 * @param p
	 *            a visitor-specified parameter
	 * @return a visitor-specified result
	 * @since 1.8
	 */
	R visitIntersection(IntersectionType t, P p);
}
