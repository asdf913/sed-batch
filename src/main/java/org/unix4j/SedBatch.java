package org.unix4j;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.function.FailableFunction;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.unix4j.builder.To;
import org.unix4j.builder.Unix4jCommandBuilder;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import io.github.toolfactory.narcissus.Narcissus;

public class SedBatch {

	private static final Logger LOG = LoggerFactory.getLogger(SedBatch.class);

	public static void main(final String[] args) throws IOException {
		//
		final Map<String, String> map = toMap(args);
		//
		final File file = testAndApply(Objects::nonNull, get(map, "file"), File::new, null);
		//
		final String before = testAndApply(SedBatch::isFile, file,
				x -> FileUtils.readFileToString(x, StandardCharsets.UTF_8), null);
		//
		final String regexp = get(map, "regexp");
		//
		final String replacement = get(map, "replacement");
		//
		final String after = testAndApply((a, b) -> Boolean.logicalAnd(a != null, b != null), regexp, replacement,
				(a, b) -> toStringResult(sed(testAndApply(SedBatch::isFile, file, Unix4j::fromFile, null), a, b)),
				null);
		//
		final Patch<String> diff = testAndApply((a, b) -> a != null && b != null, before, after,
				(a, b) -> DiffUtils.diff(a, b, null), null);
		//
		if (!isEmpty(getDeltas(diff))) {
			//
			info(LOG, getAbsolutePath(file));
			//
			info(LOG, Objects.toString(diff));
			//
			if (Objects.equals(get(map, "execute"), "true")) {
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

}