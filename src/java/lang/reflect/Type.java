package java.lang.reflect;

public interface Type {
	/**
	 * 返回描述此类型的字符串,包括有关任何类型参数的信息.
	 *
	 * @implSpec 默认实现调用{@code toString}.
	 *
	 * @return 描述此类型的字符串
	 * @since 1.8
	 */
	default String getTypeName() {
		return toString();
	}
}
