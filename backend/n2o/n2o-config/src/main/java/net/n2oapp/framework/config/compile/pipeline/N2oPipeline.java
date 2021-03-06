package net.n2oapp.framework.config.compile.pipeline;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.framework.api.metadata.compile.CompileContext;
import net.n2oapp.framework.api.metadata.compile.CompileProcessor;
import net.n2oapp.framework.api.metadata.pipeline.*;
import net.n2oapp.framework.config.metadata.compile.N2oCompileProcessor;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Конвеер для работы с метаданными N2O
 */
public abstract class N2oPipeline implements Pipeline {

    private MetadataEnvironment env;
    protected List<PipelineOperation<?, ?>> operations = new ArrayList<>();

    protected N2oPipeline(MetadataEnvironment env) {
        this.env = env;
    }

    protected void pullOp(PipelineOperationType type) {
        pullOp(env.getPipelineOperationFactory().produce(type));
    }

    protected void pullOp(PipelineOperation<?, ?> op) {
        operations.add(0, op);
    }

    protected Iterator<PipelineOperation<?, ?>> ops() {
        return operations.iterator();
    }

    protected <O, I> O execute(CompileContext<?, ?> context, DataSet data, I input) {
        CompileProcessor processor = new N2oCompileProcessor(env, context, data);
        return execute(context, data, input, processor);
    }

    protected <O, I> O execute(CompileContext<?, ?> context, DataSet data, I input, CompileProcessor processor) {
        return execute(ops(), context, data, input, processor);
    }

    protected <O, I> O execute(Iterator<PipelineOperation<?, ?>> iterator,
                               CompileContext<?, ?> context,
                               DataSet data, I input, CompileProcessor processor) {
        PipelineOperation<O, I> operation = (PipelineOperation<O, I>) iterator.next();
        if (iterator.hasNext()) {
            return operation.execute(context, data, () -> execute(iterator, context, data, input, processor), processor);
        } else {
            return operation.execute(context, data, () -> input, processor);
        }
    }
}
