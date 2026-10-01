package org.unix4j;

import java.awt.Component;
import java.awt.Container;
import java.awt.GraphicsEnvironment;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.text.JTextComponent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unix4j.builder.To;
import org.unix4j.builder.Unix4jCommandBuilder;

import com.github.difflib.DiffUtils;
import com.github.difflib.UnifiedDiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.sun.jna.platform.win32.Kernel32Util;

import io.github.toolfactory.narcissus.Narcissus;
import net.miginfocom.swing.MigLayout;

public class SedBatch extends JPanel implements ActionListener {

	private static final long serialVersionUID = -1549219888362895529L;

	private static final Logger LOG = LoggerFactory.getLogger(SedBatch.class);

	private JTextComponent tfFile, tfRegexp, tfReplacement = null;

	private AbstractButton btnFile, btnConfirm, btnExecute = null;

	private DefaultListModel<String> dlm = null;

	public static void main(final String[] args) throws IOException {
		//
		final Map<String, String> map = toMap(args);
		//
		boolean gui = false;
		//
		if (containsKey(map, "gui")) {
			//
			gui = BooleanUtils.toBooleanDefaultIfNull(Boolean.valueOf(get(map, "gui")), gui);
			//
		} // if
			//
		final String name = getName(getClass(FileSystems.getDefault()));
		//
		if (Boolean.logicalAnd(Objects.equals(name, "sun.nio.fs.MacOSXFileSystem"), !isTestMode())) {
			//
			gui = System.console() == null;
			//
		} // if
			//
		if (Boolean.logicalAnd(!gui, Objects.equals(name, "sun.nio.fs.WindowsFileSystem"))) {
			//
			final Matcher matcher = matcher(Pattern.compile("\\d+"), getName(ManagementFactory.getRuntimeMXBean()));
			//
			if (find(matcher)) {
				//
				gui = BooleanUtils.toBooleanDefaultIfNull(testAndApply(NumberUtils::isDigits, group(matcher),
						x -> endsWith(Kernel32Util.QueryFullProcessImageName(NumberUtils.toInt(x), 0), "javaw.exe"),
						null), false);
				//
			} // if
				//
		} // if
			//
		if (gui) {
			//
			final SedBatch instance = new SedBatch();
			//
			instance.setLayout(new MigLayout());
			//
			instance.add(new JLabel("File"));
			//
			instance.add(instance.tfFile = new JTextField(), String.format("wmin %1$s", 100));
			//
			instance.tfFile.setEditable(false);
			//
			final String wrap = "wrap";
			//
			instance.add(instance.btnFile = new JButton("Select File"), wrap);
			//
			instance.add(new JLabel("Regexp"));
			//
			final String growx = "growx";
			//
			instance.add(instance.tfRegexp = new JTextField(), StringUtils.joinWith(",", growx, wrap));
			//
			instance.add(new JLabel("Replacement"));
			//
			instance.add(instance.tfReplacement = new JTextField(), StringUtils.joinWith(",", growx, wrap));
			//
			instance.add(new JLabel("Confirm"));
			//
			instance.add(instance.btnConfirm = new JCheckBox(), StringUtils.joinWith(",", growx, wrap));
			//
			instance.add(new JLabel());
			//
			instance.add(instance.btnExecute = new JButton("Execute"), wrap);
			//
			instance.add(new JScrollPane(new JList<>(instance.dlm = new DefaultListModel<>())),
					String.format("span %1$s,%2$s", 3, growx));
			//
			forEach(Arrays.asList(instance.btnFile, instance.btnExecute), x -> addActionListener(x, instance));
			//
			final JFrame jFrame = testAndGet(!GraphicsEnvironment.isHeadless(), JFrame::new);
			//
			setDefaultCloseOperation(jFrame, WindowConstants.EXIT_ON_CLOSE);
			//
			add(jFrame, instance);
			//
			pack(jFrame);
			//
			testAndRun(!isTestMode(), () -> setVisible(jFrame, true));
			//
			return;
			//
		} // if
			//
		if (containsKey(map, "fileNameList")) {
			//
			final List<String> lines = testAndApply(SedBatch::isFile,
					testAndApply(Objects::nonNull, get(map, "fileNameList"), File::new, null),
					x -> FileUtils.readLines(x, StandardCharsets.UTF_8), null);
			//
			Entry<String, String> entry = null;
			//
			final boolean execute = Objects.equals(get(map, "execute"), "true");
			//
			for (int i = 0; i < size(lines); i++) {
				//
				perform(testAndApply(Objects::nonNull, get(lines, i), File::new, null), entry = ObjectUtils
						.getIfNull(entry, () -> Pair.of(get(map, "regexp"), get(map, "replacement"))), execute);
				//
			} // for
				//
		} else {
			//
			perform(testAndApply(Objects::nonNull, get(map, "file"), File::new, null),
					Pair.of(get(map, "regexp"), get(map, "replacement")), Objects.equals(get(map, "execute"), "true"));
			//
		} // if
			//
	}

	private static void addActionListener(final AbstractButton instance, final ActionListener actionListener) {
		//
		if (instance == null || actionListener == null) {
			//
			return;
			//
		} // if
			//
		final Field field = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(instance), FieldUtils::getAllFieldsList, null)),
						f -> Objects.equals(getName(f), "listenerList")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		if (field == null || Narcissus.getField(instance, field) != null) {
			//
			instance.addActionListener(actionListener);
			//
		} // if
			//
	}

	private static <T> void forEach(final Iterable<T> instance, final Consumer<? super T> action) {
		if (instance != null && (action != null || Proxy.isProxyClass(getClass(instance)))) {
			instance.forEach(action);
		}
	}

	private static void testAndRun(final boolean condition, final Runnable runnable) {
		if (condition && runnable != null) {
			runnable.run();
		}
	}

	private static void setVisible(final Component instnace, final boolean visible) {
		if (instnace != null) {
			instnace.setVisible(visible);
		}
	}

	private static void pack(final Window instance) {
		//
		if (instance == null) {
			//
			return;
			//
		} // if
			//
		final Field field = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(instance), FieldUtils::getAllFieldsList, null)),
						f -> Objects.equals(getName(f), "objectLock")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		if (field == null || Narcissus.getField(instance, field) != null) {
			//
			instance.pack();
			//
		} // if
			//
	}

	private static void add(final Container instance, final Component comp) {
		//
		if (instance == null) {
			//
			return;
			//
		} // if
			//
		final Field field = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(instance), FieldUtils::getAllFieldsList, null)),
						x -> Objects.equals(getName(x), "component")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		if (field == null || Narcissus.getField(instance, field) != null) {
			//
			instance.add(comp);
			//
		} // if
			//
	}

	private static void setDefaultCloseOperation(final JFrame instance, final int operation) {
		if (instance != null) {
			instance.setDefaultCloseOperation(operation);
		}
	}

	private static <T> T testAndGet(final boolean condition, final Supplier<T> supplier) {
		return condition && supplier != null ? supplier.get() : null;
	}

	private static boolean endsWith(final String instance, final String suffix) {
		//
		if (instance == null || suffix == null) {
			//
			return false;
			//
		} // if
			//
		final Field field = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(instance), FieldUtils::getAllFieldsList, null)),
						x -> Objects.equals(getName(x), "value")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		return (field == null || Boolean.logicalAnd(Narcissus.getField(instance, field) != null,
				Narcissus.getField(suffix, field) != null)) && instance.endsWith(suffix);
		//
	}

	private static boolean isTestMode() {
		try {
			return Class.forName("org.testng.annotations.Test") != null;
		} catch (final ClassNotFoundException e) {
			return false;
		}
	}

	private static Matcher matcher(final Pattern instance, final CharSequence input) {
		//
		if (instance == null || input == null) {
			//
			return null;
			//
		} // if
			//
		final Field normalizedPattern = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(instance), FieldUtils::getAllFieldsList, null)),
						f -> Objects.equals(getName(f), "normalizedPattern")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		final Field value = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(input), FieldUtils::getAllFieldsList, null)),
						x -> Objects.equals(getName(x), "value")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		return (normalizedPattern == null || Narcissus.getField(instance, normalizedPattern) != null)
				&& (value == null || Narcissus.getField(input, value) != null) ? instance.matcher(input) : null;
		//
	}

	private static boolean find(final Matcher instance) {
		//
		if (instance == null) {
			//
			return false;
			//
		} // if
			//
		final Field field = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(instance), FieldUtils::getAllFieldsList, null)),
						f -> Objects.equals(getName(f), "groups")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		return (field == null || Narcissus.getField(instance, field) != null) && instance.find();
		//
	}

	private static String group(final MatchResult instance) {
		//
		if (instance == null) {
			//
			return null;
			//
		} // if
			//
		final Field first = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(instance), FieldUtils::getAllFieldsList, null)),
						f -> Objects.equals(getName(f), "first")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		if (first != null && Objects.equals(first.getType(), Integer.TYPE)) {
			//
			return Narcissus.getIntField(instance, first) >= 0 ? instance.group() : null;
			//
		} // if
			//
		return instance.group();
		//
	}

	private static String getName(final RuntimeMXBean instance) {
		return instance != null ? instance.getName() : null;
	}

	private static String getName(final Class<?> instance) {
		return instance != null ? instance.getName() : null;
	}

	private static boolean containsKey(final Map<?, ?> instance, final Object key) {
		return instance != null && instance.containsKey(key);
	}

	private static void perform(final File file, final Entry<String, String> entry, final boolean execute)
			throws IOException {
		//
		final String before = testAndApply(SedBatch::isFile, file,
				x -> FileUtils.readFileToString(x, StandardCharsets.UTF_8), null);
		//
		final String regexp = getKey(entry);
		//
		final String replacement = getValue(entry);
		//
		final String after = testAndApply((a, b) -> Boolean.logicalAnd(a != null, b != null), regexp, replacement,
				(a, b) -> toStringResult(sed(testAndApply(SedBatch::isFile, file, Unix4j::fromFile, null), a, b)),
				null);
		//
		final Patch<String> diff = testAndApply((a, b) -> a != null && b != null, before, after,
				(a, b) -> DiffUtils.diff(a, b, null), null);
		//
		final List<AbstractDelta<String>> deltas = getDeltas(diff);
		//
		if (!isEmpty(deltas)) {
			//
			info(LOG, getAbsolutePath(file));
			//
			final List<String> unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(null, null,
					FileUtils.readLines(file, StandardCharsets.UTF_8), diff, 0);
			//
			for (int i = 2; i < size(unifiedDiff); i++) {
				//
				info(LOG, get(unifiedDiff, i));
				//
			} // for
				//
			if (execute) {
				//
				toFile(sed(testAndApply(SedBatch::isFile, file, Unix4j::fromFile, null), regexp, replacement), file);
				//
				info(LOG, "Updated");
				//
			} // if
				//
		} // if
			//
	}

	private static <T> List<AbstractDelta<T>> getDeltas(final Patch<T> instance) {
		//
		if (instance == null) {
			//
			return null;
			//
		} // if
			//
		final Field field = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(instance), FieldUtils::getAllFieldsList, null)),
						f -> Objects.equals(getName(f), "deltas")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		return field == null || Narcissus.getField(instance, field) != null ? instance.getDeltas() : null;
		//
	}

	private static String getAbsolutePath(final File instance) {
		return instance != null && instance.getPath() != null ? instance.getAbsolutePath() : null;
	}

	private static void info(final Logger instance, final String message) {
		if (instance != null) {
			instance.info(message);
		}
	}

	private static boolean isEmpty(final Collection<?> instance) {
		return instance == null || instance.isEmpty();
	}

	private static boolean isFile(final File instance) {
		return instance != null && instance.getPath() != null && instance.isFile();
	}

	private static <T, U, R> R testAndApply(final BiPredicate<T, U> predicate, final T t, final U u,
			final BiFunction<T, U, R> functionTrue, final BiFunction<T, U, R> functionFalse) {
		return test(predicate, t, u) ? apply(functionTrue, t, u) : apply(functionFalse, t, u);
	}

	private static <T, U, R> R apply(final BiFunction<T, U, R> instance, final T t, final U u) {
		return instance != null ? instance.apply(t, u) : null;
	}

	private static <T, U> boolean test(final BiPredicate<T, U> instance, final T t, final U u) {
		return instance != null && instance.test(t, u);
	}

	private static void toFile(final To instance, final File file) {
		if (instance != null) {
			instance.toFile(file);
		}
	}

	private static String toStringResult(final To instance) {
		return instance != null ? instance.toStringResult() : null;
	}

	private static Unix4jCommandBuilder sed(final Unix4jCommandBuilder instance, final String regexp,
			final String replacement) {
		return instance != null ? instance.sed(regexp, replacement) : instance;
	}

	private static <V> V get(final Map<?, V> instance, final Object key) {
		return instance != null ? instance.get(key) : null;
	}

	private static Map<String, String> toMap(final String... ss) {
		//
		Map<String, String> map = null;
		//
		Entry<String, String> entry = null;
		//
		for (int i = 0; i < length(ss); i++) {
			//
			if ((entry = toEntry(ArrayUtils.get(ss, i))) == null) {
				//
				continue;
				//
			} // if
				//
			put(map = ObjectUtils.getIfNull(map, LinkedHashMap::new), getKey(entry), getValue(entry));
			//
		} // for
			//
		return map;
		//
	}

	private static <K, V> void put(final Map<K, V> instance, final K key, final V value) {
		if (instance != null) {
			instance.put(key, value);
		}
	}

	private static <K> K getKey(final Entry<K, ?> instance) {
		return instance != null ? instance.getKey() : null;
	}

	private static <V> V getValue(final Entry<?, V> instance) {
		return instance != null ? instance.getValue() : null;
	}

	private static int length(final Object[] instance) {
		return instance != null ? instance.length : 0;
	}

	private static Entry<String, String> toEntry(final String string) {
		//
		final Field field = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(string), FieldUtils::getAllFieldsList, null)),
						f -> Objects.equals(getName(f), "value")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		if (string != null && field != null && Narcissus.getField(string, field) == null) {
			//
			return null;
			//
		} // if
			//
		if (Objects.equals(string, "=")) {
			//
			return Pair.of("", "");
			//
		} else if (string != null && string.length() == 2 && string.charAt(0) == '=') {
			//
			return Pair.of("", string.substring(1, string.length()));
			//
		} else if (string != null && string.length() == 2 && string.charAt(string.length() - 1) == '=') {
			//
			return Pair.of(string.substring(0, string.length() - 1), "");
			//
		} else if (string != null && string.indexOf('=') >= 0 && string.indexOf('=') == string.lastIndexOf('=')) {
			//
			return Pair.of(StringUtils.substringBefore(string, '='), StringUtils.substringAfter(string, '='));
			//
		} else if (string != null && string.length() > 2 && string.indexOf('=') != string.lastIndexOf('=')) {
			//
			return Pair.of(StringUtils.substring(string, 0, string.indexOf('=')),
					StringUtils.substring(string, string.indexOf('=') + 1));
			//
		} // if
			//
		return null;
		//
	}

	private static <E> E get(final List<E> instance, final int index) {
		return instance != null ? instance.get(index) : null;
	}

	private static int size(final Collection<?> instance) {
		return instance != null ? instance.size() : 0;
	}

	private static String getName(final Member instance) {
		return instance != null ? instance.getName() : null;
	}

	private static <T, R, A> R collect(final Stream<T> instance, final Collector<? super T, A, R> collector) {
		return instance != null && (collector != null || Proxy.isProxyClass(getClass(instance)))
				? instance.collect(collector)
				: null;
	}

	private static <T> Stream<T> filter(final Stream<T> instance, final Predicate<? super T> predicate) {
		return instance != null ? instance.filter(predicate) : instance;
	}

	private static <T> Stream<T> stream(final Collection<T> instance) {
		return instance != null ? instance.stream() : null;
	}

	private static <T, R, E extends Throwable> R testAndApply(final Predicate<T> predicate, final T value,
			final FailableFunction<T, R, E> functionTrue, final FailableFunction<T, R, E> functionFalse) throws E {
		return test(predicate, value) ? apply(functionTrue, value) : apply(functionFalse, value);
	}

	private static <T> boolean test(final Predicate<T> instance, final T value) {
		return instance != null && instance.test(value);
	}

	private static <T, R, E extends Throwable> R apply(final FailableFunction<T, R, E> instance, final T value)
			throws E {
		return instance != null ? instance.apply(value) : null;
	}

	private static Class<?> getClass(final Object instance) {
		return instance != null ? instance.getClass() : null;
	}

	@Override
	public void actionPerformed(final ActionEvent evt) {
		//
		final Object source = evt != null ? evt.getSource() : null;
		//
		if (Objects.equals(source, btnFile)) {
			//
			JFileChooser jfc = null;
			//
			try {
				//
				jfc = new JFileChooser(getCanonicalFile(new File(".")));
				//
			} catch (final IOException e) {
				//
				throw new RuntimeException(e);
				//
			} // try
				//
			if (jfc != null && Boolean.logicalAnd(!GraphicsEnvironment.isHeadless(), !isTestMode())) {
				//
				final int showOpenDialog = jfc.showOpenDialog(null);
				//
				if (showOpenDialog == JFileChooser.APPROVE_OPTION) {
					//
					setText(tfFile, getAbsolutePath(jfc.getSelectedFile()));
					//
				} else if (showOpenDialog == JFileChooser.CANCEL_OPTION) {
					//
					setText(tfFile, null);
					//
				} // if
					//
			} // if
				//
		} else if (Objects.equals(source, btnExecute)) {
			//
			final File file = testAndApply(Objects::nonNull, getText(tfFile), File::new, null);
			//
			String before = null;
			//
			try {
				//
				before = testAndApply(SedBatch::isFile, file,
						x -> FileUtils.readFileToString(x, StandardCharsets.UTF_8), null);
				//
			} catch (final IOException e) {
				//
				throw new RuntimeException(e);
				//
			} // try
				//
			final String regexp = getText(tfRegexp);
			//
			final String replacement = getText(tfReplacement);
			//
			final String after = testAndApply((a, b) -> Boolean.logicalAnd(a != null, b != null), regexp, replacement,
					(a, b) -> toStringResult(sed(testAndApply(SedBatch::isFile, file, Unix4j::fromFile, null), a, b)),
					null);
			//
			final Patch<String> diff = testAndApply((a, b) -> a != null && b != null, before, after,
					(a, b) -> DiffUtils.diff(a, b, null), null);
			//
			final List<AbstractDelta<String>> deltas = getDeltas(diff);
			//
			removeAllElements(dlm);
			//
			if (!isEmpty(deltas)) {
				//
				List<String> unifiedDiff = null;
				//
				try {
					//
					unifiedDiff = UnifiedDiffUtils.generateUnifiedDiff(null, null,
							FileUtils.readLines(file, StandardCharsets.UTF_8), diff, 0);
					//
				} catch (final IOException e) {
					//
					throw new RuntimeException(e);
					//
				} // try
					//
				for (int i = 2; i < size(unifiedDiff); i++) {
					//
					addElement(dlm, get(unifiedDiff, i));
					//
				} // for
					//
				if (isSelected(btnConfirm)) {
					//
					toFile(sed(testAndApply(SedBatch::isFile, file, Unix4j::fromFile, null), regexp, replacement),
							file);
					//
					info(LOG, "Updated");
					//
				} // if
					//
			} // if
				//
		} // if
			//
	}

	private static <E> void addElement(final DefaultListModel<E> instance, final E element) {
		//
		if (instance == null) {
			//
			return;
			//
		} // if
			//
		final Field field = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(instance), FieldUtils::getAllFieldsList, null)),
						f -> Objects.equals(getName(f), "delegate")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		if (field == null || Narcissus.getField(instance, field) != null) {
			//
			instance.addElement(element);
			//
		} // if
			//
	}

	private static void removeAllElements(final DefaultListModel<?> instance) {
		//
		if (instance == null) {
			//
			return;
			//
		} // if
			//
		final Field field = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(instance), FieldUtils::getAllFieldsList, null)),
						f -> Objects.equals(getName(f), "delegate")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		if (field == null || Narcissus.getField(instance, field) != null) {
			//
			instance.removeAllElements();
			//
		} // if
			//
	}

	private static boolean isSelected(final AbstractButton instance) {
		//
		if (instance == null) {
			//
			return false;
			//
		} // if
			//
		final Field field = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(instance), FieldUtils::getAllFieldsList, null)),
						f -> Objects.equals(getName(f), "model")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		return field == null || Narcissus.getField(instance, field) != null && instance.isSelected();
		//
	}

	private static String getText(final JTextComponent instance) {
		//
		if (instance == null) {
			//
			return null;
			//
		} // if
			//
		final Field field = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(instance), FieldUtils::getAllFieldsList, null)),
						f -> Objects.equals(getName(f), "model")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		return field == null || Narcissus.getField(instance, field) != null ? instance.getText() : null;
		//
	}

	private static void setText(final JTextComponent instance, final String text) {
		//
		if (instance == null) {
			//
			return;
			//
		} // if
			//
		final Field field = testAndApply(x -> size(x) == 1,
				collect(filter(
						stream(testAndApply(Objects::nonNull, getClass(instance), FieldUtils::getAllFieldsList, null)),
						f -> Objects.equals(getName(f), "model")), Collectors.toList()),
				x -> get(x, 0), null);
		//
		if (field == null || Narcissus.getField(instance, field) != null) {
			//
			instance.setText(text);
			//
		} // if
			//
	}

	private static File getCanonicalFile(final File instance) throws IOException {
		return instance != null && instance.getPath() != null ? instance.getCanonicalFile() : null;
	}

}