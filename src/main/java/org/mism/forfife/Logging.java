/*
 * Copyright (c) 2014, Goethe University, Goethe Center for Scientific Computing (GCSC), gcsc.uni-frankfurt.de
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package org.mism.forfife;

import java.util.ArrayList;
import java.util.List;

/**
 * Yet another logging implementation. Use Logging.instance().getLoggers() to
 * chance logging behaviour.
 * 
 * @author tr1nergy
 *
 */
final class Logging {

	private static final class DefaultLogger implements Logger {

		@Override
		public void debug(String out) {
			System.out.println(Logging.class.getName() + ".debug: " + out);
		}

		@Override
		public void error(String out, Exception e) {
			System.out.println(Logging.class.getName() + ".debug: " + out);
			if (e != null)
				e.printStackTrace();
		}

		@Override
		public void info(String out) {
			System.out.println(Logging.class.getName() + ".debug: " + out);
		}

	}

	public interface Logger {
		void debug(String out);

		/**
		 * 
		 * @param out
		 *            error msg for devs.
		 * @param e
		 *            can be null
		 */
		void error(String out, Exception e);

		void info(String out);
	}

	private final List<Logger> loggers = new ArrayList<>();

	public static void debug(String out) {
		for (Logger logger : instance().loggers) {
			logger.debug(out);
		}
	}

	public static void error(String out, Exception e) {
		for (Logger logger : instance().loggers) {
			logger.error(out, e);
		}
	}

	public static void error(String out) {
		for (Logger logger : instance().loggers) {
			logger.error(out, null);
		}
	}

	public static void info(String out) {
		for (Logger logger : instance().loggers) {
			logger.info(out);
		}
	}

	private static final Logging SINGLETONS_ARE_BAD = new Logging();

	/**
	 * TODO Create from environment/property file etc.
	 */
	private Logging() {
		loggers.add(new DefaultLogger());
	}

	public static Logging instance() {
		return SINGLETONS_ARE_BAD;
	}

	/**
	 * @return You get a hard reference to the logger list used by the logging
	 *         singleton.
	 */
	public List<Logger> getLoggers() {
		return loggers;
	}

}
