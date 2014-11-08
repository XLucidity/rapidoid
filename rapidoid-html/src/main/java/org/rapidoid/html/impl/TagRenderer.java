package org.rapidoid.html.impl;

/*
 * #%L
 * rapidoid-html
 * %%
 * Copyright (C) 2014 Nikolche Mihajlovski
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */

import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;

import org.rapidoid.html.CustomTag;
import org.rapidoid.html.HTML;
import org.rapidoid.html.Tag;
import org.rapidoid.html.TagWidget;
import org.rapidoid.html.Var;
import org.rapidoid.util.U;

public class TagRenderer {

	protected static final TagRenderer INSTANCE = new TagRenderer();

	public static TagRenderer get() {
		return INSTANCE;
	}

	public String str(Object content, Object extra) {
		return str(content, 0, false, extra);
	}

	public String str(Object content, int level, boolean inline, Object extra) {

		if (content instanceof Tag) {
			Tag<?> tag = (Tag<?>) content;
			return tag.str(level);
		} else if (content instanceof TagWidget) {
			TagWidget widget = (TagWidget) content;
			return str(widget.content(), level, inline, extra);
		} else if (content instanceof Object[]) {
			return join((Object[]) content, level, inline, extra);
		} else if (content instanceof Collection<?>) {
			return join((Collection<?>) content, level, inline, extra);
		}

		return indent(level, inline) + HTML.escape(String.valueOf(content));
	}

	protected String indent(int level, boolean inline) {
		return !inline ? U.mul("  ", level) : "";
	}

	protected String join(Collection<?> items, int level, boolean inline, Object extra) {
		StringBuilder sb = new StringBuilder();

		for (Object item : items) {
			if (!inline) {
				sb.append("\n");
			}
			sb.append(str(item, level + 1, inline, extra));
		}

		return sb.toString();
	}

	protected String join(Object[] items, int level, boolean inline, Object extra) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < items.length; i++) {
			if (!inline) {
				sb.append("\n");
			}
			sb.append(str(items[i], level + 1, inline, extra));
		}

		return sb.toString();
	}

	public String str(TagData<?> tag, int level, boolean inline, Object extra) {

		String name = HTML.escape(tag.name);
		List<Object> contents = tag.contents;

		StringBuilder sb = new StringBuilder();

		if (tag.ctx != null) {
			sb.append(" _h=\"");
			sb.append(attrToStr(tag, "_h", tag._hnd));
			sb.append("\"");
		}

		for (Entry<String, Object> e : tag.attrs.entrySet()) {
			String attr = e.getKey();

			sb.append(" ");
			sb.append(HTML.escape(attr));
			sb.append("=\"");
			sb.append(HTML.escape(attrToStr(tag, attr, e.getValue())));
			sb.append("\"");
		}

		for (String event : tag.eventHandlers.keySet()) {
			sb.append(" on");
			sb.append(event);
			sb.append("=\"");
			sb.append("_emit(this, '");
			sb.append(event);
			sb.append("')");
			sb.append("\"");
		}

		String attrib = sb.toString();

		String indent = indent(level, inline);

		if (contents == null || contents.isEmpty()) {
			return U.format("%s<%s%s></%s>", indent, name, attrib, name);
		}

		if (inline || shouldRenderInline(name, contents)) {
			String content = str(contents, level + 1, true, extra);
			if (isSingleTag(name)) {
				return U.format("%s<%s%s>", indent, name, attrib);
			} else {
				return U.format("%s<%s%s>%s</%s>", indent, name, attrib, content, name);
			}
		}

		sb = new StringBuilder();

		if (contents != null) {
			if (contents.size() < 2) {
				sb.append(str(contents, level, inline, extra));
			} else {
				sb.append(str(contents, level, inline, extra));
			}
		}

		String inside = sb.toString();

		return U.format("%s<%s%s>%s\n%s</%s>", indent, name, attrib, inside, indent, name);
	}

	protected boolean isSingleTag(String name) {
		return name.equals("input");
	}

	protected String attrToStr(TagData<?> tag, String attr, Object value) {
		if (value == null) {
			throw U.rte("The HTML attribute '%s' of tag '%s' cannot have null value!", attr, tag.name);
		}

		if (value instanceof Object[]) {
			Object[] arr = (Object[]) value;
			return U.join(" ", arr);
		}

		if (value instanceof Collection) {
			Collection<?> coll = (Collection<?>) value;
			return U.join(" ", coll);
		}

		return value.toString();
	}

	protected boolean shouldRenderInline(String name, Object content) {
		if (isSimpleContent(content)) {
			return true;
		}

		if (content instanceof Object[]) {
			return hasSimpleContent((Object[]) content);
		}

		if (content instanceof Collection) {
			return hasSimpleContent((Collection<?>) content);
		}

		return false;
	}

	protected boolean isSimpleContent(Object content) {
		if (content instanceof Var) {
			Var<?> var = (Var<?>) content;
			return isSimpleContent(var.get());
		}

		return !U.instanceOf(content, Tag.class, CustomTag.class, TagWidget.class, Object[].class, Collection.class);
	}

	protected boolean hasSimpleContent(Collection<?> content) {
		for (Object cnt : content) {
			if (!isSimpleContent(cnt)) {
				return false;
			}

		}
		return true;
	}

	protected boolean hasSimpleContent(Object[] content) {
		for (Object cnt : content) {
			if (isSimpleContent(cnt)) {
				return true;
			}
		}
		return false;
	}

}
