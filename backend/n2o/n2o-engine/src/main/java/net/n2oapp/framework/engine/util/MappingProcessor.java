package net.n2oapp.framework.engine.util;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.framework.api.metadata.global.dao.object.InvocationParameter;
import net.n2oapp.framework.api.metadata.global.dao.object.N2oObject;
import net.n2oapp.framework.api.metadata.global.dao.object.PluralityType;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Утилитный класс для маппинга данных.
 * Пока сделан
 */
public class MappingProcessor {

    private final static ExpressionParser writeParser = new SpelExpressionParser(new SpelParserConfiguration(true, true));
    private static final ExpressionParser readParser = new SpelExpressionParser(new SpelParserConfiguration(false, false));
    private final static Set<String> primitiveTypes = Stream.of("java.lang.Boolean", "java.lang.Character", "java.lang.Byte",
            "java.lang.Short", "java.lang.Integer", "java.lang.Long", "java.lang.Float", "java.lang.Double", "java.util.Date",
            "java.math.BigDecimal").collect(Collectors.toSet());

    /**
     * Входящее преобразование value согласно выражению mapping в объект target
     *
     * @param target  результирующий объект
     * @param mapping выражение преобразования
     * @param value   значение
     */
    public static void inMap(Object target, String mapping, Object value) {
        Expression expression = writeParser.parseExpression(mapping);
        if (target != null) expression.setValue(target, value);
    }

    /**
     * Исходящее преобразование value согласно mapping выражению
     *
     * @param value   исходное значение
     * @param mapping выражения преобразования
     * @return результат преобразования
     */
    public static <T> T outMap(Object value, String mapping, Class<T> clazz) {
        if (value == null)
            return null;
        if (mapping == null) {
            if (clazz.isAssignableFrom(value.getClass()))
                //noinspection unchecked
                return (T) value;
            else
                throw new N2oException("Incompatible value type. Expected is " + clazz + ", but actual is " + value.getClass());
        }
        Expression expression = readParser.parseExpression(mapping);
        return expression.getValue(value, clazz);
    }

    /**
     * Исходящее преобразование value согласно mapping и в target под ключом fieldId
     * В случае если результат после маппинга = null, в target добавляется  defaultValue
     *
     * @param target       данные результирующие
     * @param value        исходное значение
     * @param fieldId      идентификатор поля
     * @param mapping      выражение преобразования
     * @param defaultValue значение по умолчанию
     */
    public static void outMap(DataSet target, Object value, String fieldId, String mapping, Object defaultValue) {
        Expression expression = readParser.parseExpression(mapping);
        Object obj = expression.getValue(value);
        target.put(fieldId, obj == null ? defaultValue : obj);
    }

    /**
     * Генерирует список аргументов для вызова метода.
     *
     * @param dataSet         исходные данные
     * @param mapping         правила маппинга
     * @param argumentClasses список названий классов
     * @return массив объектов
     */
    public static Object[] map(DataSet dataSet, Map<String, String> mapping, List<String> argumentClasses) {
        Object[] instances = instantiateArguments(argumentClasses);
        Object[] result;
        if (instances == null || instances.length == 0) {
            result = new Object[mapping.size()];
        } else {
            result = instances;
        }
        int idx = 0;
        for (Map.Entry<String, String> map : mapping.entrySet()) {
            Expression expression = writeParser.parseExpression(map.getValue() != null ? map.getValue()
                    : "[" + idx + "]");
            expression.setValue(result, dataSet.get(map.getKey()));
            idx++;
        }
        return result;
    }

    private static Object[] instantiateArguments(List<String> arguments) {
        if (arguments == null) return null;
        Object[] argumentInstances = new Object[arguments.size()];
        for (int k = 0; k < arguments.size(); k++) {
            Class argumentClass = null;
            if (primitiveTypes.contains(arguments.get(k))) {
                argumentInstances[k] = null;
            } else {
                try {
                    argumentClass = Class.forName(arguments.get(k));
                    argumentInstances[k] = argumentClass.newInstance();
                } catch (Exception e) {
                    throw new N2oException("Can't create instance of class " + arguments.get(k), e);
                }
            }
        }
        return argumentInstances;
    }

    /**
     * Заменяет в inDataSet значение созданным объектом
     *
     * @param parameter параметр операции
     * @param dataSet   исходные данные
     */
    public static void mapParameter(InvocationParameter parameter, DataSet dataSet) {
        Object data = dataSet.get(parameter.getId());
        if (data == null)
            return;
        if (parameter.getPluralityType() == PluralityType.list
                || parameter.getPluralityType() == PluralityType.set) {
            Collection collection = parameter.getPluralityType() == PluralityType.list
                    ? new ArrayList() : new HashSet();
            for (Object item : (Collection) data) {
                collection.add(mapChildParameters(parameter, (DataSet) item));
            }
            dataSet.put(parameter.getId(), collection);
        } else {
            dataSet.put(parameter.getId(), mapChildParameters(parameter, (DataSet) data));
        }
    }

    /**
     * Создает инстанс и мапит его поля из dataSet
     *
     * @param parameter параметр операции
     * @param dataSet   исходные данные
     */
    public static Object mapChildParameters(InvocationParameter parameter, DataSet dataSet) {
        Object instance;
        try {
            instance = Class.forName(parameter.getEntityClass()).newInstance();
        } catch (ClassNotFoundException
                | IllegalAccessException
                | InstantiationException e) {
            throw new N2oException(e);
        }

        for (N2oObject.Parameter childParam : ((N2oObject.Parameter) parameter).getChildParams()) {
            writeParser.parseExpression(childParam.getMapping()).setValue(instance, dataSet.get(childParam.getId()));
        }
        return instance;
    }
}
