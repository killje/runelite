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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PluginClassLoader extends URLClassLoader
{
    
    private final PluginManager manager;
    private final Map<String, Class<?>> classes = new HashMap<>();
    private final List<Class<? extends Plugin>> pluginClasses = new ArrayList<>();
    
    public PluginClassLoader(final PluginManager manager, final ClassLoader parent, final File jarFile) throws MalformedURLException, InvalidPluginException {
        super(new URL[] {jarFile.toURI().toURL()}, parent);

        this.manager = manager;
        JarFile jar;

        try
        {
            jar = new JarFile(jarFile);
        } catch (IOException ex)
        {
            throw new InvalidPluginException("Could not open the jar folder for: `" + jarFile.getName() + "`", ex);
        }

        for (Enumeration<JarEntry> entries = jar.entries(); entries.hasMoreElements();)
        {
            
            JarEntry entry = entries.nextElement();
            String entryName = entry.getName();
            if (!entryName.endsWith(".class")) {
                continue;
            }
            
            String classname = entryName.replace('/', '.').substring(0, entryName.length() - 6);
            try
            {
                Class<?> clazz = Class.forName(classname, true, this);
                
                if (manager.isPluginClass(clazz)) {
                    pluginClasses.add((Class<Plugin>) clazz);
                }
            } catch (ClassNotFoundException ex)
            {
                log.warn("Failed to load class `{}` from {}", classname, jarFile.getName());
            }
            
        }
        
    }

    public List<Class<? extends Plugin>> getPluginClasses()
    {
        return pluginClasses;
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        return findClass(name, true);
    }

    Class<?> findClass(String name, boolean checkGlobal) throws ClassNotFoundException {
        Class<?> result = classes.get(name);

        if (result == null) {
            if (checkGlobal) {
                result = manager.getClassByName(name);
            }

            if (result == null) {
                result = super.findClass(name);

                if (result != null) {
                    manager.setClass(name, result);
                }
            }

            classes.put(name, result);
        }

        return result;
    }
    
}
