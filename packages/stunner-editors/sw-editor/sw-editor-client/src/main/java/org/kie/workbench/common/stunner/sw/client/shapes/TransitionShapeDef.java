/*
 * Copyright 2016 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.client.shapes;

import java.util.function.BiConsumer;

import org.kie.workbench.common.stunner.core.client.shape.view.ShapeView;
import org.kie.workbench.common.stunner.core.client.shape.view.handler.ViewAttributesHandler;
import org.kie.workbench.common.stunner.core.definition.shape.Glyph;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeDef;
import org.kie.workbench.common.stunner.core.definition.shape.ShapeViewDef;
import org.kie.workbench.common.stunner.sw.client.resources.SWGlyphFactory;
import org.kie.workbench.common.stunner.sw.definition.ErrorTransition;
import org.kie.workbench.common.stunner.sw.definition.StartTransition;
import org.kie.workbench.common.stunner.sw.definition.Transition;

import static org.kie.workbench.common.stunner.core.definition.adapter.binding.BindableAdapterUtils.getDefinitionId;

public class TransitionShapeDef<W>
        implements ShapeViewDef<W, TransitionView> {

    enum Type {
        TRANSITION,
        START,
        ERROR
    }

    enum Direction {
        NONE,
        ONE,
        BOTH
    }

    @Override
    public BiConsumer<W, TransitionView> viewHandler() {
        return new ViewAttributesHandlerBuilder().build()::handle;
    }

    @Override
    public Glyph getGlyph(Class<? extends W> clazz,
                          final String defId) {
        Type type = getTypeByClass(clazz);
        if (type == Type.START) {
            return SWGlyphFactory.TRANSITION_START;
        }
        if (type == Type.ERROR) {
            return SWGlyphFactory.TRANSITION_ERROR;
        }
        return SWGlyphFactory.TRANSITION;
    }

    @Override
    public Class<? extends ShapeDef> getType() {
        return TransitionShapeDef.class;
    }

    private static class ViewAttributesHandlerBuilder<W, V extends ShapeView>
            extends ViewAttributesHandler.Builder<W, V> {

        public ViewAttributesHandlerBuilder() {
            this.fillColor(TransitionShapeDef::getColor)
                    .strokeColor(TransitionShapeDef::getColor)
                    .strokeWidth(bean -> 1d);
        }
    }

    private static final String TYPE_TRANSITION = getDefinitionId(Transition.class);
    private static final String TYPE_START = getDefinitionId(StartTransition.class);
    private static final String TYPE_ERROR = getDefinitionId(ErrorTransition.class);

    public static Type getType(Object transition) {
        Type type = getTypeOrNull(transition);
        if (null != type) {
            return type;
        }
        throw new IllegalStateException("Type [" + transition.getClass() + "] is not a known transition.");
    }

    public static Type getTypeByClass(Class<?> transitionType) {
        Type type = getTypeByIdOrNull(getDefinitionId(transitionType));
        if (null != type) {
            return type;
        }
        throw new IllegalStateException("Type [" + transitionType + "] is not a known transition.");
    }

    private static Type getTypeOrNull(Object transition) {
        String id = getDefinitionId(transition.getClass());
        return getTypeByIdOrNull(id);
    }

    private static Type getTypeByIdOrNull(String id) {
        if (TYPE_TRANSITION.equals(id)) {
            return Type.TRANSITION;
        }
        if (TYPE_START.equals(id)) {
            return Type.START;
        }
        if (TYPE_ERROR.equals(id)) {
            return Type.ERROR;
        }
        return null;
    }

    private static String getColor(Object transition) {
        Type type = getType(transition);
        if (type == Type.START) {
            return "#0000FF";
        }
        if (type == Type.ERROR) {
            return "#FF0000";
        }
        return "#000000";
    }
}
