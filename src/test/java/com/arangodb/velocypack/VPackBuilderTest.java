package com.arangodb.velocypack;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.math.BigInteger;
import java.util.Date;

import org.junit.Test;

import com.arangodb.velocypack.exception.VPackBuilderNeedOpenCompoundException;
import com.arangodb.velocypack.exception.VPackBuilderNumberOutOfRangeException;
import com.arangodb.velocypack.exception.VPackBuilderUnexpectedValueException;
import com.arangodb.velocypack.exception.VPackException;

/**
 * @author Mark - mark at arangodb.com
 *
 */
public class VPackBuilderTest {

	@Test
	public void empty() {
		final VPackSlice slice = new VPackBuilder().slice();
		assertThat(slice.isNone(), is(true));
	}

	@Test
	public void addNull() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.NULL));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isNull(), is(true));
	}

	@Test
	public void addBooleanTrue() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(true));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isBoolean(), is(true));
		assertThat(slice.getAsBoolean(), is(true));
	}

	@Test
	public void addBooleanFalse() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(false));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isBoolean(), is(true));
		assertThat(slice.getAsBoolean(), is(false));
	}

	@Test
	public void addBooleanNull() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final Boolean b = null;
		builder.add(new Value(b));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isNull(), is(true));
	}

	@Test
	public void addDouble() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final double value = Double.MAX_VALUE;
		builder.add(new Value(value));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isDouble(), is(true));
		assertThat(slice.getAsDouble(), is(value));
	}

	@Test
	public void addIntegerAsSmallIntMin() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final int value = -6;
		builder.add(new Value(value, ValueType.SMALLINT));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isSmallInt(), is(true));
		assertThat(slice.getAsInt(), is(value));
	}

	@Test
	public void addIntegerAsSmallIntMax() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final int value = 9;
		builder.add(new Value(value, ValueType.SMALLINT));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isSmallInt(), is(true));
		assertThat(slice.getAsInt(), is(value));
	}

	@Test(expected = VPackBuilderNumberOutOfRangeException.class)
	public void addIntegerAsSmallIntOutofRange() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final int value = Integer.MAX_VALUE;
		builder.add(new Value(value, ValueType.SMALLINT));
	}

	@Test
	public void addLongAsSmallIntMin() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final long value = -6;
		builder.add(new Value(value, ValueType.SMALLINT));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isSmallInt(), is(true));
		assertThat(slice.getAsLong(), is(value));
	}

	@Test
	public void addLongAsSmallIntMax() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final long value = 9;
		builder.add(new Value(value, ValueType.SMALLINT));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isSmallInt(), is(true));
		assertThat(slice.getAsLong(), is(value));
	}

	@Test(expected = VPackBuilderNumberOutOfRangeException.class)
	public void addLongAsSmallIntOutofRange() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final long value = Long.MAX_VALUE;
		builder.add(new Value(value, ValueType.SMALLINT));
	}

	@Test
	public void addBigIntegerAsSmallIntMin() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final BigInteger value = BigInteger.valueOf(-6);
		builder.add(new Value(value, ValueType.SMALLINT));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isSmallInt(), is(true));
		assertThat(slice.getAsBigInteger(), is(value));
	}

	@Test
	public void addBigIntegerAsSmallIntMax() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final BigInteger value = BigInteger.valueOf(9);
		builder.add(new Value(value, ValueType.SMALLINT));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isSmallInt(), is(true));
		assertThat(slice.getAsBigInteger(), is(value));
	}

	@Test(expected = VPackBuilderNumberOutOfRangeException.class)
	public void addBigIntegerAsSmallIntOutofRange() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final BigInteger value = BigInteger.valueOf(Long.MAX_VALUE);
		builder.add(new Value(value, ValueType.SMALLINT));
	}

	@Test
	public void addIntegerAsInt() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final int value = Integer.MAX_VALUE;
		builder.add(new Value(value, ValueType.INT));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isInt(), is(true));
		assertThat(slice.getAsInt(), is(value));
	}

	@Test
	public void addLongAsInt() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final long value = Long.MAX_VALUE;
		builder.add(new Value(value, ValueType.INT));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isInt(), is(true));
		assertThat(slice.getAsLong(), is(value));
	}

	@Test
	public void addBigIntegerAsInt() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final BigInteger value = BigInteger.valueOf(Long.MAX_VALUE);
		builder.add(new Value(value, ValueType.INT));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isInt(), is(true));
		assertThat(slice.getAsBigInteger(), is(value));
	}

	@Test
	public void addLongAsUInt() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final long value = Long.MAX_VALUE;
		builder.add(new Value(value, ValueType.UINT));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isUInt(), is(true));
		assertThat(slice.getAsLong(), is(value));
	}

	@Test
	public void addBigIntegerAsUInt() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final BigInteger value = BigInteger.valueOf(Long.MAX_VALUE);
		builder.add(new Value(value, ValueType.UINT));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isUInt(), is(true));
		assertThat(slice.getAsBigInteger(), is(value));
	}

	@Test(expected = VPackBuilderUnexpectedValueException.class)
	public void addLongAsUIntNegative() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final long value = -10;
		builder.add(new Value(value, ValueType.UINT));
	}

	@Test(expected = VPackBuilderUnexpectedValueException.class)
	public void addBigIntegerAsUIntNegative() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final BigInteger value = BigInteger.valueOf(-10);
		builder.add(new Value(value, ValueType.UINT));
	}

	@Test
	public void addUTCDate() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final Date date = new Date();
		builder.add(new Value(date));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isDate(), is(true));
		assertThat(slice.getAsDate(), is(date));
	}

	@Test
	public void addStringShort() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final String s = "Hallo Welt!";
		builder.add(new Value(s));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isString(), is(true));
		assertThat(slice.getAsString(), is(s));
	}

	@Test
	public void addStringLong() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		final String s = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus.";
		builder.add(new Value(s));

		final VPackSlice slice = builder.slice();
		assertThat(slice.isString(), is(true));
		assertThat(slice.getAsString(), is(s));
	}

	@Test
	public void emptyArray() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.ARRAY));
		builder.close();

		final VPackSlice slice = builder.slice();
		assertThat(slice.isArray(), is(true));
		assertThat(slice.getLength(), is(0));
		try {
			slice.get(0);
			fail();
		} catch (final IndexOutOfBoundsException e) {

		}
	}

	@Test
	public void compactArray() throws VPackException {
		final long[] expected = { 1, 16 };
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.ARRAY, true));
		for (final long l : expected) {
			builder.add(new Value(l));
		}
		builder.close();

		final VPackSlice slice = builder.slice();
		assertThat(slice.isArray(), is(true));
		assertThat(slice.getLength(), is(2));
		for (int i = 0; i < expected.length; i++) {
			final VPackSlice at = slice.get(i);
			assertThat(at.isInteger(), is(true));
			assertThat(at.getAsLong(), is(expected[i]));
		}
	}

	@Test
	public void arrayItemsSameLength() throws VPackException {
		VPackSlice sliceNotSame;
		{
			final VPackBuilder builder = new VPackBuilder();
			builder.add(new Value(ValueType.ARRAY));
			builder.add(new Value("aa"));
			builder.add(new Value("a"));
			builder.close();
			sliceNotSame = builder.slice();
		}
		VPackSlice sliceSame;
		{
			final VPackBuilder builder = new VPackBuilder();
			builder.add(new Value(ValueType.ARRAY));
			builder.add(new Value("aa"));
			builder.add(new Value("aa"));
			builder.close();
			sliceSame = builder.slice();
		}
		assertThat(sliceSame.getByteSize() < sliceNotSame.getByteSize(), is(true));
	}

	@Test
	public void unindexedArray() throws VPackException {
		final long[] expected = { 1, 16 };
		final VPackBuilder builder = new VPackBuilder();
		builder.getOptions().setBuildUnindexedArrays(true);
		builder.add(new Value(ValueType.ARRAY, false));
		for (final long l : expected) {
			builder.add(new Value(l));
		}
		builder.close();

		final VPackSlice slice = builder.slice();
		assertThat(slice.isArray(), is(true));
		assertThat(slice.getLength(), is(2));
		for (int i = 0; i < expected.length; i++) {
			final VPackSlice at = slice.get(i);
			assertThat(at.isInteger(), is(true));
			assertThat(at.getAsLong(), is(expected[i]));
		}
	}

	@Test
	public void indexedArray() throws VPackException {
		final long[] values = { 1, 2, 3 };
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.ARRAY));
		for (final long l : values) {
			builder.add(new Value(l));
		}
		builder.close();

		final VPackSlice slice = builder.slice();
		assertThat(slice.isArray(), is(true));
		assertThat(slice.getLength(), is(3));
	}

	@Test
	public void indexedArray2ByteLength() throws VPackException {
		final int valueCount = 100;
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.ARRAY));
		for (long i = 0; i < valueCount; i++) {
			builder.add(new Value(i
					+ "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus."));
		}
		builder.close();

		final VPackSlice slice = builder.slice();
		assertThat(slice.head(), is((byte) 0x07));
		assertThat(slice.isArray(), is(true));
		assertThat(slice.getLength(), is(valueCount));
	}

	@Test
	public void indexedArray2ByteLengthNoIndexTable() throws VPackException {
		final int valueCount = 100;
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.ARRAY));
		for (long i = 0; i < valueCount; i++) {
			builder.add(new Value(
					"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus."));
		}
		builder.close();

		final VPackSlice slice = builder.slice();
		assertThat(slice.head(), is((byte) 0x03));
		assertThat(slice.isArray(), is(true));
		assertThat(slice.getLength(), is(valueCount));
	}

	@Test
	public void indexedArray4ByteLength() throws VPackException {
		final int valueCount = 200;
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.ARRAY));
		for (long i = 0; i < valueCount; i++) {
			builder.add(new Value(
					"Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus."));
		}
		builder.close();

		final VPackSlice slice = builder.slice();
		assertThat(slice.head(), is((byte) 0x04));
		assertThat(slice.isArray(), is(true));
		assertThat(slice.getLength(), is(valueCount));
	}

	@Test
	public void indexedArray4ByteLengthNoIndexTable() throws VPackException {
		final int valueCount = 200;
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.ARRAY));
		for (long i = 0; i < valueCount; i++) {
			builder.add(new Value(i
					+ "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate eleifend tellus."));
		}
		builder.close();

		final VPackSlice slice = builder.slice();
		assertThat(slice.head(), is((byte) 0x08));
		assertThat(slice.isArray(), is(true));
		assertThat(slice.getLength(), is(valueCount));
	}

	@Test
	public void arrayInArray() throws VPackException {
		final long[][] values = { { 1, 2, 3 }, { 1, 2, 3 } };
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.ARRAY));
		for (final long[] ls : values) {
			builder.add(new Value(ValueType.ARRAY));
			for (final long l : ls) {
				builder.add(new Value(l));
			}
			builder.close();
		}
		builder.close();

		final VPackSlice slice = builder.slice();
		assertThat(slice.isArray(), is(true));
		assertThat(slice.getLength(), is(values.length));
		for (int i = 0; i < values.length; i++) {
			final VPackSlice ls = slice.get(i);
			assertThat(ls.isArray(), is(true));
			assertThat(ls.getLength(), is(values[i].length));
			for (int j = 0; j < values[i].length; j++) {
				final VPackSlice l = ls.get(j);
				assertThat(l.isInteger(), is(true));
				assertThat(l.getAsLong(), is(values[i][j]));
			}
		}
	}

	@Test
	public void arrayInArrayInArray() throws VPackException {
		final long[][][] values = { { { 1, 2, 3 } } };
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.ARRAY));
		for (final long[][] lss : values) {
			builder.add(new Value(ValueType.ARRAY));
			for (final long[] ls : lss) {
				builder.add(new Value(ValueType.ARRAY));
				for (final long l : ls) {
					builder.add(new Value(l));
				}
				builder.close();
			}
			builder.close();
		}
		builder.close();

		final VPackSlice slice = builder.slice();
		assertThat(slice.isArray(), is(true));
		assertThat(slice.getLength(), is(values.length));
		for (int i = 0; i < values.length; i++) {
			final VPackSlice lls = slice.get(i);
			assertThat(lls.isArray(), is(true));
			assertThat(lls.getLength(), is(values[i].length));
			for (int j = 0; j < values[i].length; j++) {
				final VPackSlice ls = lls.get(i);
				assertThat(ls.isArray(), is(true));
				assertThat(ls.getLength(), is(values[i][j].length));
				for (int k = 0; k < values[i][j].length; k++) {
					final VPackSlice l = ls.get(k);
					assertThat(l.isInteger(), is(true));
					assertThat(l.getAsLong(), is(values[i][j][k]));
				}
			}

		}
	}

	@Test
	public void emptyObject() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.OBJECT));
		builder.close();

		final VPackSlice slice = builder.slice();
		assertThat(slice.isObject(), is(true));
		assertThat(slice.getLength(), is(0));
		final VPackSlice a = slice.get("a");
		assertThat(a.isNone(), is(true));
		try {
			slice.keyAt(0);
			fail();
		} catch (final IndexOutOfBoundsException e) {

		}
		try {
			slice.valueAt(0);
			fail();
		} catch (final IndexOutOfBoundsException e) {

		}
	}

	@Test
	public void compactObject() throws VPackException {
		// {"a": 12, "b": true, "c": "xyz"}
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.OBJECT, true));
		builder.add("a", new Value(12));
		builder.add("b", new Value(true));
		builder.add("c", new Value("xyz"));
		builder.close();

		final VPackSlice slice = builder.slice();
		assertThat(slice.isObject(), is(true));
		assertThat(slice.getLength(), is(3));
		assertThat(slice.get("a").getAsLong(), is(12L));
		assertThat(slice.get("b").getAsBoolean(), is(true));
		assertThat(slice.get("c").getAsString(), is("xyz"));
	}

	@Test
	public void unindexedObject() throws VPackException {
		// {"a": 12, "b": true, "c": "xyz"}
		final VPackBuilder builder = new VPackBuilder();
		builder.getOptions().setBuildUnindexedObjects(true);
		builder.add(new Value(ValueType.OBJECT, false));
		builder.add("a", new Value(12));
		builder.add("b", new Value(true));
		builder.add("c", new Value("xyz"));
		builder.close();

		final VPackSlice slice = builder.slice();
		assertThat(slice.isObject(), is(true));
		assertThat(slice.getLength(), is(3));
		assertThat(slice.get("a").getAsLong(), is(12L));
		assertThat(slice.get("b").getAsBoolean(), is(true));
		assertThat(slice.get("c").getAsString(), is("xyz"));
	}

	@Test
	public void indexedObject() throws VPackException {
		// {"a": 12, "b": true, "c": "xyz"}
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.OBJECT));
		builder.add("a", new Value(12));
		builder.add("b", new Value(true));
		builder.add("c", new Value("xyz"));
		builder.close();

		final VPackSlice slice = builder.slice();
		assertThat(slice.isObject(), is(true));
		assertThat(slice.getLength(), is(3));
		assertThat(slice.get("a").getAsLong(), is(12L));
		assertThat(slice.get("b").getAsBoolean(), is(true));
		assertThat(slice.get("c").getAsString(), is("xyz"));
	}

	@Test
	public void objectInObject() throws VPackException {
		// {"a":{"a1":1,"a2":2},"b":{"b1":1,"b2":1}}
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.OBJECT));
		{
			builder.add("a", new Value(ValueType.OBJECT));
			builder.add("a1", new Value(1));
			builder.add("a2", new Value(2));
			builder.close();
		}
		{
			builder.add("b", new Value(ValueType.OBJECT));
			builder.add("b1", new Value(1));
			builder.add("b2", new Value(2));
			builder.close();
		}
		builder.close();

		final VPackSlice slice = builder.slice();
		assertThat(slice.isObject(), is(true));
		assertThat(slice.getLength(), is(2));
		{
			final VPackSlice a = slice.get("a");
			assertThat(a.isObject(), is(true));
			assertThat(a.getLength(), is(2));
			assertThat(a.get("a1").getAsLong(), is(1L));
			assertThat(a.get("a2").getAsLong(), is(2L));
		}
		{
			final VPackSlice b = slice.get("b");
			assertThat(b.isObject(), is(true));
			assertThat(b.getLength(), is(2));
			assertThat(b.get("b1").getAsLong(), is(1L));
			assertThat(b.get("b2").getAsLong(), is(2L));
		}
	}

	@Test
	public void objectInObjectInObject() throws VPackException {
		// {"a":{"b":{"c":{"d":true}}}
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.OBJECT));
		builder.add("a", new Value(ValueType.OBJECT));
		builder.add("b", new Value(ValueType.OBJECT));
		builder.add("c", new Value(ValueType.OBJECT));
		builder.add("d", new Value(true));
		builder.close();
		builder.close();
		builder.close();
		builder.close();

		final VPackSlice slice = builder.slice();
		assertThat(slice.isObject(), is(true));
		assertThat(slice.getLength(), is(1));
		final VPackSlice a = slice.get("a");
		assertThat(a.isObject(), is(true));
		assertThat(a.getLength(), is(1));
		final VPackSlice b = a.get("b");
		assertThat(b.isObject(), is(true));
		assertThat(b.getLength(), is(1));
		final VPackSlice c = b.get("c");
		assertThat(c.isObject(), is(true));
		assertThat(c.getLength(), is(1));
		final VPackSlice d = c.get("d");
		assertThat(d.isBoolean(), is(true));
		assertThat(d.isTrue(), is(true));
	}

	@Test
	public void objectAttributeNotFound() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.OBJECT));
		builder.add("a", new Value("a"));
		builder.close();
		final VPackSlice vpack = builder.slice();
		assertThat(vpack.isObject(), is(true));
		final VPackSlice b = vpack.get("b");
		assertThat(b.isNone(), is(true));
	}

	@Test
	public void object1ByteOffset() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.OBJECT));
		final int size = 5;
		for (int i = 0; i < size; i++) {
			builder.add(String.valueOf(i), new Value(ValueType.OBJECT));
			for (int j = 0; j < size; j++) {
				builder.add(String.valueOf(j), new Value("test"));
			}
			builder.close();
		}
		builder.close();
		final VPackSlice vpack = builder.slice();
		assertThat(vpack.isObject(), is(true));
		assertThat(vpack.getLength(), is(size));
		for (int i = 0; i < size; i++) {
			final VPackSlice attr = vpack.get(String.valueOf(i));
			assertThat(attr.isObject(), is(true));
			for (int j = 0; j < size; j++) {
				final VPackSlice childAttr = attr.get(String.valueOf(j));
				assertThat(childAttr.isString(), is(true));
			}
		}
	}

	@Test
	public void object2ByteOffset() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.OBJECT));
		final int size = 10;
		for (int i = 0; i < size; i++) {
			builder.add(String.valueOf(i), new Value(ValueType.OBJECT));
			for (int j = 0; j < size; j++) {
				builder.add(String.valueOf(j), new Value("test"));
			}
			builder.close();
		}
		builder.close();
		final VPackSlice vpack = builder.slice();
		assertThat(vpack.isObject(), is(true));
		assertThat(vpack.getLength(), is(size));
		for (int i = 0; i < size; i++) {
			final VPackSlice attr = vpack.get(String.valueOf(i));
			assertThat(attr.isObject(), is(true));
			for (int j = 0; j < size; j++) {
				final VPackSlice childAttr = attr.get(String.valueOf(j));
				assertThat(childAttr.isString(), is(true));
			}
		}
	}

	@Test
	public void sortObjectAttr() throws VPackException {
		final int min = 0;
		final int max = 9;
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.OBJECT));
		for (int i = max; i >= min; i--) {
			builder.add(String.valueOf(i), new Value("test"));
		}
		builder.close();
		final VPackSlice vpack = builder.slice();
		assertThat(vpack.isObject(), is(true));
		assertThat(vpack.getLength(), is(max - min + 1));
		for (int i = min, j = 0; i <= max; i++, j++) {
			assertThat(vpack.keyAt(j).getAsString(), is(String.valueOf(i)));
		}
	}

	@Test
	public void sortObjectAttr2() throws VPackException {
		final String[] keys = { "a", "b", "c", "d", "e", "f", "g", "h" };
		final String[] keysUnsorted = { "b", "d", "c", "e", "g", "f", "h", "a" };
		assertThat(keysUnsorted.length, is(keys.length));
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.OBJECT));
		for (int i = 0; i < keysUnsorted.length; i++) {
			builder.add(String.valueOf(keysUnsorted[i]), new Value("test"));
		}
		builder.close();
		final VPackSlice vpack = builder.slice();
		assertThat(vpack.isObject(), is(true));
		assertThat(vpack.getLength(), is(keys.length));
		for (int i = 0; i < keys.length; i++) {
			assertThat(vpack.keyAt(i).getAsString(), is(String.valueOf(keys[i])));
		}
	}

	@Test
	public void attributeAdapterDefaults() throws VPackException {
		final VPackSlice vpackWithAttrAdapter;
		{
			final VPackBuilder builder = new VPackBuilder();
			builder.add(new Value(ValueType.OBJECT));
			builder.add("_key", new Value("a"));
			builder.close();
			vpackWithAttrAdapter = builder.slice();
			assertThat(vpackWithAttrAdapter.isObject(), is(true));
		}
		final VPackSlice vpackWithoutAttrAdapter;
		{
			final VPackBuilder builder = new VPackBuilder();
			builder.add(new Value(ValueType.OBJECT));
			builder.add("_kay", new Value("a"));
			builder.close();
			vpackWithoutAttrAdapter = builder.slice();
			assertThat(vpackWithoutAttrAdapter.isObject(), is(true));
		}
		assertThat(vpackWithAttrAdapter.getByteSize() < vpackWithoutAttrAdapter.getByteSize(), is(true));
	}

	@Test(expected = VPackBuilderNeedOpenCompoundException.class)
	public void closeClosed() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.OBJECT));
		builder.close();
		builder.close();
	}

	@Test
	public void addBinary() throws VPackException {
		final byte[] expected = new byte[] { 49, 50, 51, 52, 53, 54, 55, 56, 57 };
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(expected));
		final VPackSlice slice = builder.slice();

		assertThat(slice.isBinary(), is(true));
		assertThat(slice.getBinaryLength(), is(expected.length));
		assertThat(slice.getAsBinary(), is(expected));
		assertThat(slice.getByteSize(), is(1 + 4 + expected.length));
	}

	@Test
	public void addVPack() throws VPackException {
		final VPackBuilder builder = new VPackBuilder();
		builder.add(new Value(ValueType.OBJECT));
		builder.add("s", new Value(new VPackBuilder().add(new Value("test")).slice()));
		builder.close();
		final VPackSlice slice = builder.slice();
		assertThat(slice, is(notNullValue()));
		assertThat(slice.isObject(), is(true));
		assertThat(slice.get("s").isString(), is(true));
		assertThat(slice.get("s").getAsString(), is("test"));
	}
}
