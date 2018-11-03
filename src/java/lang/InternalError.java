package java.lang;

public class InternalError extends VirtualMachineError {
	private static final long serialVersionUID = -9062593416125562365L;

	public InternalError() {
		super();
	}

	public InternalError(String message) {
		super(message);
	}

	/**
	 * 使用指定的详细消息和原因构造一个{@code InternalError}。
	 * <p>
	 * 请注意，与{@code cause}关联的详细消息<i>not</i>会自动合并到此错误的详细消息中。
	 *
	 * @param message
	 *            详细消息(保存以供以后通过{@link #getMessage()}方法检索)。
	 * @param cause
	 *            原因(保存以供以后通过{@link #getCause()}方法检索)。
	 *            (允许使用{@code null}值，并指示原因不存在或未知。)
	 * @since 1.8
	 */
	public InternalError(String message, Throwable cause) {
		super(message, cause);
	}

	/**
	 * 构造一个{@code InternalError}，其中包含{@code(cause ==
	 * null?null:cause.toString())}的指定原因和详细消息(通常包含{@code cause}的类和详细消息)。
	 *
	 * @param cause
	 *            原因(保存以供以后通过{@link #getCause()}方法检索)。
	 *            (允许使用{@code null}值，并指示原因不存在或未知。)
	 * @since 1.8
	 */
	public InternalError(Throwable cause) {
		super(cause);
	}
}
