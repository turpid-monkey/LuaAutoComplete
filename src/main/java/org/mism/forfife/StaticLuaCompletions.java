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
import java.util.Arrays;
import java.util.List;

import org.fife.ui.autocomplete.BasicCompletion;
import org.fife.ui.autocomplete.Completion;
import org.fife.ui.autocomplete.CompletionProvider;
import org.fife.ui.autocomplete.FunctionCompletion;
import org.fife.ui.autocomplete.ParameterizedCompletion.Parameter;
import org.fife.ui.autocomplete.ShorthandCompletion;

public class StaticLuaCompletions {

	String[][] completionsTable = {
			{
					"BC",
					"for",
					"starts a for loop",
					"<b>for</b> variable <b>=</b> start<b>,</b> stop [<b>,</b> step] <b>do</b><br>myLoop<br><b>end</b><br><br>see also <b>break</b>" },
			{
					"BC",
					"while",
					"starts a while loop",
					"<b>while</b> condition <b>do</b><br>myLoop<br><b>end</b><br><br>see also <b>break</b>" },
			{
					"BC",
					"repeat",
					"starts a repeat-until loop",
					"<b>repeat</b><br>myLoop<br><b>until</b> condition<br><br>see also <b>break</b>" },
			{ "BC", "break", "break",
					"aborts a loop like <b>for</b>,<b>repeat until</b> or <b>while</b>" },
			{
					"BC",
					"if",
					"starts an if statement",
					"<b>if</b> condition1 <b>then</b><br>conditionalCode1<br><b>elseif</b> condition2 <b>then</b><br>conditionalCode2<br><b>else</b><br>elseCode<br><b>end</b>" },
			{
					"BC",
					"else",
					"starts an else clause in if statement",
					"<b>if</b> condition1 <b>then</b><br>conditionalCode1<br><b>elseif</b> condition2 <b>then</b><br>conditionalCode2<br><b>else</b><br>elseCode<br><b>end</b>" },
			{
					"BC",
					"elseif",
					"starts an elseif clause",
					"<b>if</b> condition1 <b>then</b><br>conditionalCode1<br><b>elseif</b> condition2 <b>then</b><br>conditionalCode2<br><b>else</b><br>elseCode<br><b>end</b>" },
			{
					"BC",
					"then",
					"starts then clause",
					"<b>if</b> condition1 <b>then</b><br>conditionalCode1<br><b>elseif</b> condition2 <b>then</b><br>conditionalCode2<b><b>else</b><br>elseCode<br><b>end</b>" },
			{
					"BC",
					"function",
					"starts a function definition",
					"<b>function</b> functionName<b>(</b>parameter1<b>,</b> parameter2<b>)</b><br>code<br>[<b>return</b> myVal]<br><b>end</b>" },
			{
					"BC",
					"return",
					"returns a value",
					"<b>function</b> functionName<b>(</b>parameter1<b>,</b> parameter2<b>)</b><br>code<br>[<b>return</b> myVal]<br><b>end</b>" },
			{
					"BC",
					"assert",
					"fail on condition",
					"assert (v [, message])<br>Issues an error when the value of its argument v is false (i.e., nil or false); otherwise, returns all its arguments. message is an error message; when absent, it defaults to \"assertion failed!\"" },
			{ "BC", "dofile", "declare file dependency",
					"don't use dofile. use ug_load_script." },
			{ "BC", "error", "crash the program",
					"error (message [, level])<br>Terminates execution and prints error" },
			{
					"BC",
					"pairs",
					"assign array to variables",
					"pairs (t)<br>Returns three values: the next function, the table t, and nil, so that the construction<br>for k,v in pairs(t) do body end<br>will iterate over all key-value pairs of table t." },
			{
					"BC",
					"print",
					"print some stuff",
					"print text to console/log. example<br>print(myVar..\" myText! myInteger = \"..myInteger)" },
			{
					"BC",
					"tostring",
					"turn stuff to string",
					"Receives an argument of any type and converts it to a string in a reasonable format. Specify __tostring in your class to use in ug4 registry" },
			{ "BC", "require", "declare file dependency",
					"don't use require. use ug_load_script." },
			{
					"BC",
					"ug_load_script(",
					"load file via UG shell",
					"ug_load_script(filename)<br>loads a lua script and executes it.<br>Loads lua scripts from<br>- relative to current script<br>- ug4/scripts<br>- ug4/apps<br>- ug4/<br>- as absolute filename<br>This function is efficient in parallel. However, you have to assure that you call ug_load_script on all cores." },
			{ "PC", "string.byte", "string.byte (s [, i [, j]])", "byte", "s",
					"i", "j" },
			{ "PC", "string.find",
					"string.find (s, pattern [, init [, plain]])", "string",
					"s", "pattern", "init", "plain" },
			{
					"PC",
					"string.format",
					"string.format (formatstring, ...)<br>This is like printf in C, only that modifiers *, l, L, n, p, and h are not supported.",
					"string", "formatstring", "params..." },
			{ "PC", "string.len", "string.len(s)", "int", "s" },
			{ "PC", "string.lower",
					"string.lower(s)<br>returns lowercase copy of string s",
					"string", "s" },
			{ "PC", "string.upper",
					"string.upper(s)<br>returns uppercase copy of string s",
					"string", "s" },
			{ "PC", "string.match", "string.match (s, pattern [, init])",
					"bool", "s", "pattern", "init" },
			{
					"PC",
					"string.rep",
					"string.rep (s, n)<br>Returns a string that is the concatenation of n copies of the string s.",
					"string", "s", "n" },
			{ "PC", "string.reverse", "string.reverse (s)", "string", "s" },
			{ "PC", "string.sub", "string.sub (s, i [, j])", "string", "s",
					"i", "j" },
			{ "PC", "table.concat", "table.concat (table [, sep [, i [, j]]])",
					"table", "table", "sep", "i", "j" },
			{ "PC", "table.insert", "table.insert (table, [pos,] value)",
					"table", "table", "pos", "value" },
			{
					"PC",
					"table.maxn",
					"table.maxn (table)<br>Returns the largest positive numerical index of the given table, or zero if the table has no positive numerical indices.",
					"int", "table" },
			{ "PC", "table.remove", "table.remove (table [, pos])", "table",
					"table", "pos" },
			{ "PC", "table.sort", "table.sort (table [, comp])", "table",
					"table", "comp" },
			{ "PC", "math.abs", "math.abs (x)", "number", "x" },
			{ "PC", "math.acos", "math.acos (x)", "number", "x" },
			{ "PC", "math.asin", "math.asin (x)", "number", "x" },
			{ "PC", "math.atan", "math.atan (x)", "number", "x" },
			{ "PC", "math.atan2", "math.atan2 (y, x)", "number", "x", "y" },
			{ "PC", "math.ceil", "math.ceil(x)", "number", "x" },
			{ "PC", "math.cos", "math.cos(x)", "number", "x" },
			{ "PC", "math.cosh", "math.cosh(x)", "number", "x" },
			{
					"PC",
					"math.deg",
					"math.deg(x)<br>Returns the angle x (given in radians) in degrees",
					"number", "x" },
			{ "PC", "math.exp", "math.exp(x)", "number", "x" },
			{ "PC", "math.floor", "math.floor(x)", "number", "x" },
			{
					"PC",
					"math.fmod",
					"PC",
					"math.fmod(x, y)<br>Returns the remainder of the division of x by y that rounds the quotient towards zero",
					"number", "x", "y" },
			{
					"PC",
					"math.frexp",
					"math.frexp(x))<br>Returns m and e such that x = m2^e, e is an integer and the absolute value of m is in the range [0.5, 1) (or zero when x is zero)",
					"number", "x" },
			{
					"PC",
					"math.huge",
					"a value larger than or equal to any other numerical value",
					"number" },
			{ "PC", "math.ldexp", "math.ldexp (m, e)<br>returns m2^e",
					"number", "m", "e" },
			{ "PC", "math.log", "math.log(x)", "number", "x" },
			{ "PC", "math.log10", "math.log10(x)", "number", "x" },
			{ "PC", "math.min", "math.min(x)", "number", "x" },
			{ "PC", "math.max", "math.max(x)", "number", "x" },
			{
					"PC",
					"math.modf",
					"math.modf(x)<br>Returns two numbers, the integral part of x and the fractional part of x.",
					"table", "x" },
			{ "BC", "math.pi", "3.141...", "good old PI" },
			{ "PC", "math.pow", "math.pow (x, y)", "number", "x", "y" },
			{ "PC", "math.rad", "math.rad (x)", "number", "x" },
			{ "PC", "math.random", "math.random ([m [, n]])", "number", "m",
					"n" },
			{ "PC", "math.randomseed", "math.randomseed(x)", "number", "x" },
			{ "PC", "math.sin", "math.sin(x)", "number", "x" },
			{ "PC", "math.sinh", "math.sinh(x)", "number", "x" },
			{ "PC", "math.sqrt", "math.sqrt(x)", "number", "x" },
			{ "PC", "math.tan", "math.tan(x)", "number", "x" },
			{ "PC", "math.tanh", "math.tanh(x)", "number", "x" },
			{
					"PC",
					"io.open",
					"io.open(filename [, model])<br>mode is like the fopen mode.",
					"handle", "filename", "model" },
			{
					"PC",
					"os.clock",
					"os.clock()<br>Returns an approximation of the amount in seconds of CPU time used by the program",
					"int" },
			{ "PC", "os.date", "os.date ([format [, time]])", "date", "format",
					"time" },
			{ "PC", "os.difftime", "os.difftime(t2, t1)", "int", "t2", "t1" },
			{ "PC", "os.execute", "os.execute(command)", "int", "command" },
			{ "PC", "os.getenv", "os.getenv (varname)", "string", "varname" },
			{ "PC", "os.remove", "os.remove (filename)", "bool", "filename" },
			{ "PC", "os.rename", "os.rename (oldname, newname)", "bool",
					"oldname", "newname" },
			{ "PC", "os.time", "os.time ([table])", "date", "table" },
			{ "PC", "os.tmpname", "os.tmpname()", "file" },
			{ "BC", "breakpoint()", "add breakpoint",
					"add a ug4 LUA breakpoint at this line" } };
	
	public StaticLuaCompletions(CompletionProvider provider)
	{
		this.provider = provider;
	}
	
	CompletionProvider provider;
	List<Completion> completionBuffer = new ArrayList<>();
	boolean init = false;
	
	public List<Completion> getCompletions()
	{
		if(!init)
		{
			addCompletions(completionBuffer, provider, completionsTable);
			init = true;
		}
		return completionBuffer;
	}

	public void addCompletion(List<Completion> completions,
			CompletionProvider provider, String[] row) {
		switch (row[0]) {
		case "BC":
			if (row.length != 4)
				throw new IllegalArgumentException(
						"Needs BC <repltext> <short-descr> <summary>"
								+ Arrays.toString(row));
			completions.add(new BasicCompletion(provider, row[1], row[2],
					row[3]));
			break;
		case "SH":
			if (row.length != 4)
				throw new IllegalArgumentException(
						"Needs SH <short-cut> <repltext> <short-descr>: "
								+ Arrays.toString(row));
			completions.add(new ShorthandCompletion(provider, row[1], row[2],
					row[3]));
			break;
		case "PC":
			if (row.length < 4)
				throw new IllegalArgumentException(
						"Needs PC <function> <summary> <ret> (<param>)?"
								+ Arrays.toString(row));
			FunctionCompletion fc = new FunctionCompletion(provider, row[1],
					row[3]);
			fc.setShortDescription(row[2]);
			List<Parameter> params = new ArrayList<>();
			for (int i = 4; i < row.length; i++) {
				params.add(new Parameter(null, row[i]));
			}
			fc.setParams(params);
			completions.add(fc);
			break;
		}
	}

	public void addCompletions(List<Completion> completions,
			CompletionProvider provider, String[][] completionsTable) {
		for (String[] row : completionsTable) {
			addCompletion(completions, provider, row);

		}
	}
}
