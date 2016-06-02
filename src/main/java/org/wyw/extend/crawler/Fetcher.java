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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PushbackInputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.wyw.extend.crawler.pojo.Page;


public class Fetcher{

	public static final Logger logger = Logger.getLogger(Fetcher.class);
	
	private static final int BUFFER_SIZE = 8 * 1024;
	/** The network timeout in millisecond */
	private static final int timeout = 10000;
	/** The length limit for downloaded content, in bytes. */
	private static final int maxContent = 64 * 1024 * 10;
	/** The Nutch 'User-Agent' request header */
	private static final String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/39.0.2171.95 Safari/537.36";
	/** The "Accept-Language" request header value. */
	private static final String acceptLanguage = "en-us,en-gb,en;q=0.7,*;q=0.3";
	/** The "Accept" request header value. */
	private static final String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8";
	//重连次数
	private static final int retry = 3;
	
	private int code = -1;
	private byte[] content;
	private final Map<String, String> headers = new HashMap<String, String>();

	public void fetch(Page page) throws IOException {
		URL url = page.getUrl();
		try{
			request(url);
			if (code == 200) { // got a good response
				page.setResponseCode(code);
				page.setHeaders(headers);
				page.setContent(ByteBuffer.wrap(content));
				logger.info("fetch success: "+url);
			} else
				throw new IOException("!!!fetch failure: "+url+", responseCode: "+code);
		}catch(IOException e){
			throw e;
		}catch(Exception e){
			throw new IOException("fet failure: "+url+", responseCode: " + code);
		}
	}
	
	private void request(URL url) throws Exception{
		int count = retry;
		while(true){
			try{
				doRequest(url);
				break;
			}catch (IOException e) {
				if(--count>0){
					code = -1;
					content = null;
					headers.clear();
				}else
					throw e;
			}
		}
	}
	
	private void doRequest(URL url) throws Exception{
		String path = "".equals(url.getFile()) ? "/" : url.getFile();
		String host = url.getHost();
		int port;
		String portString;
		if (url.getPort() == -1) {
			port = 80;
			portString = "";
		} else {
			port = url.getPort();
			portString = ":" + port;
		}
		
		Socket socket = null;
		try {
			socket = new Socket(); // create the socket
			socket.setSoTimeout(timeout);
			
			// connect
			InetSocketAddress sockAddr = new InetSocketAddress(host, port);
			socket.connect(sockAddr, timeout);
			
			// make request
			OutputStream req = socket.getOutputStream();
			StringBuffer reqStr = new StringBuffer("GET ");
			reqStr.append(path);
			reqStr.append(" HTTP/1.0\r\n");
			reqStr.append("Host: ");
			reqStr.append(host);
			reqStr.append(portString);
			reqStr.append("\r\n");
			reqStr.append("Accept-Encoding: x-gzip, gzip\r\n");
			reqStr.append("Accept: ");
			reqStr.append(accept);
			reqStr.append("\r\n");
			reqStr.append("User-Agent: ");
			reqStr.append(userAgent);
			reqStr.append("\r\n");
			reqStr.append("\r\n");
			byte[] reqBytes = reqStr.toString().getBytes();
			req.write(reqBytes);
			req.flush();

			// process response
			PushbackInputStream in = new PushbackInputStream(
					new BufferedInputStream(socket.getInputStream(),
							BUFFER_SIZE), BUFFER_SIZE);

			StringBuffer line = new StringBuffer();
			boolean haveSeenNonContinueStatus = false;
			while (!haveSeenNonContinueStatus) {
				// parse status code line
				this.code = parseStatusLine(in, line);
				// parse headers
				parseHeaders(in, line);
				haveSeenNonContinueStatus = (code != 100); // 100 is "Continue"
			}

			readPlainContent(in);

			String contentEncoding = headers.get("Content-Encoding");
			if ("gzip".equals(contentEncoding) || "x-gzip".equals(contentEncoding)) {
				content = processGzipEncoded(content, url);
			}
		}finally {
			if (socket != null)
				socket.close();
		}
	}
	
	private byte[] processGzipEncoded(byte[] compressed, URL url) throws IOException {
		byte[] content = GZIPUtils.unzipBestEffort(compressed, maxContent);
		if (content == null)
			throw new IOException("unzipBestEffort returned null");
		return content;
	}

	private byte[] processDeflateEncoded(byte[] compressed, URL url) throws IOException {
		byte[] content = DeflateUtils.inflateBestEffort(compressed, maxContent);
		if (content == null)
			throw new IOException("inflateBestEffort returned null");
		return content;
	}

	private void readPlainContent(InputStream in) throws Exception{
		int contentLength = Integer.MAX_VALUE; // get content length
		String contentLengthString = headers.get("Content-Length");
		if (contentLengthString != null) {
			contentLengthString = contentLengthString.trim();
			try {
				if (!contentLengthString.isEmpty())
					contentLength = Integer.parseInt(contentLengthString);
			} catch (NumberFormatException e) {
				throw new Exception("bad content length: "+ contentLengthString);
			}
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream(BUFFER_SIZE);
		byte[] bytes = new byte[BUFFER_SIZE];
		int length = 0; // read content
		for (int i = in.read(bytes); i != -1 && length + i <= contentLength; i = in.read(bytes)) {
			out.write(bytes, 0, i);
			length += i;
		}
		content = out.toByteArray();
	}

	private int parseStatusLine(PushbackInputStream in, StringBuffer line) throws Exception {
		readLine(in, line, false);
		int codeStart = line.indexOf(" ");
		int codeEnd = line.indexOf(" ", codeStart + 1);

		// handle lines with no plaintext result code, ie:
		// "HTTP/1.1 200" vs "HTTP/1.1 200 OK"
		if (codeEnd == -1)
			codeEnd = line.length();

		int code;
		try {
			code = Integer.parseInt(line.substring(codeStart + 1, codeEnd));
		} catch (NumberFormatException e) {
			throw new Exception("bad status line '" + line + "': "+ e.getMessage(), e);
		}

		return code;
	}

	private void processHeaderLine(StringBuffer line) throws Exception {
		int colonIndex = line.indexOf(":"); // key is up to colon
		if (colonIndex == -1) {
			int i;
			for (i = 0; i < line.length(); i++)
				if (!Character.isWhitespace(line.charAt(i)))
					break;
			if (i == line.length())
				return;
			throw new Exception("No colon in header:" + line);
		}
		String key = line.substring(0, colonIndex);

		int valueStart = colonIndex + 1; // skip whitespace
		while (valueStart < line.length()) {
			int c = line.charAt(valueStart);
			if (c != ' ' && c != '\t')
				break;
			valueStart++;
		}
		String value = line.substring(valueStart);
		headers.put(key, value);
	}

	// Adds headers to our headers Metadata
	private void parseHeaders(PushbackInputStream in, StringBuffer line) throws Exception {

		while (readLine(in, line, true) != 0) {

			// handle HTTP responses with missing blank line after headers
			int pos;
			if (((pos = line.indexOf("<!DOCTYPE")) != -1)
					|| ((pos = line.indexOf("<HTML")) != -1)
					|| ((pos = line.indexOf("<html")) != -1)) {

				in.unread(line.substring(pos).getBytes("UTF-8"));
				line.setLength(pos);

				try {
					// TODO: (CM) We don't know the header names here
					// since we're just handling them generically. It would
					// be nice to provide some sort of mapping function here
					// for the returned header names to the standard metadata
					// names in the ParseData class
					processHeaderLine(line);
				} catch (Exception e) {
					// fixme:
					logger.error("Failed with the following exception: ", e);
				}
				return;
			}

			processHeaderLine(line);
		}
	}

	private static int readLine(PushbackInputStream in, StringBuffer line, boolean allowContinuedLine) throws IOException {
		line.setLength(0);
		for (int c = in.read(); c != -1; c = in.read()) {
			switch (c) {
			case '\r':
				if (peek(in) == '\n') {
					in.read();
				}
			case '\n':
				if (line.length() > 0) {
					// at EOL -- check for continued line if the current
					// (possibly continued) line wasn't blank
					if (allowContinuedLine)
						switch (peek(in)) {
						case ' ':
						case '\t': // line is continued
							in.read();
							continue;
						}
				}
				return line.length(); // else complete
			default:
				line.append((char) c);
			}
		}
		throw new EOFException();
	}

	private static int peek(PushbackInputStream in) throws IOException {
		int value = in.read();
		in.unread(value);
		return value;
	}
	
	public static void main(String[] args) throws Exception {
		URL url = new URL("http://www.suimeng.com/weekvote-1.html");
		Page page = new Page(url);
		Fetcher fetchJob = new Fetcher();
		fetchJob.fetch(page);
		System.out.println(page.getResponseCode());
		System.out.println(new String(page.getContent().array(),"gb18030"));
	}

}
