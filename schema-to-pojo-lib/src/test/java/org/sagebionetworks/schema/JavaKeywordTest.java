package org.sagebionetworks.schema;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class JavaKeywordTest {

	@Test
	void testDetermineJavaName_Null() {
		String nullString = null;
		assertNull(JavaKeyword.determineJavaName(nullString));
	}

	@Test
	void testDetermineJavaName_NonKeyword() {
		String nonKeyword = "foobar";
		assertEquals(nonKeyword, JavaKeyword.determineJavaName(nonKeyword));
	}

	@Test
	void testDetermineJavaName_Keyword() {
		// just some examples of keywords. not comprehensive
		assertEquals("_class", JavaKeyword.determineJavaName("class"));
		assertEquals("_static", JavaKeyword.determineJavaName("static"));
		assertEquals("_const", JavaKeyword.determineJavaName("const"));
		assertEquals("_boolean", JavaKeyword.determineJavaName("boolean"));
		assertEquals("_long", JavaKeyword.determineJavaName("long"));
		assertEquals("_enum", JavaKeyword.determineJavaName("enum"));
		assertEquals("_null", JavaKeyword.determineJavaName("null"));
	}

	@Test
	void testDetermineJsonName_Null() {
		String nullString = null;
		assertNull(JavaKeyword.determineJsonName(nullString));
	}

	@Test
	void testDetermineJsonName_EmptyString() {
		String emptyString = "";
		assertEquals(emptyString, JavaKeyword.determineJsonName(emptyString));
	}


	@Test
	void testDetermineJsonName_NonKeyword() {
		String nonKeyword = "foobar";
		assertEquals(nonKeyword, JavaKeyword.determineJsonName(nonKeyword));
	}

	@Test
	void testDetermineJsonName_Keyword() {
		// just some examples of keywords. not comprehensive
		assertEquals("class", JavaKeyword.determineJsonName("_class"));
		assertEquals("static", JavaKeyword.determineJsonName("_static"));
		assertEquals("const", JavaKeyword.determineJsonName("_const"));
		assertEquals("boolean", JavaKeyword.determineJsonName("_boolean"));
		assertEquals("long", JavaKeyword.determineJsonName("_long"));
		assertEquals("enum", JavaKeyword.determineJsonName("_enum"));
		assertEquals("null", JavaKeyword.determineJsonName("_null"));
	}
}