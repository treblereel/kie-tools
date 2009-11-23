/*
 * Copyright 2009 JBoss, a divison Red Hat, Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.errai.samples.broadcastservice.server;

import com.google.inject.Inject;
import org.jboss.errai.bus.client.CommandMessage;
import org.jboss.errai.bus.client.MessageBus;
import org.jboss.errai.bus.client.MessageCallback;
import org.jboss.errai.bus.server.annotations.Service;

@Service
public class BroadcastService implements MessageCallback {
    private MessageBus bus;

    @Inject
    public BroadcastService(MessageBus bus) {
        this.bus = bus;
    }

    public void callback(CommandMessage message) {
        CommandMessage.create()
                .toSubject("BroadcastReceiver")
                .copy("BroadcastText", message)
                .sendNowWith(bus);
    }
}
