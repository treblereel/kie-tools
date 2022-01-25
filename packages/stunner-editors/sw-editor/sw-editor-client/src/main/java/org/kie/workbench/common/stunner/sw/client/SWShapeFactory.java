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
import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeViewDef;
import org.kie.workbench.common.stunner.svg.client.shape.def.SVGShapeDef;
import org.kie.workbench.common.stunner.svg.client.shape.factory.SVGShapeFactory;
import org.kie.workbench.common.stunner.sw.client.shapes.EndShapeDef;
import org.kie.workbench.common.stunner.sw.client.shapes.StartShapeDef;
import org.kie.workbench.common.stunner.sw.client.shapes.StateShapeDef;
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

@ApplicationScoped
public class SWShapeFactory
        implements ShapeFactory<Object, Shape> {

    private final SVGShapeFactory svgShapeFactory;

    @Inject
    public SWShapeFactory(final SVGShapeFactory svgShapeFactory) {
        this.svgShapeFactory = svgShapeFactory;
    }

    private static final Map<Class<?>, ShapeViewDef> typeViewDefinitions = new HashMap<Class<?>, ShapeViewDef>() {{
        put(Start.class, new StartShapeDef());
        put(End.class, new EndShapeDef());
        put(InjectState.class, new StateShapeDef());
        put(SwitchState.class, new StateShapeDef());
        put(Transition.class, new TransitionShapeDef());
        put(StartTransition.class, new TransitionShapeDef());
        put(ErrorTransition.class, new TransitionShapeDef());
    }};

    @Override
    public Shape newShape(Object instance) {
        ShapeViewDef def = typeViewDefinitions.get(instance.getClass());
        if (null != def) {
            if (isTransition(instance)) {
                return newConnector(instance, (TransitionShapeDef) def);
            } else {
                return svgShapeFactory.newShape(instance, (SVGShapeDef) def);
            }
        }
        throw new UnsupportedOperationException("No factory available for building shape [" + instance.getClass().getName() + "]");
    }

    private static boolean isTransition(Object instance) {
        return ((instance instanceof Transition) ||
                (instance instanceof StartTransition) ||
                (instance instanceof ErrorTransition));
    }

    private Shape<ShapeView> newConnector(final Object instance,
                                          final TransitionShapeDef def) {
        TransitionView view = new TransitionView(0,
                                                 0,
                                                 100,
                                                 100);
        return new TransitionShape<>(def, view);
    }

    @Override
    public Glyph getGlyph(String definitionId) {
        Map.Entry<Class<?>, ShapeViewDef> typeDefs = typeViewDefinitions.entrySet().stream()
                .filter(e -> BindableAdapterUtils.getDefinitionId(e.getKey()).equals(definitionId))
                .findAny()
                .get();

        Class<?> type = typeDefs.getKey();
        ShapeViewDef def = typeDefs.getValue();
        return def.getGlyph(type, definitionId);
    }
}
