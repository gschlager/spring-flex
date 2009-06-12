/*
 * Copyright 2002-2009 the original author or authors.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.flex.core;

import java.util.LinkedHashSet;
import java.util.Set;

import junit.framework.TestCase;

import org.springframework.flex.config.MessageBrokerConfigProcessor;
import org.springframework.mock.web.MockServletContext;
import org.springframework.web.context.support.StaticWebApplicationContext;

import flex.messaging.FlexContext;
import flex.messaging.MessageBroker;

public abstract class AbstractMessageBrokerTests extends TestCase {

    private final StaticWebApplicationContext context = new StaticWebApplicationContext();

    private MessageBrokerFactoryBean mbfb;

    private final Set<MessageBrokerConfigProcessor> startupProcessors = new LinkedHashSet<MessageBrokerConfigProcessor>();

    private String currentConfigPath;

    protected final void addStartupProcessor(MessageBrokerConfigProcessor processor) {
        this.startupProcessors.add(processor);
    }

    protected final MessageBroker createMessageBroker() throws Exception {
        this.context.setServletContext(new MockServletContext());
        this.mbfb = new MessageBrokerFactoryBean();
        this.mbfb.setServletContext(this.context.getServletContext());
        this.mbfb.setResourceLoader(this.context);
        this.mbfb.setBeanName(super.getName() + "MessageBroker");
        this.mbfb.setBeanClassLoader(this.context.getClassLoader());
        this.currentConfigPath = getServicesConfigPath();
        this.mbfb.setServicesConfigPath(this.currentConfigPath);
        this.mbfb.setConfigProcessors(this.startupProcessors);
        this.mbfb.afterPropertiesSet();

        return (MessageBroker) this.mbfb.getObject();
    }

    protected String getCurrentConfigPath() {
        return this.currentConfigPath;
    }

    protected final MessageBroker getMessageBroker() throws Exception {
        if (FlexContext.getMessageBroker() != null) {
            return FlexContext.getMessageBroker();
        } else {
            return createMessageBroker();
        }
    }

    protected String getServicesConfigPath() {
        return "classpath:org/springframework/flex/core/services-config.xml";
    }

    protected final void setDirty() {
        if (FlexContext.getMessageBroker() != null) {
            FlexContext.getMessageBroker().stop();
            FlexContext.setThreadLocalObjects(null, null, null, null, null, null);
        }
        this.startupProcessors.clear();
    }

}