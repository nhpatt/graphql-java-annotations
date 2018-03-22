/**
 * Copyright 2016 Yurii Rashkovskii
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
 */
package graphql.annotations.processor.util;

import graphql.annotations.processor.exceptions.GraphQLAnnotationsException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

/**
 * A package level helper in calling reflective methods and turning them into
 * GraphQLAnnotationsException runtime exceptions
 */
public class ReflectionKit {
    public static <T> T newInstance(Class<T> clazz) throws GraphQLAnnotationsException {
        try {
            try {
                Method getInstance = clazz.getMethod("getInstance", new Class<?>[0]);
                if (Modifier.isStatic(getInstance.getModifiers()) && clazz.isAssignableFrom(getInstance.getReturnType())) {
                    return (T) getInstance.invoke(null);
                }
            } catch (NoSuchMethodException e) {
                // ignore, just call the constructor
            }
            return clazz.newInstance();
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new GraphQLAnnotationsException("Unable to instantiate class : " + clazz, e);
        }
    }

    public static <T> T constructNewInstance(Constructor<T> constructor, Object... args) throws GraphQLAnnotationsException {
        try {
            return constructor.newInstance(args);

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new GraphQLAnnotationsException("Unable to instantiate via constructor : " + constructor, e);
        }
    }

    public static <T> Constructor<T> constructor(Class<T> type, Class<?>... parameterTypes) {
        try {
            return type.getConstructor(parameterTypes);
        } catch (NoSuchMethodException e) {
            throw new GraphQLAnnotationsException("Unable to find constructor", e);
        }
    }

    public static <T> T newInstance(Class<T> clazz, Object parameter) {
        if (parameter != null) {
            for (Constructor<T> constructor : (Constructor<T>[]) clazz.getConstructors()) {
                if (constructor.getParameterCount() == 1 && constructor.getParameters()[0].getType().isAssignableFrom(parameter.getClass())) {
                    return constructNewInstance(constructor, parameter);
                }
            }
        }
        return null;
    }


}
