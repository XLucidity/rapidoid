package org.rapidoid.beany;

/*
 * #%L
 * rapidoid-beany
 * %%
 * Copyright (C) 2014 - 2015 Nikolche Mihajlovski
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

import java.lang.annotation.Annotation;

public class AnnotatedPropertyFilter extends PropertyFilter {

	private final Class<? extends Annotation> annotated;

	public AnnotatedPropertyFilter(Class<? extends Annotation> annotated) {
		this.annotated = annotated;
	}

	@Override
	public boolean eval(Prop prop) throws Exception {
		return prop.getAnnotation(annotated) != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotated == null) ? 0 : annotated.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AnnotatedPropertyFilter other = (AnnotatedPropertyFilter) obj;
		if (annotated == null) {
			if (other.annotated != null)
				return false;
		} else if (!annotated.equals(other.annotated))
			return false;
		return true;
	}

}