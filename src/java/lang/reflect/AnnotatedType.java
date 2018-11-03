package java.lang.reflect;

/**
 * {@code AnnotatedType}表示当前在此VM中运行的程序中可能注释使用的类型.
 * 该用途可以是Java编程语言中的任何类型,包括数组类型,参数化类型,类型变量或通配符类型.
 * 
 * @since 1.8
 */
public interface AnnotatedType extends AnnotatedElement {

	/**
	 * 返回此带注释的类型表示的基础类型.
	 *
	 * @return 此注释类型表示的类型
	 */
	public Type getType();
}
