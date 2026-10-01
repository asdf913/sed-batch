package org.unix4j;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.management.RuntimeMXBean;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
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
import java.util.stream.Stream;

import javax.swing.AbstractButton;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import org.unix4j.builder.To;
import org.unix4j.builder.Unix4jCommandBuilder;

import com.google.common.reflect.Reflection;

import io.github.toolfactory.narcissus.Narcissus;

class SedBatchTest {

	private static final String EMPTY = "";

	private static Method METHOD_GET_NAME, METHOD_GET_CLASS, METHOD_COLLECT, METHOD_GET_ABSOLUTE_PATH, METHOD_GET,
			METHOD_SIZE, METHOD_ADD_ACTION_LISTENER, METHOD_FOR_EACH, METHOD_TEST_AND_GET, METHOD_ENDS_WITH,
			METHOD_MATCHER, METHOD_FIND, METHOD_GROUP, METHOD_ADD_ELEMENT, METHOD_REMOVE_ALL_ELEMENTS,
			METHOD_IS_SELECTED, METHOD_GET_TEXT, METHOD_SET_TEXT = null;

	@BeforeClass
	static void beforeClass() throws NoSuchMethodException {
		//
		final Class<?> clz = SedBatch.class;
		//
		(METHOD_GET_NAME = clz.getDeclaredMethod("getName", Member.class)).setAccessible(true);
		//
		(METHOD_GET_CLASS = clz.getDeclaredMethod("getClass", Object.class)).setAccessible(true);
		//
		(METHOD_COLLECT = clz.getDeclaredMethod("collect", Stream.class, Collector.class)).setAccessible(true);
		//
		(METHOD_GET_ABSOLUTE_PATH = clz.getDeclaredMethod("getAbsolutePath", File.class)).setAccessible(true);
		//
		(METHOD_GET = clz.getDeclaredMethod("get", List.class, Integer.TYPE)).setAccessible(true);
		//
		(METHOD_SIZE = clz.getDeclaredMethod("size", Collection.class)).setAccessible(true);
		//
		(METHOD_ADD_ACTION_LISTENER = clz.getDeclaredMethod("addActionListener", AbstractButton.class,
				ActionListener.class)).setAccessible(true);
		//
		(METHOD_FOR_EACH = clz.getDeclaredMethod("forEach", Iterable.class, Consumer.class)).setAccessible(true);
		//
		(METHOD_TEST_AND_GET = clz.getDeclaredMethod("testAndGet", Boolean.TYPE, Supplier.class)).setAccessible(true);
		//
		(METHOD_ENDS_WITH = clz.getDeclaredMethod("endsWith", String.class, String.class)).setAccessible(true);
		//
		(METHOD_MATCHER = clz.getDeclaredMethod("matcher", Pattern.class, CharSequence.class)).setAccessible(true);
		//
		(METHOD_FIND = clz.getDeclaredMethod("find", Matcher.class)).setAccessible(true);
		//
		(METHOD_GROUP = clz.getDeclaredMethod("group", MatchResult.class)).setAccessible(true);
		//
		(METHOD_ADD_ELEMENT = clz.getDeclaredMethod("addElement", DefaultListModel.class, Object.class))
				.setAccessible(true);
		//
		(METHOD_REMOVE_ALL_ELEMENTS = clz.getDeclaredMethod("removeAllElements", DefaultListModel.class))
				.setAccessible(true);
		//
		(METHOD_IS_SELECTED = clz.getDeclaredMethod("isSelected", AbstractButton.class)).setAccessible(true);
		//
		(METHOD_GET_TEXT = clz.getDeclaredMethod("getText", JTextComponent.class)).setAccessible(true);
		//
		(METHOD_SET_TEXT = clz.getDeclaredMethod("setText", JTextComponent.class, String.class)).setAccessible(true);
		//
	}

	private static class IH implements InvocationHandler {

		private Boolean test, isEmpty, containsKey = null;

		private Integer size = null;

		@Override
		public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
			//
			if (Objects.equals(method != null ? method.getReturnType() : null, Void.TYPE)) {
				//
				return null;
				//
			} // if
				//
			final String name = getName(method);
			//
			if (proxy instanceof Collection) {
				//
				if (Objects.equals(name, "size")) {
					//
					return size;
					//
				} else if (Objects.equals(name, "isEmpty")) {
					//
					return isEmpty;
					//
				} else if (Objects.equals(name, "stream")) {
					//
					return null;
					//
				} // if
					//
			} // if
				//
			if (proxy instanceof Member && Objects.equals(name, "getName")) {
				//
				return null;
				//
			} else if (proxy instanceof Map) {
				//
				if (contains(Arrays.asList("get", "put"), name)) {
					//
					return null;
					//
				} else if (Objects.equals(name, "containsKey")) {
					//
					return containsKey;
					//
				} // if
					//
			} else if (proxy instanceof List && Objects.equals(name, "get")) {
				//
				return null;
				//
			} else if ((proxy instanceof Predicate || proxy instanceof BiPredicate) && Objects.equals(name, "test")) {
				//
				return test;
				//
			} else if (proxy instanceof Entry && contains(Arrays.asList("getValue", "getKey"), name)) {
				//
				return null;
				//
			} else if (proxy instanceof To && contains(Arrays.asList("toFile", "toStringResult"), name)) {
				//
				return null;
				//
			} else if (proxy instanceof Unix4jCommandBuilder && Objects.equals(name, "sed")) {
				//
				return null;
				//
			} else if ((proxy instanceof FailableFunction || proxy instanceof BiFunction)
					&& Objects.equals(name, "apply")) {
				//
				return null;
				//
			} else if (proxy instanceof Stream) {
				//
				if (contains(Arrays.asList("collect"), name)) {
					//
					return null;
					//
				} else if (Objects.equals(name, "filter")) {
					//
					return proxy;
					//
				} // if
					//
				return null;
				//
			} else if (proxy instanceof RuntimeMXBean && Objects.equals(name, "getName")) {
				//
				return null;
				//
			} else if (proxy instanceof MatchResult && Objects.equals(name, "group")) {
				//
				return null;
				//
			} else if (proxy instanceof Supplier && Objects.equals(name, "get")) {
				//
				return null;
				//
			} // if
				//
			throw new Throwable(name);
			//
		}

	}

	private static String getName(final Member instance) throws Throwable {
		try {
			final Object obj = invoke(METHOD_GET_NAME, null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (String) obj;
			}
			throw new Throwable(Objects.toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static Object invoke(final Method method, final Object instance, final Object... args)
			throws IllegalAccessException, InvocationTargetException {
		return method != null && method.getDeclaringClass() != null ? method.invoke(instance, args) : null;
	}

	private IH ih = null;

	private SedBatch instance = null;

	private Pattern pattern = null;

	private DefaultListModel<?> dlm = null;

	private AbstractButton abstractButton = null;

	private JTextComponent jTextComponent = null;

	@BeforeMethod
	void beforeMethod() {
		//
		ih = new IH();
		//
		instance = cast(SedBatch.class, Narcissus.allocateInstance(SedBatch.class));
		//
		pattern = Pattern.compile("\\d+");
		//
		dlm = new DefaultListModel<>();
		//
		abstractButton = new JButton();
		//
		jTextComponent = new JTextField();
		//
	}

	private static <T> T cast(final Class<T> clz, final Object instance) {
		return clz != null && clz.isInstance(instance) ? clz.cast(instance) : null;
	}

	@Test

	void testNull() throws Throwable {
		//
		final Method[] ms = SedBatch.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Class<?> parameterType = null;
		//
		Object result = null;
		//
		String toString = null;
		//
		Collection<Object> collection = null;
		//
		Object[] os = null;
		//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null) {
				//
				continue;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if (Objects.equals(parameterType = ArrayUtils.get(parameterTypes, j), Integer.TYPE)) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else if (Objects.equals(parameterType, Boolean.TYPE)) {
					//
					add(collection, Boolean.TRUE);
					//
				} else {
					//
					add(collection, null);
					//
				} // if
					//
			} // for
				//
			os = toArray(collection);
			//
			result = Modifier.isStatic(m.getModifiers()) ? Narcissus.invokeStaticMethod(m, os)
					: Narcissus.invokeMethod(instance, m, os);
			//
			toString = Objects.toString(m);
			//
			if (contains(Arrays.asList(Integer.TYPE, Boolean.TYPE), m.getReturnType())) {
				//
				Assert.assertNotNull(result, toString);
				//
			} else {
				//
				Assert.assertNull(result, toString);
				//
			} // if
				//
		} // for
			//
	}

	@Test

	void testNotNull() throws Throwable {
		//
		final Method[] ms = SedBatch.class.getDeclaredMethods();
		//
		Method m = null;
		//
		Class<?>[] parameterTypes = null;
		//
		Class<?> parameterType = null;
		//
		Object result = null;
		//
		String toString, name = null;
		//
		Collection<Object> collection = null;
		//
		Object[] os = null;
		//
		for (int i = 0; ms != null && i < ms.length; i++) {
			//
			if ((m = ArrayUtils.get(ms, i)) == null || m.isSynthetic()
					|| (parameterTypes = m.getParameterTypes()) == null) {
				//
				continue;
				//
			} // if
				//
			clear(collection = ObjectUtils.getIfNull(collection, ArrayList::new));
			//
			for (int j = 0; j < parameterTypes.length; j++) {
				//
				if (Objects.equals(parameterType = ArrayUtils.get(parameterTypes, j), Integer.TYPE)) {
					//
					add(collection, Integer.valueOf(0));
					//
				} else if (Objects.equals(parameterType, Boolean.TYPE)) {
					//
					add(collection, Boolean.TRUE);
					//
				} else if (parameterType != null && parameterType.isInterface()) {
					//
					if ((ih = ObjectUtils.getIfNull(ih, IH::new)) != null) {
						//
						final List<Field> fs = FieldUtils.getAllFieldsList(getClass(ih));
						//
						Field f = null;
						//
						for (int k = 0; k < size(fs); k++) {
							//
							if ((f = get(fs, k)) == null) {
								//
								continue;
								//
							} // if
								//
							final Class<?> type = f.getType();
							//
							if (Objects.equals(type, Boolean.class)) {
								//
								Narcissus.setField(ih, f, Boolean.TRUE);
								//
							} else if (Objects.equals(type, Integer.class)) {
								//
								Narcissus.setField(ih, f, Integer.valueOf(0));
								//
							} // if
								//
						} // for
							//
					} // if
						//
					add(collection, Reflection.newProxy(parameterType, ih));
					//
				} else if (parameterType != null && parameterType.isArray()) {
					//
					add(collection, Array.newInstance(parameterType.getComponentType(), 0));
					//
				} else if (Objects.equals(parameterType, Class.class)) {
					//
					add(collection, Class.class);
					//
				} else if (Objects.equals(parameterType, Component.class)
						|| Objects.equals(parameterType, JTextComponent.class)) {
					//
					add(collection, Narcissus.allocateInstance(JTextField.class));
					//
				} else if (Objects.equals(parameterType, AbstractButton.class)) {
					//
					add(collection, Narcissus.allocateInstance(JButton.class));
					//
				} else {
					//
					add(collection, Narcissus.allocateInstance(parameterType));
					//
				} // if
					//
			} // for
				//
			os = toArray(collection);
			//
			result = Modifier.isStatic(m.getModifiers()) ? Narcissus.invokeStaticMethod(m, os)
					: Narcissus.invokeMethod(instance, m, os);
			//
			toString = Objects.toString(m);
			//
			if (contains(Arrays.asList(Integer.TYPE, Boolean.TYPE), m.getReturnType())
					|| Boolean.logicalAnd(Objects.equals(name = getName(m), "getClass"),
							Arrays.equals(parameterTypes, new Object[] { Object.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "filter"),
							Arrays.equals(parameterTypes, new Object[] { Stream.class, Predicate.class }))
					|| Boolean.logicalAnd(Objects.equals(name, "getName"),
							Arrays.equals(parameterTypes, new Object[] { Class.class }))) {
				//
				Assert.assertNotNull(result, toString);
				//
			} else {
				//
				Assert.assertNull(result, toString);
				//
			} // if
				//
		} // for
			//
	}

	private static <E> E get(final List<E> instance, final int index) throws Throwable {
		try {
			return (E) invoke(METHOD_GET, null, instance, index);
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static int size(final Collection<?> instance) throws Throwable {
		try {
			final Object obj = invoke(METHOD_SIZE, null, instance);
			if (obj instanceof Integer) {
				return ((Integer) obj).intValue();
			}
			throw new Throwable(Objects.toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static Class<?> getClass(final Object instance) throws Throwable {
		try {
			final Object obj = invoke(METHOD_GET_CLASS, null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof Class) {
				return (Class) obj;
			}
			throw new Throwable(Objects.toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static boolean contains(final Collection<?> instance, final Object item) {
		return instance != null && instance.contains(item);
	}

	private static <E> void add(final Collection<E> instance, final E item) {
		if (instance != null) {
			instance.add(item);
		}
	}

	private static void clear(final Collection<?> instance) {
		if (instance != null) {
			instance.clear();
		}
	}

	private static Object[] toArray(final Collection<?> instance) {
		return instance != null ? instance.toArray() : null;
	}

	@Test
	void testMain() throws Throwable {
		//
		SedBatch.main(new String[] { "gui=true" });
		//
		SedBatch.main(new String[] { "=", "= ", " =", "== " });
		//
		SedBatch.main(new String[] { "file=." });
		//
		final File file = File.createTempFile(nextAlphanumeric(RandomStringUtils.secure(), 3), null, new File("."));
		//
		final String string = "123";
		//
		final Charset charset = StandardCharsets.UTF_8;
		//
		if (file != null && file.exists()) {
			//
			FileUtils.writeStringToFile(file, string, charset);
			//
			file.deleteOnExit();
			//
		} // if
			//
		final String absolutePath = getAbsolutePath(file);
		//
		SedBatch.main(new String[] { "file=" + absolutePath });
		//
		SedBatch.main(new String[] { "file=" + absolutePath, "regexp=\\d+" });
		//
		Assert.assertEquals(FileUtils.readFileToString(file, charset), string);
		//
		SedBatch.main(new String[] { "file=" + absolutePath, "regexp=\\d+", "replacement=" });
		//
		Assert.assertEquals(FileUtils.readFileToString(file, charset), string);
		//
		SedBatch.main(new String[] { "file=" + absolutePath, "regexp=\\d+", "replacement=", "execute=true" });
		//
		Assert.assertEquals(FileUtils.readFileToString(file, charset), EMPTY);
		//
		SedBatch.main(new String[] { "fileNameList=." });
		//
		FileUtils.writeStringToFile(file, ".", charset);
		//
		SedBatch.main(new String[] { "fileNameList=" + absolutePath });
		//
		FileUtils.deleteQuietly(file);
		//
	}

	private static String getAbsolutePath(final File instance) throws Throwable {
		try {
			final Object obj = invoke(METHOD_GET_ABSOLUTE_PATH, null, instance);
			if (obj == null) {
				return null;
			} else if (obj instanceof String) {
				return (String) obj;
			}
			throw new Throwable(Objects.toString(getClass(obj)));
		} catch (final InvocationTargetException e) {
			throw e.getTargetException();
		}
	}

	private static String nextAlphanumeric(final RandomStringUtils instnace, final int count) {
		return instnace != null ? instnace.nextAlphanumeric(count) : null;
	}

	@Test
	void testActionPerformed() throws Exception {
		//
		if (instance == null) {
			//
			return;
			//
		} // if
			//
		final AbstractButton btnExecute = abstractButton;
		//
		FieldUtils.writeDeclaredField(instance, "btnExecute", btnExecute, true);
		//
		instance.actionPerformed(new ActionEvent(btnExecute, 0, null));
		//
	}

	@Test
	void testCollect() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNull(invoke(METHOD_COLLECT, null,
				Reflection.newProxy(Stream.class, ih = ObjectUtils.getIfNull(ih, IH::new)), null));
		//
		Assert.assertNull(invoke(METHOD_COLLECT, null, Stream.empty(), null));
		//
	}

	@Test
	void testAddActionListener() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNull(invoke(METHOD_ADD_ACTION_LISTENER, null, abstractButton, null));
		//
	}

	@Test
	void testForEach() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNull(invoke(METHOD_FOR_EACH, null,
				Reflection.newProxy(Iterable.class, ObjectUtils.getIfNull(ih, IH::new)), null));
		//
		Assert.assertNull(invoke(METHOD_FOR_EACH, null, Collections.emptySet(), null));
		//
	}

	@Test
	void testTestAndGet() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNull(invoke(METHOD_TEST_AND_GET, null, Boolean.FALSE, null));
		//
	}

	@Test
	void testEndsWith() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertEquals(invoke(METHOD_ENDS_WITH, null, EMPTY, null), Boolean.FALSE);
		//
		Assert.assertEquals(invoke(METHOD_ENDS_WITH, null, EMPTY, EMPTY), Boolean.TRUE);
		//
		final String s = "s";
		//
		Assert.assertEquals(invoke(METHOD_ENDS_WITH, null, s, EMPTY), Boolean.TRUE);
		//
		Assert.assertEquals(invoke(METHOD_ENDS_WITH, null, EMPTY, s), Boolean.FALSE);
		//
	}

	@Test
	void testMatcher() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNull(invoke(METHOD_MATCHER, null, pattern, null));
		//
		Assert.assertNull(invoke(METHOD_MATCHER, null, pattern, Narcissus.allocateInstance(String.class)));
		//
	}

	@Test
	void testFind() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertEquals(invoke(METHOD_FIND, null, invoke(METHOD_MATCHER, null, pattern, EMPTY)), Boolean.FALSE);
		//
		Assert.assertEquals(invoke(METHOD_FIND, null, invoke(METHOD_MATCHER, null, pattern, Integer.toString(1))),
				Boolean.TRUE);
		//
	}

	@Test
	void testGroup() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNull(invoke(METHOD_GROUP, null, invoke(METHOD_MATCHER, null, pattern, EMPTY)));
		//
		final int one = 1;
		//
		final Matcher matcher = cast(Matcher.class, invoke(METHOD_MATCHER, null, pattern, Integer.toString(one)));
		//
		if (matcher != null && matcher.find()) {
			//
			Assert.assertEquals(invoke(METHOD_GROUP, null, matcher), Integer.toString(one));
			//
		} // if
			//
	}

	@Test
	void testAddElement() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNull(invoke(METHOD_ADD_ELEMENT, null, dlm, null));
		//
	}

	@Test
	void testRemoveAllElements() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNull(invoke(METHOD_REMOVE_ALL_ELEMENTS, null, dlm));
		//
	}

	@Test
	void testIsSelected() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertEquals(invoke(METHOD_IS_SELECTED, null, abstractButton), Boolean.FALSE);
		//
		if (abstractButton != null) {
			//
			abstractButton.setSelected(true);
			//
		} // if
			//
		Assert.assertEquals(invoke(METHOD_IS_SELECTED, null, abstractButton), Boolean.TRUE);
		//
	}

	@Test
	void testGetText() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertEquals(invoke(METHOD_GET_TEXT, null, jTextComponent), EMPTY);
		//
	}

	@Test
	void testSetText() throws IllegalAccessException, InvocationTargetException {
		//
		Assert.assertNull(invoke(METHOD_SET_TEXT, null, jTextComponent, null));
		//
	}

}