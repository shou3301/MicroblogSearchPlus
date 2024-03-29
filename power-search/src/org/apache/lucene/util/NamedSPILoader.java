package org.apache.lucene.util;

/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.ServiceLoader;

//import com.sun.xml.internal.ws.api.pipe.Codec;
import org.apache.lucene.codecs.*;

/**
 * Helper class for loading named SPIs from classpath (e.g. Codec, PostingsFormat).
 * @lucene.internal
 */
public final class NamedSPILoader<S extends NamedSPILoader.NamedSPI> implements Iterable<S> {

  private final Map<String,S> services;
  private final Class<S> clazz;

  public NamedSPILoader(Class<S> clazz) {
    this.clazz = clazz;
    final ServiceLoader<S> loader = ServiceLoader.load(clazz);
    ServiceLoader<Codec> testLoader = ServiceLoader.load(Codec.class);
    if (!testLoader.iterator().hasNext())
    	System.out.println("null");
    final LinkedHashMap<String,S> services = new LinkedHashMap<String,S>();
    for (final S service : loader) {
      final String name = service.getName();
      //System.out.println(name);
      // only add the first one for each name, later services will be ignored
      // this allows to place services before others in classpath to make 
      // them used instead of others
      if (!services.containsKey(name)) {
        services.put(name, service);
      }
    }
    this.services = Collections.unmodifiableMap(services);
  }
  
  public S lookup(String name) {
    final S service = services.get(name);
    if (service != null) return service;
    throw new IllegalArgumentException("A SPI class of type "+clazz.getName()+" with name '"+name+"' does not exist. "+
     "You need to add the corresponding JAR file supporting this SPI to your classpath."+
     "The current classpath supports the following names: "+availableServices());
  }

  public Set<String> availableServices() {
    return services.keySet();
  }
  
  public Iterator<S> iterator() {
    return services.values().iterator();
  }
  
  public static interface NamedSPI {
    String getName();
  }
  
}
