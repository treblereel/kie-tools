/*
 * Copyright © 2022 Treblereel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.workbench.common.stunner.sw.definition.custom;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;
import org.kie.workbench.common.stunner.sw.definition.Transition;

public class DataConditionTransitionStartJsonbTypeSerializer  implements JsonbSerializer<Object> {

    org.kie.workbench.common.stunner.sw.definition.Transition_JsonSerializerImpl transition_JsonSerializerImpl =
            new org.kie.workbench.common.stunner.sw.definition.Transition_JsonSerializerImpl();

    @Override
    public void serialize(Object obj, JsonGenerator generator, SerializationContext ctx) {
        if (obj instanceof Boolean) {
            generator.write("start", ((Boolean) obj));
        } else if (obj instanceof Transition) {
            transition_JsonSerializerImpl.serialize((Transition) obj, "start", generator, ctx);
        }
    }
}
