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
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class PluginLoader
{

    private final Map<String, PluginClassLoader> loaders = new LinkedHashMap<>();
    private final Pattern[] fileFilters = new Pattern[]{Pattern.compile("\\.jar$"),};
    private final Map<String, Class<?>> classes = new HashMap<>();

    public Plugin loadPlugin(final File file) throws InvalidPluginException
    {

        if (!file.exists())
        {
            throw new InvalidPluginException(new FileNotFoundException(file.getPath() + " does not exist"));
        }

        final File parentFile = file.getParentFile();
        final File dataFolder = new File(parentFile, "ClimbContext");

        if (dataFolder.exists() && !dataFolder.isDirectory())
        {
            throw new InvalidPluginException(String.format(
                    "Projected datafolder: `%s' for %s (%s) exists and is not a directory",
                    dataFolder,
                    "Climb Context",
                    file
            ));
        }

//        for (final String pluginName : description.getDepend()) {
//            if (loaders == null) {
//                throw new UnknownDependencyException(pluginName);
//            }
//            PluginClassLoader current = loaders.get(pluginName);
//
//            if (current == null) {
//                throw new UnknownDependencyException(pluginName);
//            }
//        }
        final PluginClassLoader loader;
        try
        {
            loader = new PluginClassLoader(this, getClass().getClassLoader(), dataFolder, file);
        } catch (InvalidPluginException ex)
        {
            throw ex;
        } catch (Throwable ex)
        {
            throw new InvalidPluginException(ex);
        }

        loaders.put("ClimbContext", loader);

        return loader.plugin;
    }

//    public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
//        Validate.notNull(file, "File cannot be null");
//
//        JarFile jar = null;
//        InputStream stream = null;
//
//        try {
//            jar = new JarFile(file);
//            JarEntry entry = jar.getJarEntry("plugin.yml");
//
//            if (entry == null) {
//                throw new InvalidDescriptionException(new FileNotFoundException("Jar does not contain plugin.yml"));
//            }
//
//            stream = jar.getInputStream(entry);
//
//            return new PluginDescriptionFile(stream);
//
//        } catch (IOException ex) {
//            throw new InvalidDescriptionException(ex);
//        } catch (YAMLException ex) {
//            throw new InvalidDescriptionException(ex);
//        } finally {
//            if (jar != null) {
//                try {
//                    jar.close();
//                } catch (IOException e) {
//                }
//            }
//            if (stream != null) {
//                try {
//                    stream.close();
//                } catch (IOException e) {
//                }
//            }
//        }
//    }
    public Pattern[] getPluginFileFilters()
    {
        return fileFilters.clone();
    }
    
    
    void setClass(final String name, final Class<?> clazz) {
        if (!classes.containsKey(name)) {
            classes.put(name, clazz);
        }
    }

    Class<?> getClassByName(final String name)
    {
        Class<?> cachedClass = classes.get(name);

        if (cachedClass != null)
        {
            return cachedClass;
        } else
        {
            for (String current : loaders.keySet())
            {
                PluginClassLoader loader = loaders.get(current);
                try
                {
                    cachedClass = loader.findClass(name, false);
                } catch (ClassNotFoundException cnfe) {}
                
                if (cachedClass != null)
                {
                    return cachedClass;
                }
            }
        }
        return null;
    }
    
}
