package net.n2oapp.framework.boot;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.n2oapp.cache.template.SyncCacheTemplate;
import net.n2oapp.engine.factory.integration.spring.OverrideBean;
import net.n2oapp.framework.api.MetadataEnvironment;
import net.n2oapp.framework.api.context.ContextProcessor;
import net.n2oapp.framework.api.data.DomainProcessor;
import net.n2oapp.framework.api.data.QueryProcessor;
import net.n2oapp.framework.api.event.N2oEventBus;
import net.n2oapp.framework.api.metadata.compile.*;
import net.n2oapp.framework.api.metadata.global.dao.N2oQuery;
import net.n2oapp.framework.api.metadata.global.dao.object.N2oObject;
import net.n2oapp.framework.api.metadata.global.view.fieldset.N2oFieldSet;
import net.n2oapp.framework.api.metadata.header.N2oHeader;
import net.n2oapp.framework.api.metadata.global.view.page.N2oPage;
import net.n2oapp.framework.api.metadata.global.view.widget.N2oWidget;
import net.n2oapp.framework.api.metadata.io.IOProcessor;
import net.n2oapp.framework.api.metadata.io.IOProcessorAware;
import net.n2oapp.framework.api.metadata.local.CompilerHolder;
import net.n2oapp.framework.api.metadata.local.N2oCompiler;
import net.n2oapp.framework.api.metadata.menu.N2oMenu;
import net.n2oapp.framework.api.metadata.persister.NamespacePersisterFactory;
import net.n2oapp.framework.api.metadata.pipeline.*;
import net.n2oapp.framework.api.metadata.reader.ConfigMetadataLocker;
import net.n2oapp.framework.api.metadata.reader.NamespaceReaderFactory;
import net.n2oapp.framework.api.metadata.validate.SourceValidator;
import net.n2oapp.framework.api.metadata.validate.SourceValidatorFactory;
import net.n2oapp.framework.api.pack.MetadataPack;
import net.n2oapp.framework.api.reader.SourceLoader;
import net.n2oapp.framework.api.reader.SourceLoaderFactory;
import net.n2oapp.framework.api.register.*;
import net.n2oapp.framework.api.register.route.MetadataRouter;
import net.n2oapp.framework.api.register.route.RouteRegister;
import net.n2oapp.framework.api.register.scan.MetadataScanner;
import net.n2oapp.framework.api.register.scan.MetadataScannerFactory;
import net.n2oapp.framework.api.script.ScriptProcessor;
import net.n2oapp.framework.config.ConfigStarter;
import net.n2oapp.framework.config.N2oApplicationBuilder;
import net.n2oapp.framework.config.compile.pipeline.N2oEnvironment;
import net.n2oapp.framework.config.compile.pipeline.N2oPipelineOperationFactory;
import net.n2oapp.framework.config.compile.pipeline.N2oPipelineSupport;
import net.n2oapp.framework.config.compile.pipeline.operation.*;
import net.n2oapp.framework.config.io.IOProcessorImpl;
import net.n2oapp.framework.config.metadata.CompileProcessorAdapter;
import net.n2oapp.framework.config.metadata.compile.*;
import net.n2oapp.framework.config.metadata.compile.toolbar.CrudGenerator;
import net.n2oapp.framework.config.metadata.transformer.factory.TransformerFactory;
import net.n2oapp.framework.config.persister.MetadataPersister;
import net.n2oapp.framework.config.persister.N2oMetadataPersisterFactory;
import net.n2oapp.framework.config.reader.*;
import net.n2oapp.framework.config.reader.util.N2oJdomTextProcessing;
import net.n2oapp.framework.config.register.*;
import net.n2oapp.framework.config.register.dynamic.N2oDynamicMetadataProviderFactory;
import net.n2oapp.framework.config.register.dynamic.JavaSourceLoader;
import net.n2oapp.framework.config.register.route.N2oRouteRegister;
import net.n2oapp.framework.config.register.route.N2oRouter;
import net.n2oapp.framework.config.register.scan.N2oMetadataScannerFactory;
import net.n2oapp.framework.config.util.SubModelsProcessor;
import net.n2oapp.framework.config.validate.N2oSourceValidatorFactory;
import net.n2oapp.framework.config.warmup.HeaderWarmUpper;
import net.n2oapp.framework.engine.util.json.N2oBeanSerializerFactory;
import net.n2oapp.properties.io.PropertiesInfoCollector;
import net.n2oapp.watchdir.WatchDir;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.*;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;

import java.text.SimpleDateFormat;
import java.util.*;

import static java.util.Arrays.asList;

/**
 * Конфигурация сборки и хранения метаданных
 */
@Configuration
@Import(N2oCacheConfiguration.class)
@ComponentScan(basePackages = {"net.n2oapp.framework.config", "net.n2oapp.framework.header"}, lazyInit = true)
public class N2oMetadataConfiguration {

    @Value("${n2o.config.path}")
    private String configPath;

    @Value("${n2o.format.date}")
    private String dataFormat;

    @Value("${n2o.config.readonly}")
    private boolean readonly;

    @Bean(name = "n2oObjectMapper")
    public ObjectMapper n2oObjectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        objectMapper.setSerializerFactory(new N2oBeanSerializerFactory());
        objectMapper.setDateFormat(new SimpleDateFormat(dataFormat));
        objectMapper.setVisibility(objectMapper.getSerializationConfig().getDefaultVisibilityChecker()
                .withFieldVisibility(JsonAutoDetect.Visibility.NONE)
                .withGetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withSetterVisibility(JsonAutoDetect.Visibility.NONE)
                .withCreatorVisibility(JsonAutoDetect.Visibility.NONE)
                .withIsGetterVisibility(JsonAutoDetect.Visibility.NONE));
        return objectMapper;
    }

    @Bean
    public DomainProcessor domainProcessor(@Qualifier("n2oObjectMapper") ObjectMapper objectMapper) {
        return new DomainProcessor(objectMapper, dataFormat);
    }

    @Bean
    public N2oEventBus n2oEventBus(ApplicationEventPublisher publisher) {
        return new N2oEventBus(publisher);
    }

    @Bean
    public MetadataPersister metadataPersister() {
        return new MetadataPersister(readonly);
    }

    @Bean
    public CacheControl n2oCacheControl(CacheManager cacheManager) {
        return new CacheControl(cacheManager);
    }

    @Bean
    public PropertiesInfoCollector propertiesMetaInfoCollector() {
        return new PropertiesInfoCollector("classpath*:META-INF/n2o.properties");
    }

    @Bean
    @Primary
    public MetadataRegister metadataRegister() {
        return new N2oMetadataRegister();
    }

    @Bean
    public WatchDir watchDir() {
        return new WatchDir();
    }

    @Bean
    public ConfigMetadataLocker configMetadataLocker() {
        return new ConfigMetadataLockerImpl(configPath);
    }


    @Bean
    public RouteRegister routeRegister() {
        return new N2oRouteRegister();
    }

    @Bean
    public MetadataRouter n2oRouter(N2oRouteRegister routeRegister, MetadataEnvironment env) {
        return new N2oRouter(routeRegister, N2oPipelineSupport.readPipeline(env)
                .read().transform().validate().cache().copy().compile().transform());
    }

    @Bean
    public TransformerFactory transformerFactory(ApplicationContext context) {
        TransformerFactory transformerFactory = new TransformerFactory();
        transformerFactory.setApplicationContext(context);
        return transformerFactory;
    }

    @Bean
    public N2oCompiler metadataCompiler(MetadataEnvironment environment, MetadataRegister metadataRegister) {
        PipelineSupport p = new N2oPipelineSupport(environment);
        return new CompileProcessorAdapter(new N2oCompileProcessor(environment), metadataRegister);
    }

    @Bean
    public CompilerHolder compilerHolder(N2oCompiler compiler) {
        return new CompilerHolder(compiler);
    }

    @Bean
    public SubModelsProcessor subModelsProcessor(QueryProcessor queryProcessor, N2oCompiler compiler) {
        return new SubModelsProcessor(queryProcessor, compiler);
    }

    @Bean
    public ScriptProcessor scriptProcessor() {
        ScriptProcessor scriptProcessor = new ScriptProcessor();
        scriptProcessor.setDateFormat(dataFormat);
        return scriptProcessor;
    }

    @Bean
    public N2oJdomTextProcessing n2oJdomTextProcessing(MessageSourceAccessor n2oMessageSourceAccessor) {
        return new N2oJdomTextProcessing(n2oMessageSourceAccessor);
    }

    @Bean
    public SourceTypeRegister sourceTypeRegister() {
        SourceTypeRegister register = new N2oSourceTypeRegister();
        register.addAll(asList(new MetaType("object", N2oObject.class),
                new MetaType("query", N2oQuery.class),
                new MetaType("page", N2oPage.class),
                new MetaType("widget", N2oWidget.class),
                new MetaType("fieldset", N2oFieldSet.class),
                new MetaType("header", N2oHeader.class),
                new MetaType("menu", N2oMenu.class)));
        return register;
    }

    @Bean
    public MetadataEnvironment n2oEnvironment(Map<String, ButtonGenerator> generators,
                                              MessageSourceAccessor messageSourceAccessor,
                                              ConfigurableEnvironment springEnv,
                                              DomainProcessor domainProcessor,
                                              ContextProcessor contextProcessor,
                                              SourceTypeRegister sourceTypeRegister,
                                              MetadataRegister metadataRegister,
                                              RouteRegister routeRegister,
                                              MetadataScannerFactory metadataScannerFactory,
                                              SourceLoaderFactory sourceReaderFactory,
                                              SourceValidatorFactory sourceValidatorFactory,
                                              NamespacePersisterFactory persisterFactory,
                                              NamespaceReaderFactory readerFactory,
                                              SourceCompilerFactory sourceCompilerFactory,
                                              CompileTransformerFactory compileTransformerFactory,
                                              SourceTransformerFactory sourceTransformerFactory,
                                              SourceMergerFactory sourceMergerFactory,
                                              MetadataBinderFactory metadataBinderFactory,
                                              PipelineOperationFactory pipelineOperationFactory,
                                              DynamicMetadataProviderFactory dynamicMetadataProviderFactory,
                                              ExtensionAttributeMapperFactory extensionAttributeMapperFactory,
                                              ButtonGeneratorFactory buttonGeneratorFactory) {
        ((CrudGenerator)generators.get("crudGenerator")).setButtonGeneratorFactory(buttonGeneratorFactory);
        N2oEnvironment environment = new N2oEnvironment();
        environment.setSystemProperties(springEnv);
        environment.setMessageSource(messageSourceAccessor);
        environment.setSourceTypeRegister(sourceTypeRegister);
        environment.setMetadataRegister(metadataRegister);
        environment.setRouteRegister(routeRegister);
        environment.setMetadataScannerFactory(metadataScannerFactory);
        environment.setDynamicMetadataProviderFactory(dynamicMetadataProviderFactory);
        environment.setSourceLoaderFactory(sourceReaderFactory);
        environment.setNamespacePersisterFactory(persisterFactory);
        environment.setNamespaceReaderFactory(readerFactory);
        environment.setSourceValidatorFactory(sourceValidatorFactory);
        environment.setSourceCompilerFactory(sourceCompilerFactory);
        environment.setCompileTransformerFactory(compileTransformerFactory);
        environment.setSourceTransformerFactory(sourceTransformerFactory);
        environment.setSourceMergerFactory(sourceMergerFactory);
        environment.setPipelineOperationFactory(pipelineOperationFactory);
        environment.setMetadataBinderFactory(metadataBinderFactory);
        environment.setDomainProcessor(domainProcessor);
        environment.setContextProcessor(contextProcessor);
        environment.setExtensionAttributeMapperFactory(extensionAttributeMapperFactory);
        environment.setButtonGeneratorFactory(buttonGeneratorFactory);
        return environment;
    }

    @Bean
    public N2oApplicationBuilder n2oApplicationBuilder(MetadataEnvironment n2oEnvironment,
                                                       Optional<Map<String, MetadataPack<? super N2oApplicationBuilder>>> beans) {
        N2oApplicationBuilder applicationBuilder = new N2oApplicationBuilder(n2oEnvironment);
        Map<String, MetadataPack<? super N2oApplicationBuilder>> packs = OverrideBean.removeOverriddenBeans(beans.orElse(Collections.emptyMap()));
        applicationBuilder.packs(packs.values().toArray(new MetadataPack[packs.values().size()]));
        return applicationBuilder;
    }

    @Bean(destroyMethod = "stop")
    public ConfigStarter configStarter(N2oEventBus eventBus,
                                       ConfigMetadataLocker locker,
                                       WatchDir watchDir,
                                       N2oApplicationBuilder applicationBuilder,
                                       Environment environment) {
        return new ConfigStarter(applicationBuilder, eventBus, locker, watchDir, configPath);
    }

    @Bean
    public HeaderWarmUpper headerWarmUpper(N2oApplicationBuilder applicationBuilder, Environment environment) {
        HeaderWarmUpper headerWarmUpper = new HeaderWarmUpper();
        headerWarmUpper.setEnvironment(environment);
        headerWarmUpper.setApplicationBuilder(applicationBuilder);
        return headerWarmUpper;
    }

    @Configuration
    static class MetadataLoaderConfiguration {


        @Bean
        public XmlMetadataLoader xmlMetadataReader(NamespaceReaderFactory elementReaderFactory) {
            return new XmlMetadataLoader(elementReaderFactory);
        }

        @Bean
        public JavaSourceLoader javaSourceReader(N2oDynamicMetadataProviderFactory dynamicMetadataProviderFactory) {
            return new JavaSourceLoader(dynamicMetadataProviderFactory);//todo сейчас не кешируются объекты, если их вернулось множество
        }

        @Bean
        public GroovySourceReader groovySourceReader() {
            return new GroovySourceReader();
        }
    }

    @Configuration
    static class MetadataFactoryConfiguration {

        @Bean
        MetadataScannerFactory metadataScannerFactory(Optional<Map<String, MetadataScanner>> scaners) {
            return new N2oMetadataScannerFactory(scaners.orElse(Collections.emptyMap()));
        }

        @Bean
        NamespacePersisterFactory persisterFactory(ApplicationContext context) {
            N2oMetadataPersisterFactory metadataPersisterFactory = new N2oMetadataPersisterFactory();
            metadataPersisterFactory.setApplicationContext(context);
            return metadataPersisterFactory;
        }

        @Bean
        NamespaceReaderFactory readerFactory(ApplicationContext context) {
            N2oNamespaceReaderFactory metadataReaderFactory = new N2oNamespaceReaderFactory();
            metadataReaderFactory.setApplicationContext(context);
            return metadataReaderFactory;
        }

        @Bean
        PipelineOperationFactory pipelineOperationFactory(Optional<Map<String, PipelineOperation>> operations) {
            return new N2oPipelineOperationFactory(operations.orElse(Collections.emptyMap()));
        }


        @Bean
        SourceLoaderFactory sourceLoaderFactory(Map<String, SourceLoader> beans) {
            N2oSourceLoaderFactory configReaderFactory = new N2oSourceLoaderFactory(beans);
            return configReaderFactory;
        }

        @Bean
        N2oDynamicMetadataProviderFactory dynamicMetadataProviderFactory(Optional<Map<String, DynamicMetadataProvider>> providers) {
            return new N2oDynamicMetadataProviderFactory(providers.orElse(Collections.emptyMap()));
        }

        @Bean
        SourceValidatorFactory sourceValidatorFactory(Optional<Map<String, SourceValidator>> validators) {
            return new N2oSourceValidatorFactory(validators.orElse(Collections.emptyMap()));
        }

        @Bean
        SourceCompilerFactory sourceCompilerFactory(Map<String, SourceCompiler> compilers) {
            return new N2oSourceCompilerFactory(compilers);
        }

        @Bean
        CompileTransformerFactory compileTransformerFactory(Optional<Map<String, CompileTransformer>> transformers) {
            return new N2oCompileTransformerFactory(transformers.orElse(Collections.emptyMap()));
        }

        @Bean
        SourceTransformerFactory sourceTransformerFactory(Optional<Map<String, SourceTransformer>> transformers) {
            return new N2oSourceTransformerFactory(transformers.orElse(Collections.emptyMap()));
        }

        @Bean
        SourceMergerFactory sourceMergerFactory(Optional<Map<String, SourceMerger>> mergers) {
            return new N2oSourceMergerFactory(mergers.orElse(Collections.emptyMap()));
        }

        @Bean
        MetadataBinderFactory metadataBinderFactory(Optional<Map<String, MetadataBinder>> binders) {
            return new N2oMetadataBinderFactory(binders.orElse(Collections.emptyMap()));
        }

        @Bean
        ExtensionAttributeMapperFactory extensionAttributeMapperFactory(Optional<Map<String, ExtensionAttributeMapper>> extMappers) {
            return new N2oExtensionAttributeMapperFactory(extMappers.orElse(Collections.emptyMap()));
        }

        @Bean
        ButtonGeneratorFactory toolbarItemGeneratorFactory(Map<String, ButtonGenerator> generators) {
            return new N2oButtonGeneratorFactory(generators);
        }
    }

    @Configuration
    static class PipelineOperationConfiguration {

        @Bean
        ReadOperation readOperation(MetadataRegister configRegister, SourceLoaderFactory readerFactory) {
            return new ReadOperation(configRegister, readerFactory);
        }

        @Bean
        MergeOperation mergeOperation(SourceMergerFactory sourceMergerFactory) {
            return new MergeOperation(sourceMergerFactory);
        }

        @Bean
        ValidateOperation validateOperation(SourceValidatorFactory sourceValidatorFactory) {
            return new ValidateOperation(sourceValidatorFactory);
        }

        @Bean
        SourceCacheOperation sourceCacheOperation(CacheManager cacheManager, MetadataRegister metadataRegister) {
            return new SourceCacheOperation(new SyncCacheTemplate(cacheManager), metadataRegister);
        }

        @Bean
        CompileCacheOperation compileCacheOperation(CacheManager cacheManager) {
            return new CompileCacheOperation(new SyncCacheTemplate(cacheManager));
        }

        @Bean
        SourceTransformOperation sourceTransformOperation(SourceTransformerFactory factory) {
            return new SourceTransformOperation(factory);
        }

        @Bean
        CompileTransformOperation compileTransformOperation(CompileTransformerFactory factory) {
            return new CompileTransformOperation(factory);
        }

        @Bean
        CompileOperation compileOperation(SourceCompilerFactory sourceCompilerFactory) {
            return new CompileOperation(sourceCompilerFactory);
        }

        @Bean
        BindOperation bindOperation(MetadataBinderFactory binderFactory) {
            return new BindOperation(binderFactory);
        }

        @Bean
        CopyOperation cloneOperation() {
            return new CopyOperation();
        }

    }

    @Configuration
    static class MetadataIOConfiguration {
        @Bean
        IOProcessor readerProcessor(MessageSourceAccessor messageSourceAccessor,
                                    NamespaceReaderFactory readerFactory) {
            IOProcessorImpl ioProcessor = new IOProcessorImpl(readerFactory);
            ioProcessor.setMessageSourceAccessor(messageSourceAccessor);
            if (readerFactory instanceof IOProcessorAware) {
                ((IOProcessorAware) readerFactory).setIOProcessor(ioProcessor);
            }
            return ioProcessor;
        }

        @Bean
        IOProcessor persisterProcessor(MessageSourceAccessor messageSourceAccessor,
                                       NamespacePersisterFactory persisterFactory) {
            IOProcessorImpl ioProcessor = new IOProcessorImpl(persisterFactory);
            ioProcessor.setMessageSourceAccessor(messageSourceAccessor);
            if (persisterFactory instanceof IOProcessorAware) {
                ((IOProcessorAware) persisterFactory).setIOProcessor(ioProcessor);
            }
            return ioProcessor;
        }
    }
}
