package com.kaydeesea.spigot.util;

import java.util.ArrayList;
import java.util.List;

public class YamlUtils {
	public static int findKey(List<String> lines, String key) {
		String[] parts = key.split("\\.");
		int _line = 0;
		List<String> _cpath = new ArrayList<>();
		for (String part : parts) {
			_cpath.add(part);
			for (int i = _line; i < lines.size(); i++) {
				if (lines.get(i).trim().startsWith(part)) {
					_line = i;
					if (String.join(".", _cpath).equals(key)) {
						return _line;
					}
				}
			}
		}
		return -1;
	}
}
