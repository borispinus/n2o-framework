package net.n2oapp.framework.api.metadata.compile;

import net.n2oapp.framework.api.metadata.Compiled;
import net.n2oapp.framework.api.metadata.SourceMetadata;
import net.n2oapp.framework.api.metadata.aware.ExtensionAttributesAware;
import net.n2oapp.framework.api.metadata.meta.BindLink;
import net.n2oapp.framework.api.metadata.meta.ModelLink;

import java.util.Map;
import java.util.Set;

/**
 * Процессор сборки метаданных
 */
public interface CompileProcessor {

    /**
     * Собрать объект
     *
     * @param source  Исходный объект
     * @param context Контекст сборки
     * @param scope   Объекты, влияющие на последующую сборку. Должны быть разных классов.
     * @param <S>     Тип исходного объекта
     * @param <D>     Тип собранного объекта
     * @return Собранный объект
     */
    <D extends Compiled, S> D compile(S source, CompileContext<?, ?> context, Object... scope);

    /**
     * Связать метаданные с данными
     *
     * @param compiled Метаданная
     * @param <D>      Тип метаданной
     */
    <D extends Compiled> void bind(D compiled);

    /**
     * Собрать дополнительные атрибуты
     *
     * @param source исходный объект с атрибутами
     * @return собранные атрибуты
     */
    Map<String, Object> mapAttributes(ExtensionAttributesAware source);

    /**
     * Получить метаданную, оказывающую влияние на сборку
     *
     * @param scopeClass Класс метаданной
     * @param <D>        Тип скоупа
     * @return Метаданная, оказывающая влияние на сборку, или null
     */
    <D> D getScope(Class<D> scopeClass);

    /**
     * Получить собранный объект по идентификатору
     *
     * @param context Контекст сборки
     * @param <D>     Тип собранного объекта
     * @return Собранный объект
     */
    <D extends Compiled> D getCompiled(CompileContext<D, ?> context);

    /**
     * Получить исходный объект по идентификатору
     *
     * @param id          Идентификатор
     * @param sourceClass Класс исходного объекта
     * @param <S>         Тип исходного объекта
     * @return Исходный объект
     */
    <S extends SourceMetadata> S getSource(String id, Class<S> sourceClass);

    /**
     * Зарегистрировать новый маршрут метаданных под контекст
     *
     * @param urlPattern Шаблон URL
     * @param context    Контекст сборки
     */
    <D extends Compiled> void addRoute(String urlPattern, CompileContext<D, ?> context);

    /**
     * Заменить плейсхолдер на значение
     *
     * @param placeholder Плейсхолдер
     * @param <T>         Тип значения
     * @return Значение
     */
    <T> T resolve(String placeholder, Class<T> clazz);

    /**
     * Конвентировать значение в объект по домену
     *
     * @param value  значение для конвертации
     * @param domain Домен значения
     * @return значение
     */
    Object resolve(String value, String domain);

    /**
     * Конвентировать значение в объект
     *
     * @param value значение для конвертации
     * @return значение
     */
    Object resolve(String value);


    /**
     * Заменить в тексте плейсхолдеры на значения
     *
     * @param text Текст с плейсхолдерами
     * @return Текст со значениями вместо плейсхолдеров
     */
    String resolveText(String text);
    
     /**
     * Заменить в строке плейсхолдеры {...} на значения, кроме исключений
     *
     * @param text Строка с плейсхолдерами
     * @return Строка со значениями вместо плейсхолдеров
     */
    String resolveParams(String text);

    /**
     * Заменить в адресе плейсхолдеры на значения
     *
     * @param url    Адрес
     * @param pathMappings path параметры
     * @param queryMappings query параметры
     * @return Адрес со значениями вместо плейсхолдеров
     */
    String resolveUrl(String url, Map<String, ? extends BindLink> pathMappings, Map<String, ? extends BindLink> queryMappings);

    /**
     * Заменить в адресе только переданные параметры на значения
     *
     * @param url    Адрес
     * @param params path параметры
     * @return Измененный адрес
     */
    String resolveUrlParams(String url, Set<String> params);

    /**
     * Попытаться разрешить значение ModelLink
     *
     * @param link исходная ссылка на значение
     * @return ссылка с константой(если получилось разрешить ссылку) или исходная ссылка
     */
    ModelLink resolveLink(ModelLink link);

    /**
     * Превратить текст с ссылками в JS код
     * @param text Текст
     * @return JS код или текст, если в нем нет ссылок
     */
    String resolveJS(String text);

    /**
     * Получить локализованное сообщение по коду и аргументам
     *
     * @param messageCode Код сообщения
     * @param arguments   Аргументы сообщения
     * @return Локализованное сообщение
     */
    String getMessage(String messageCode, Object... arguments);

    /**
     * Привести значение к значению по умолчанию, если оно null.
     * Если первое значение по умолчанию тоже null, берется следующее и т.д.
     *
     * @param value              Исходное значение
     * @param defaultValue1      Первое значения по умолчанию
     * @param otherDefaultValues Следующие значения по умолчанию
     * @param <T>                Тип значения
     * @return Значение приведенное к значению по умолчанию
     */
    @SuppressWarnings("unchecked")
    default <T> T cast(T value, T defaultValue1, Object... otherDefaultValues) {
        if (value != null) return value;
        if (defaultValue1 != null) return defaultValue1;
        if (otherDefaultValues != null)
            for (Object defaultValue : otherDefaultValues) {
                if (defaultValue != null) {
                    return (T) defaultValue;
                }
            }
        return null;
    }
}
