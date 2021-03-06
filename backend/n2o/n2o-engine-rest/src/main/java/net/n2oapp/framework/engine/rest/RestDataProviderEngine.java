package net.n2oapp.framework.engine.rest;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.framework.api.data.MapInvocationEngine;
import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.framework.api.metadata.dataprovider.N2oRestDataProvider;
import net.n2oapp.framework.api.util.RestClient;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static net.n2oapp.framework.engine.data.QueryUtil.normalizeQueryParams;
import static net.n2oapp.framework.engine.data.QueryUtil.replaceListPlaceholder;


/**
 * Сервис вызова rest клиента
 */
public class RestDataProviderEngine implements MapInvocationEngine<N2oRestDataProvider> {

    private RestClient restClient;
    private String baseRestUrl;

    public RestDataProviderEngine(RestClient restClient) {
        this.restClient = restClient;
    }

    public void setBaseRestUrl(String baseRestUrl) {
        this.baseRestUrl = baseRestUrl;
    }

    @Override
    public Object invoke(N2oRestDataProvider invocation, Map<String, Object> data) {
        String query = invocation.getQuery();
        if (query == null)
            throw new N2oException("query mustn't be null");
        final N2oRestDataProvider.Method method = invocation.getMethod() == null ? N2oRestDataProvider.Method.GET : invocation.getMethod();
        Map<String, Object> args = new HashMap<>();
        data.forEach(args::put);
        if (!query.contains("http")) {
            if (!query.startsWith("/"))
                query = "/" + query;
            if (query.contains("//"))
                query = query.replace("//", "/");
            query = baseRestUrl + query;
        }
        query = replaceListPlaceholder(query, "{select}", args.remove("select"), "", (a, b) -> a + invocation.getSelectSeparator() + b);
        query = replaceListPlaceholder(query, "{filters}", args.remove("filters"), "", (a, b) -> a + invocation.getFiltersSeparator() + b);
        query = replaceListPlaceholder(query, "{sorting}", args.remove("sorting"), "", (a, b) -> a + invocation.getSortingSeparator() + b);
        query = replaceListPlaceholder(query, "{join}", args.remove("join"), "", (a, b) -> a + invocation.getJoinSeparator() + b);
        query = resolvePathPlaceholders(query, args);
        query = normalizeQueryParams(query);
        return executeQuery(method.name(), query, args, invocation.getProxyHost(), invocation.getProxyPort());
    }

    private DataSet executeQuery(String method, String query, Map<String, Object> args, String proxyHost,
                                 Integer proxyPort) throws ApacheRestClient.RestException {

        Map<String, String> headers = new HashMap<>();
        args = new HashMap<>(args);

        switch (method) {
            case "GET":
                return restClient.GET(query, Collections.emptyMap(), headers, proxyHost, proxyPort);
            case "POST":
                return restClient.POST(query, args, headers, proxyHost, proxyPort);
            case "PUT":
                return restClient.PUT(query, args, headers, proxyHost, proxyPort);
            case "DELETE":
                return restClient.DELETE(query, args, headers, proxyHost, proxyPort);
            case "HEAD":
                return restClient.HEAD(query, args, headers, proxyHost, proxyPort);
            default:
                throw new UnsupportedOperationException("Method " + method + " unsupported");
        }
    }

    private String resolvePathPlaceholders(String query, Map<String, Object> args) {
        for (String key : new HashSet<>(args.keySet())) {
            String p = "{" + key + "}";
            if (query.contains(p)) {
                String value = args.get(key) == null ? "" : RestUtil.encode(args.get(key).toString());
                query = query.replace("{" + key + "}", value);
                args.remove(key);
            }
        }
        return query;
    }

    @Override
    public Class<? extends N2oRestDataProvider> getType() {
        return N2oRestDataProvider.class;
    }

}
