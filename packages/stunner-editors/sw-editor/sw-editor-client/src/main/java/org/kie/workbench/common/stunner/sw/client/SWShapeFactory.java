/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.client;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.stunner.core.client.shape.Shape;
import org.kie.workbench.common.stunner.core.client.shape.factory.ShapeFactory;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeViewDef;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeFactory;
import org.kie.workbench.common.stunner.sw.client.shapes.AnyStateShapeDef;
import org.kie.workbench.common.stunner.sw.client.shapes.TransitionShape;
import org.kie.workbench.common.stunner.sw.client.shapes.TransitionShapeDef;
import org.kie.workbench.common.stunner.sw.client.shapes.TransitionView;
import org.kie.workbench.common.stunner.sw.definition.End;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.InjectState;
import org.kie.workbench.common.stunner.sw.definition.Start;
import org.kie.workbench.common.stunner.sw.definition.StartTransition;
import org.kie.workbench.common.stunner.sw.definition.SwitchState;
import org.kie.workbench.common.stunner.sw.definition.Transition;

import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

@ApplicationScoped
public class SWShapeFactory
        implements ShapeFactory<Object, Shape> {

    private static final Map<Class<?>, ShapeViewDef> typeViewDefinitions = new HashMap<Class<?>, ShapeViewDef>() {{
        put(Start.class, new AnyStateShapeDef());
        put(End.class, new AnyStateShapeDef());
        put(InjectState.class, new AnyStateShapeDef());
        put(SwitchState.class, new AnyStateShapeDef());
        put(Transition.class, new TransitionShapeDef());
        put(StartTransition.class, new TransitionShapeDef());
        put(ErrorTransition.class, new TransitionShapeDef());
    }};

    private final SVGShapeFactory svgShapeFactory;

    @Inject
    public SWShapeFactory(final SVGShapeFactory svgShapeFactory) {
        this.svgShapeFactory = svgShapeFactory;
    }

    @Override
    @SuppressWarnings("all")
    public Shape newShape(Object instance) {
        ShapeViewDef def = typeViewDefinitions.get(instance.getClass());
        if (def instanceof TransitionShapeDef) {
            return new TransitionShape((TransitionShapeDef) def, new TransitionView());
        } else {
            return svgShapeFactory.newShape(instance, (SVGShapeDef) def);
        }
    }

    @Override
    @SuppressWarnings("all")
    public Glyph getGlyph(String definitionId) {
        Map.Entry<Class<?>, ShapeViewDef> typeDefs = typeViewDefinitions.entrySet().stream()
                .filter(e -> getDefinitionId(e.getKey()).equals(definitionId))
                .findAny()
                .get();
        Class<?> type = typeDefs.getKey();
        ShapeViewDef def = typeDefs.getValue();
        return def.getGlyph(type, definitionId);
    }
}
