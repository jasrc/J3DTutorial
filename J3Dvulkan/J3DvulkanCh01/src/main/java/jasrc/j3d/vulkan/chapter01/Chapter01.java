package jasrc.j3d.vulkan.chapter01;

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
			System.out.println("Following subchapters are available:");
			System.out.println("NONE at this time");
		}
	}

}
