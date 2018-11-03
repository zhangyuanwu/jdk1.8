package java.lang.reflect;

/**
 * {@code AnnotatedParameterizedType}表示参数化类型的潜在注释用法,其类型参数本身可以表示类型的注释用法.
 *
 * @since 1.8
 */
public interface AnnotatedParameterizedType extends AnnotatedType {
	/**
	 * 返回此参数化类型的可能带注释的实际类型参数.
	 *
	 * @return 此参数化类型的潜在注释实际类型参数
	 */
	AnnotatedType[] getAnnotatedActualTypeArguments();
}
