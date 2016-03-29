package java.text;

import java.io.Serializable;
import java.util.Map;
/*
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
import java.util.Set;

public interface AttributedCharacterIterator extends CharacterIterator {

    public static class Attribute implements Serializable {

        public static final Attribute LANGUAGE = new Attribute("language");

        public static final Attribute READING = new Attribute("reading");

        public static final Attribute INPUT_METHOD_SEGMENT
                = new Attribute("input_method_segment");

        private final String name;

        protected Attribute(String name) {
            this.name = name;
        }

        protected String getName() {
            return name;
        }

        @Override
        public final boolean equals(Object obj) {
            return obj == this;
        }

        @Override
        public final int hashCode() {
            return super.hashCode();
        }
    }

    Set<Attribute> getAllAttributeKeys();

    Map<Attribute, Object> getAttributes();

    Object getAttribute(AttributedCharacterIterator.Attribute attrib);

    int getRunStart();

    int getRunStart(Set<? extends Attribute> attribs);

    int getRunStart(AttributedCharacterIterator.Attribute attrib);

    int getRunLimit();

    int getRunLimit(Set<? extends Attribute> attribs);

    int getRunLimit(AttributedCharacterIterator.Attribute attrib);

}
