package net.n2oapp.framework.api.metadata.pipeline;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;

import java.util.function.Supplier;

/**
 * Операция по сборке метаданных в конвеере
 */
@FunctionalInterface
public interface PipelineOperation<O, I> {

    /**
     * Выполнить операцию над объектом в канале
     *
     * @param context  Конеткст сборки
     * @param data     Данные запроса
     * @param supplier Поставщик объекта из конвеера
     * @return Объект, отправляемый дальше по конвееру
     */
    O execute(CompileContext<?, ?> context, DataSet data, Supplier<I> supplier, CompileProcessor processor);

}
