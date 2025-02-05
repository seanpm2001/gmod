//  Groosh -- Provides a shell-like capability for handling external processes
//
//  Copyright © 2004 Yuri Schimke
//
//  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in
//  compliance with the License. You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software distributed under the License is
//  distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
//  implied. See the License for the specific language governing permissions and limitations under the
//  License.

package com.baulsupp.groovy.groosh;

import groovy.lang.Closure;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import com.baulsupp.process.IOUtil;
import com.baulsupp.process.Sink;
import com.baulsupp.process.Source;
import com.baulsupp.process.StandardStreams;

/**
 * 
 * @author Yuri Schimke
 *
 */
public class StreamClosureProcess extends GrooshProcess implements
		Callable<Boolean> {
	protected Closure closure;
	private InputStream is;
	private OutputStream os;
	private Future<Boolean> result;

	public StreamClosureProcess(Closure closure) {
		this.closure = closure;
	}

	public void start() {
		if (is == null)
			throw new RuntimeException("closure processes need a source");

		if (os == null)
			os = StandardStreams.stdout().getStream();

		result = IOUtil.getExecutor().submit(this);
	}

	public void waitForExit() throws IOException {
		try {
			result.get();
		} catch (Exception e) {
			// TODO handle the exceptions
			throw new RuntimeException(e);
		}
	}

	public Boolean call() throws Exception {
		try {
			process(is, os);
			return true;
		} finally {
			os.flush();
			os.close();
			is.close();
		}

	}

	protected void process(final InputStream is, final OutputStream os)
			throws IOException {
		List<Object> l = new ArrayList<Object>();
		l.add(is);
		l.add(os);
		closure.call(l);
		os.flush();
	}

	public class ClosureSink extends Sink {

		public void setStream(InputStream is) {
			StreamClosureProcess.this.is = is;
		}

		public boolean receivesStream() {
			return true;
		}
	}

	protected Sink getSink() {
		return new ClosureSink();
	}

	public class ClosureSource extends Source {
		public void connect(Sink sink) throws IOException {
			if (sink.providesStream()) {
				StreamClosureProcess.this.os = sink.getStream();
			} else if (sink.receivesStream()) {
				Pipe pipe = Pipe.open();
				StreamClosureProcess.this.os = Channels.newOutputStream(pipe
						.sink());
				sink.setStream(Channels.newInputStream(pipe.source()));
			} else {
				throw new UnsupportedOperationException("sink type unknown");
			}
		}
	}

	protected Source getSource() {
		return new ClosureSource();
	}
}
