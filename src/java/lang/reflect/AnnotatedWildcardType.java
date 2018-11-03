package java.lang.reflect;

/**
 * {@code AnnotatedWildcardType}表示通配符类型参数的潜在注释用法,其上限或下限本身可以表示类型的注释用法.
 *
 * @since 1.8
 */
public interface AnnotatedWildcardType extends AnnotatedType {

	/**
	 * 返回此通配符类型的可能带注释的下限.
	 *
	 * @return 此通配符类型的潜在注释下限
	 */
	AnnotatedType[] getAnnotatedLowerBounds();

	/**
	 * 返回此通配符类型的可能带注释的上限.
	 *
	 * @return 此通配符类型的潜在注释上限
	 */
	AnnotatedType[] getAnnotatedUpperBounds();
}
