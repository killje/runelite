/*
 * Copyright (c) 2018, Patrick Beuks (killje) <patrick.beuks@gmail.com>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins;

import com.google.inject.Binder;
import com.google.inject.CreationException;
import com.google.inject.Injector;
import com.google.inject.Module;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.runelite.client.RuneLite;

public class PluginClassLoader extends URLClassLoader
{
    
    private final PluginLoader loader;
    final Plugin plugin;
    private final File dataFolder;
    private final File file;
    private final Map<String, Class<?>> classes = new HashMap<>();
    
    public PluginClassLoader(final PluginLoader loader, final ClassLoader parent, final File dataFolder, final File file) throws MalformedURLException, InvalidPluginException {
        super(new URL[] {file.toURI().toURL()}, parent);

        this.loader = loader;
        this.dataFolder = dataFolder;
        this.file = file;

        try {
            Class<?> jarClass;
            try {
                jarClass = Class.forName("me.killje.climbcontext.ClimbContext", true, this);
            } catch (ClassNotFoundException ex) {
                throw new InvalidPluginException("Cannot find main class `me.killje.climbcontext.ClimbContext`", ex);
            }

            PluginDescriptor pluginDescriptor = jarClass.getAnnotation(PluginDescriptor.class);
            if (pluginDescriptor == null)
			{
                throw new InvalidPluginException("Plugin does not have a descriptor");
            }
            
            Class<? extends Plugin> pluginClass;
            try {
                pluginClass = jarClass.asSubclass(Plugin.class);
            } catch (ClassCastException ex) {
                throw new InvalidPluginException("main class `me.killje.climbcontext.ClimbContext` does not extend Plugin", ex);
            }
            
            plugin = instantiate((Class<Plugin>) pluginClass);
        } catch (PluginInstantiationException ex)
        {
            throw new InvalidPluginException("Could not start plugin", ex);
        }
    }
    
    private Plugin instantiate(Class<Plugin> clazz) throws PluginInstantiationException
	{
		Plugin plugin;
		try
		{
			plugin = clazz.newInstance();
		}
		catch (InstantiationException | IllegalAccessException ex)
		{
			throw new PluginInstantiationException(ex);
		}

		try
		{
			Module pluginModule = (Binder binder) ->
			{
				binder.bind(clazz).toInstance(plugin);
				binder.install(plugin);
			};
			Injector pluginInjector = RuneLite.getInjector().createChildInjector(pluginModule);
			pluginInjector.injectMembers(plugin);
			plugin.injector = pluginInjector;
		}
		catch (CreationException ex)
		{
			throw new PluginInstantiationException(ex);
		}

		return plugin;
	}

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) {
                result = loader.getClassByName(name);
            }

            if (result == null) {
                result = super.findClass(name);

                if (result != null) {
                    loader.setClass(name, result);
                }
            }

            classes.put(name, result);
        }

        return result;
    }

    Set<String> getClasses() {
        return classes.keySet();
    }

    synchronized void initialize(Plugin Plugin) throws Exception {
        plugin.startUp();
    }
    
}
