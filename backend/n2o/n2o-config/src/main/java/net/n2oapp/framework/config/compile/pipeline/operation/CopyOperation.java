package net.n2oapp.framework.config.compile.pipeline.operation;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.metadata.aware.PipelineOperationTypeAware;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.pipeline.PipelineOperation;
import net.n2oapp.framework.api.metadata.pipeline.PipelineOperationType;
import org.springframework.util.SerializationUtils;

import java.util.function.Supplier;

/**
 * Операция копирования метаданных в конвеере
 */
public class CopyOperation<S> implements PipelineOperation<S, S>, PipelineOperationTypeAware {
    @Override
    public PipelineOperationType getPipelineOperationType() {
        return PipelineOperationType.COPY;
    }

    @Override
    public S execute(CompileContext<?, ?> context, DataSet data, Supplier<S> supplier, CompileProcessor processor) {
        return (S) SerializationUtils.deserialize(SerializationUtils.serialize(supplier.get()));
    }

}
