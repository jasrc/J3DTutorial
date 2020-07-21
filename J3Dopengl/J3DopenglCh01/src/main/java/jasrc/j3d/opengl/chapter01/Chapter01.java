package jasrc.j3d.opengl.chapter01;

/**
 *
 *
 * @author Arnold, J.
 */
public class Chapter01 {

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		new Chapter01().execute(args);
	}

	/**
	 * @param args
	 */
	public void execute(final String[] args) {
		Class<?> clazz = null;
		if (args.length == 1) {
			try {
				clazz = Class.forName(getClass().getCanonicalName() + "_" + args[0]);
				return;
			} catch (final Exception e) {
				// ignore
			}
		}
		if (clazz != null) {
			try {
				final var objects = new Object[] { new String[0] };
				clazz.getDeclaredMethod("main", String[].class).invoke(null, objects);
				return;
			} catch (final Exception e) {
				// ignore
			}
		} else {
			System.out.println("Usage: ");
			System.out.println(getClass().getCanonicalName() + " <subchapter>");
			System.out.println("<subchapter> - e.g. " + getClass().getSimpleName() + "_2 subchapter is 2");
			System.out.println();
			System.out.println("Following subchpaters are available:");
			System.out.println("1 - Simple color example");
			System.out.println("2 - Simple color example as subclass");
			System.out.println("3 - Dynamic color example");
			System.out.println("4 - Text example (white on black)");
			System.out.println("5 - Text example (switch between black & white)");
			System.out.println("6 - Text example (dynamic color)");

		}
	}

}
