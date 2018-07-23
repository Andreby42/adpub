package com.bus.chelaile.common;

import java.io.IOException;
import java.util.Map;

public interface Text {
	void write(Appendable buf, Map<String, String> ctx) throws IOException;
}
