package org.msgpack.type;

import org.msgpack.RubySymbolException;
import java.util.regex.Pattern;

public class RubySymbol {
	private String _string;
	private static Pattern _pattern = compileSymbolRegex();
	
	public RubySymbol(String s) {
		if (_pattern.matcher(s).matches()) {
			_string = s;
		} else {
			throw new RubySymbolException(String.format("String contains invalid characters: %s", s));
		}
	}
	
	public boolean equals(Object obj) {
		if (!(obj instanceof RubySymbol)) {
			return false;
		} else {
			RubySymbol sym_obj = (RubySymbol) obj;
			return sym_obj.toString().equals(toString());
		}
	}
	
	public int hashCode() {
		return _string.hashCode();
	}
	
	public String toString() {
		return _string;
	}
	
	private static Pattern compileSymbolRegex() {
		return Pattern.compile("\\A[A-Za-z@\\$_][0-9A-Za-z_]*[0-9A-Za-z!_=\\?]?\\z");
	}
}
