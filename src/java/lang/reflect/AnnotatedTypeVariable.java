package java.lang.reflect;

/**
 * {@code AnnotatedTypeVariable}表示对类型变量的潜在注释使用,其声明可能具有自身代表类型的注释用法的边界.
 * 
 * @since 1.8
 */
public interface AnnotatedTypeVariable extends AnnotatedType {

	/**
	 * 返回此类型变量的潜在注释边界.
	 * 
	 * @return 此类型变量的潜在注释边界
	 */
	AnnotatedType[] getAnnotatedBounds();
}
