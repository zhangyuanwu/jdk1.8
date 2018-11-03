package java.lang.reflect;

public interface TypeVariable<D extends GenericDeclaration> extends Type, AnnotatedElement {
	Type[] getBounds();

	D getGenericDeclaration();

	String getName();

	/**
	 * 返回AnnotatedType对象的数组,这些对象表示使用类型来表示此TypeVariable表示的类型参数的上限.
	 * 数组中对象的顺序对应于type参数声明中的边界顺序. 如果type参数声明没有边界,则返回长度为0的数组.
	 *
	 * @return 表示类型变量上限的对象数组
	 * @since 1.8
	 */
	AnnotatedType[] getAnnotatedBounds();
}
