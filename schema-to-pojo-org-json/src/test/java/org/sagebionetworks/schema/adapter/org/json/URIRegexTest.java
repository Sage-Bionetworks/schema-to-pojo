package org.sagebionetworks.schema.adapter.org.json;

import static org.junit.Assert.*;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Ignore;
import org.junit.Test;

/**
 * Following RFC 3986
 * 
 * @see http://tools.ietf.org/html/rfc3986
 * @author jmhill
 *
 */
public class URIRegexTest {

	public static final String GEN_DELIMS_REG_EX_BODY = ":/?#\\[\\]@";
	public static final String GEN_DELIMS_REG_EX = "["+GEN_DELIMS_REG_EX_BODY+"]";
	public static final String SUB_DELIMS_REG_EX_BODY = "!$&'()\\*\\+,;=";
	public static final String SUB_DELIMS_REG_EX = "["+SUB_DELIMS_REG_EX_BODY+"]";
	public static final String RESERVED = "["+GEN_DELIMS_REG_EX_BODY+SUB_DELIMS_REG_EX_BODY+"]";;
	
	public static final String SCHEME_REG_EX = "[a-z]([a-z0-9\\.\\-\\+])*";
	public static final String UNRESERVED_REG_EX = "[a-zA-Z0-9_~\\-\\.]";
	public static final String USER_INFO_REG_EX ="";
	public static final String AUTHROITY_REG_EX = "//";
	public static final String URI_REG_EX = SCHEME_REG_EX+":";
	
	public static final String[] generalDelimiters = new String[] {
		":",
		"/",
		"?",
		"#",
		"[",
		"]",
		"@",
	};
	
	public static final String[] subDelimiters = new String[] {
		"!",
		"$",
		"&",
		"'",
		"(",
		")",
		"*",
		"+",
		",",
		";",
		"=",
	};
	
	
	
	public static final String[] validSchemes = new String[] {
		"foo",
		"ftp",
		"http",
		"ldap",
		"mailto",
		"news",
		"tel",
		"telnet",
		"urn",
		"a93-a+b.1",
	};
	
	public static final String[] invalidSchemes = new String[] {
		"9foo",
		"+foo",
		"-foo",
		".foo",
		"fTp",
		"http:",
	};

	public static final String[] validURIs = new String[] {
			"foo://username:password@example.com:8042/over/there/index.dtb?type=animal&name=narwhal#nose",
			"ftp://ftp.is.co.za/rfc/rfc1808.txt",
			"http://www.ietf.org/rfc/rfc2396.txt",
			"ldap://[2001:db8::7]/c=GB?objectClass?one",
			"mailto:John.Doe@example.com",
			"news:comp.infosystems.www.servers.unix",
			"tel:+1-816-555-1212",
			"telnet://192.0.2.16:80/",
			"urn:oasis:names:specification:docbook:dtd:xml:4.1.2", };
	
	@Test
	public void testGeneralDelimiters(){
		Pattern pattern = Pattern.compile(GEN_DELIMS_REG_EX);
		for(String toTest: generalDelimiters){
			Matcher m = pattern.matcher(toTest);
			assertTrue(toTest+" is valid but did not match",m.matches());
		}
	}
	
	@Test
	public void testSubDelimiters(){
		Pattern pattern = Pattern.compile(SUB_DELIMS_REG_EX);
		for(String toTest: subDelimiters){
			Matcher m = pattern.matcher(toTest);
			assertTrue(toTest+" is valid but did not match",m.matches());
		}
	}
	
	@Test
	public void testReserved(){
		Pattern pattern = Pattern.compile(RESERVED);
		// Reserved is gen-delims and sub_delims
		for(String toTest: generalDelimiters){
			Matcher m = pattern.matcher(toTest);
			assertTrue(toTest+" is valid but did not match",m.matches());
		}
		for(String toTest: subDelimiters){
			Matcher m = pattern.matcher(toTest);
			assertTrue(toTest+" is valid but did not match",m.matches());
		}

	}
	
	@Test
	public void testValidSchemes(){
		Pattern pattern = Pattern.compile(SCHEME_REG_EX);
		for(String toTest: validSchemes){
			Matcher m = pattern.matcher(toTest);
			assertTrue(toTest+" is valid but did not match",m.matches());
		}
	}
	
	
	@Test
	public void testInvalidSchemes(){
		Pattern pattern = Pattern.compile(SCHEME_REG_EX);
		for(String toTest: invalidSchemes){
			Matcher m = pattern.matcher(toTest);
			assertFalse(toTest+" is invalid but matched",m.matches());
		}
		
	}
	
	@Ignore
	@Test
	public void testValidURI(){
		Pattern pattern = Pattern.compile(URI_REG_EX);
		for(String toTest: validURIs){
			Matcher m = pattern.matcher(toTest);
			assertTrue(toTest+" is valid but failed",m.matches());
		}
		
	}

}
