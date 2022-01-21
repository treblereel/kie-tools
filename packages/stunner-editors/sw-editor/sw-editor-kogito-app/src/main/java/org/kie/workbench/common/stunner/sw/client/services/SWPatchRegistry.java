/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.kie.workbench.common.stunner.sw.client.services;

import java.util.LinkedList;
import java.util.List;

import javax.enterprise.event.Observes;
import javax.inject.Inject;
import javax.inject.Singleton;

import elemental2.dom.DomGlobal;
import org.appformer.client.stateControl.registry.Registry;
import org.kie.workbench.common.stunner.core.client.api.SessionManager;
import org.kie.workbench.common.stunner.core.client.canvas.AbstractCanvasHandler;
import org.kie.workbench.common.stunner.core.client.canvas.event.registration.RegisterChangedEvent;
import org.kie.workbench.common.stunner.core.client.command.CanvasViolation;
import org.kie.workbench.common.stunner.core.client.session.ClientSession;
import org.kie.workbench.common.stunner.core.client.session.impl.EditorSession;
import org.kie.workbench.common.stunner.core.command.Command;
import org.kie.workbench.common.stunner.core.command.impl.CompositeCommand;
import org.kie.workbench.common.stunner.core.command.impl.DeferredCommand;
import org.kie.workbench.common.stunner.core.command.impl.DeferredCompositeCommand;

@Singleton
public class SWPatchRegistry {

    private final SessionManager sessionManager;
    private final List<String> patchUpdates;

    @Inject
    public SWPatchRegistry(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
        this.patchUpdates = new LinkedList<>();
    }

    // Being called for every new command being executed.
    public void onRegistryChangedEvent(@Observes RegisterChangedEvent changedEvent) {
        ClientSession currentSession = sessionManager.getCurrentSession();
        if (currentSession instanceof EditorSession) {
            EditorSession session = (EditorSession) currentSession;
            Registry<Command<AbstractCanvasHandler, CanvasViolation>> commandRegistry = session.getCommandRegistry();
            List<Command<AbstractCanvasHandler, CanvasViolation>> history = commandRegistry.getHistory();
            if (history.size() > 0) {
                onCommandExecuted(history.get(0));
            }
        }
    }

    public List<String> getPatchUpdates() {
        return patchUpdates;
    }

    private void applyPathForCommand(Command<AbstractCanvasHandler, CanvasViolation> command) {
        DomGlobal.console.log("+- " + command.toString());
        patchUpdates.add(command.toString());
    }

    private void onCommandExecuted(Command<AbstractCanvasHandler, CanvasViolation> command) {
        if (command instanceof CompositeCommand) {
            onCommandsExecuted(((CompositeCommand<AbstractCanvasHandler, CanvasViolation>) command).getCommands());
        } else if (command instanceof DeferredCompositeCommand) {
            onCommandsExecuted(((DeferredCompositeCommand<AbstractCanvasHandler, CanvasViolation>) command).getCommands());
        } else if (command instanceof DeferredCommand){
            onCommandExecuted(((DeferredCommand<AbstractCanvasHandler, CanvasViolation>) command).getCommand());
        } else {
            applyPathForCommand(command);
        }
    }

    private void onCommandsExecuted(List<Command<AbstractCanvasHandler, CanvasViolation>> commands) {
        for (Command<AbstractCanvasHandler, CanvasViolation> c : commands) {
            onCommandExecuted(c);
        }
    }
}
