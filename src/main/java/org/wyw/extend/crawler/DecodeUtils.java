/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wyw.extend.crawler;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.wyw.extend.crawler.pojo.Page;


public class DecodeUtils {

	public static final Logger logger = Logger.getLogger(DecodeUtils.class);

	// I used 1000 bytes at first, but found that some documents have
	// meta tag well past the first 1000 bytes.
	// (e.g. http://cn.promo.yahoo.com/customcare/music.html)
	private static final int CHUNK_SIZE = 2000;
	
	private static String defaultCharEncoding = "utf-8";

	// NUTCH-1006 Meta equiv with single quotes not accepted
//	private static Pattern metaPattern = Pattern.compile(
//			"<meta\\s+([^>]*http-equiv=(\"|')?content-type(\"|')?[^>]*)>",
//			Pattern.CASE_INSENSITIVE);
//	private static Pattern charsetPattern = Pattern.compile(
//			"charset=\\s*([a-z][_\\-0-9a-z]*)", Pattern.CASE_INSENSITIVE);
	private static Pattern charsetPattern = Pattern.compile(
			"<meta\\s+[^>]*charset=\\s*[\"']?\\s*([a-z][_\\-0-9a-z]*)\\s*[\"']?\\s*[^>]*>", Pattern.CASE_INSENSITIVE);

	/**
	 * Given a <code>ByteBuffer</code> representing an html file of an
	 * <em>unknown</em> encoding, read out 'charset' parameter in the meta tag
	 * from the first <code>CHUNK_SIZE</code> bytes. If there's no meta tag for
	 * Content-Type or no charset is specified, <code>null</code> is returned. <br />
	 * FIXME: non-byte oriented character encodings (UTF-16, UTF-32) can't be
	 * handled with this. We need to do something similar to what's done by
	 * mozilla
	 * (http://lxr.mozilla.org/seamonkey/source/parser/htmlparser/src/nsParser
	 * .cpp#1993). See also http://www.w3.org/TR/REC-xml/#sec-guessing <br />
	 * 
	 * @param content
	 *            <code>ByteBuffer</code> representation of an html file
	 */

	private static String sniffCharacterEncoding(ByteBuffer content) {
		int length = Math.min(content.remaining(), CHUNK_SIZE);

		// We don't care about non-ASCII parts so that it's sufficient
		// to just inflate each byte to a 16-bit value by padding.
		// For instance, the sequence {0x41, 0x82, 0xb7} will be turned into
		// {U+0041, U+0082, U+00B7}.
		String str = "";
		try {
			str = new String(content.array(), content.arrayOffset()
					+ content.position(), length, Charset.forName("ASCII").toString());
		} catch (UnsupportedEncodingException e) {
			// code should never come here, but just in case...
			return null;
		}

		Matcher charsetMatcher = charsetPattern.matcher(str);
		String encoding = null;
		if (charsetMatcher.find())
			encoding = new String(charsetMatcher.group(1));
		
		return encoding;
	}

	public static void decode(Page page) {
		EncodingDetector detector = new EncodingDetector();
		detector.autoDetectClues(page);
		detector.addClue(sniffCharacterEncoding(page.getContent()), "sniffed");
		String encoding = detector.guessEncoding(defaultCharEncoding);
		page.setEncoding(encoding);
//		logger.info("decode charset="+encoding);
	}

}
