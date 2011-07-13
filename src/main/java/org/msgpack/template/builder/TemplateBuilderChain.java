//
// MessagePack for Java
//
// Copyright (C) 2009-2011 FURUHASHI Sadayuki
//
//    Licensed under the Apache License, Version 2.0 (the "License");
//    you may not use this file except in compliance with the License.
//    You may obtain a copy of the License at
//
//        http://www.apache.org/licenses/LICENSE-2.0
//
//    Unless required by applicable law or agreed to in writing, software
//    distributed under the License is distributed on an "AS IS" BASIS,
//    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//    See the License for the specific language governing permissions and
//    limitations under the License.
//
package org.msgpack.template.builder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import org.msgpack.template.TemplateRegistry;


public class TemplateBuilderChain {

    protected List<TemplateBuilder> templateBuilders = new ArrayList<TemplateBuilder>();

    protected TemplateBuilder forceTemplateBuilder = null;

    public TemplateBuilderChain() {
    }

    public void init(TemplateRegistry registry) {
	// TODO #MN
	// add javassist-based template builder
	// add beans-based template builder
	templateBuilders.add(new ReflectionTemplateBuilder(registry));
	templateBuilders.add(new ReflectionOrdinalEnumTemplateBuilder(registry));
	forceTemplateBuilder = new ReflectionTemplateBuilder(registry);
    }

    public TemplateBuilder select(Type targetType) {
	for (TemplateBuilder tb : templateBuilders) {
	    if (tb.matchType(targetType)) {
		return tb;
	    }
	}
	return forceTemplateBuilder;
    }
}
