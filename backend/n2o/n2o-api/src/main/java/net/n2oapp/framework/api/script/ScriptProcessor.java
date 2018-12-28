package net.n2oapp.framework.api.script;

import net.n2oapp.criteria.dataset.DataSet;
import net.n2oapp.criteria.dataset.Interval;
import net.n2oapp.framework.api.StringUtils;
import net.n2oapp.framework.api.exception.N2oException;
import net.n2oapp.framework.api.metadata.global.view.widget.table.N2oSwitch;
import net.n2oapp.properties.StaticProperties;
import org.apache.commons.io.IOUtils;
import org.mozilla.javascript.EvaluatorException;
import org.mozilla.javascript.Parser;
import org.mozilla.javascript.ast.AstNode;
import org.mozilla.javascript.ast.Name;
import org.mozilla.javascript.ast.NodeVisitor;
import org.mozilla.javascript.ast.PropertyGet;

import javax.script.Bindings;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Утилитный класс для генерации js скриптов
 */
public class ScriptProcessor {
    private static final ScriptProcessor instance = new ScriptProcessor();
    private static final List<String> momentFuncs = Arrays.asList("moment", "now", "today", "yesterday", "tomorrow",
            "beginWeek", "endWeek", "beginMonth", "endMonth", "beginQuarter", "endQuarter", "beginYear", "endYear");
    private static final String spread_operator = "*.";

    private String dateFormat;
    private final static ScriptEngineManager engineMgr = new ScriptEngineManager();
    private static volatile ScriptEngine scriptEngine;
    private static Object underscoreJs;
    private static Object numeralJs;


    @Deprecated
    public static ScriptProcessor getInstance() {
        return instance;
    }

    public static String resolveLinks(String text) {
        if (text == null)
            return null;
        if (StringUtils.hasLink(text)) {
            String expr = text;
            if (expr.contains("${")) {
                while (expr.contains("${")) {
                    int idx = expr.indexOf("}", expr.indexOf("${"));
                    boolean notEnd = idx < expr.length() - 1;
                    expr = expr.replaceFirst("\\$\\{", "\\$<");
                    if (notEnd) {
                        expr = expr.substring(0, idx) + ">>" + expr.substring(idx + 1);
                    } else {
                        expr = expr.substring(0, idx) + ">>";
                    }

                }
            }
            if (expr.contains("#{")) {
                while (expr.contains("#{")) {
                    int idx = expr.indexOf("}", expr.indexOf("#{"));
                    boolean notEnd = idx < expr.length() - 1;
                    expr = expr.replaceFirst("#\\{", "#<");
                    if (notEnd) {
                        expr = expr.substring(0, idx) + ">>" + expr.substring(idx + 1);
                    } else {
                        expr = expr.substring(0, idx) + ">>";
                    }
                }
            }
            if (StringUtils.hasLink(expr)) {
                expr = resolveToJsString(expr);
                return toJsExpression(expr.replaceAll("#<", "#{").replaceAll("\\$<", "\\${").replaceAll(">>", "}"));
            } else {
                return expr.replaceAll("#<", "#{").replaceAll("\\$<", "\\${").replaceAll(">>", "}");
            }
        }
        if (text.equals("false"))
            return toJsExpression(text);//"false" as String is true for JavaScript
        return text;
    }

    /**
     * Преобразование выражения в самовызывающуюся js функцию.
     * Примеры.
     * "if (gender.id = 1) return 'М'; else return 'Ж';" -> "(function(){if (gender.id = 1) return 'М'; else return 'Ж';})()"
     * "gender.id == 1" -> "(function(){return gender.id == 1;})()"
     * "function(){if (gender.id = 1) return 'М'; else return 'Ж';}" -> "(function(){if (gender.id = 1) return 'М'; else return 'Ж';})()"
     * "(function(){ return '123'; })()" -> "(function(){ return '123'; })()"
     * @param text выражение сождержащее ссылки
     * @return js функция
     */
    public static String resolveFunction(String text) {
        if (text == null) return null;
        if (text.startsWith("(function")) {
            return text;
        }
        if (text.startsWith("function")) {
            return String.format("(%s)()", text);
        }
        if (text.contains("return")) {
            return String.format("(function(){ %s })()", text);
        } else {
            return text;
        }
    }


    /**
     * Преобразование выражений с ссылками в js код.
     * Примеры.
     * {id} -> `id` (String), "1" -> 1 (Integer)
     * "true" -> true (Boolean), "1,2" -> 1,2 (String)
     * "test" -> test (String), "`1+1`" -> `1+1` (String)
     * "Test{id}" -> `'Test'+id` (String)
     * #{test} -> #{test} (String), ${test} -> ${test} (String)
     * {test*.id} -> `.map(fuct...)`
     *
     * @param text выражение сождержащее ссылки
     * @return js код
     */
    public static Object resolveExpression(String text) {
        String expression = resolveLinks(text);
        if (expression.equals("true") || expression.equals("false"))
            return Boolean.valueOf(expression);
        if (expression.matches("([\\d]+)")) {
            try {
                return Integer.parseInt(expression);
            } catch (NumberFormatException e) {
            }
        }
        return expression;
    }

    /**
     * Преобразование списка expression в js код
     * Примеры
     * "1" -> [1] (List<Integer>), ("1", "2") -> [1,2] (List<Integer>)
     * ("Test1", "Test2") -> ["Test1","Test2"] (List<String>)
     * ("true", "false") -> [true,false] (List<Boolean>)
     * ("{id}") -> `[id]` (String), ("{id1}", "{id2}") -> `[id1,id2]` (String)
     * ("Test{id1}", "Test{id2}") -> `['Test'+id1,'Test'+id2]` (String)
     *
     * @param values значения, записанное в xml как список value
     * @return js код
     */
    public static Object resolveArrayExpression(String... values) {
        //todo реализовать варианты с плейсхлодарами(пока реализовано только для констант)
        if (values.length == 0) {
            return null;
        } else {
            List result = new ArrayList();
            for (String value : values) {
                result.add(resolveExpression(value));
            }
            return result;
        }
    }

    private static String toJsExpression(String expression) {
        return "`" + expression + "`";
    }

    private static Object removeJsBraces(Object expression) {
        if (expression instanceof String)
            return ((String) expression).replaceAll("`", "");
        return expression;
    }

    private static String resolveToJsString(String text) {
        if (text == null) return null;
        StringBuilder sb = new StringBuilder();
        String[] split = text.split("\\{");
        if (split.length <= 1)
            return text;
        if (!split[0].equals("")){
            sb.append("'".concat(split[0]).concat("'"));
        }
        for (int i = 1; i < split.length; i++) {
            int idxSuffix = split[i].indexOf("}");
            if (idxSuffix > 0) {
                String value = split[i].substring(0, idxSuffix);
                if (value.contains(spread_operator)) {
                    value = value.substring(0, value.indexOf(spread_operator)) + ".map(function(t){return t." +
                            value.substring(value.indexOf(spread_operator) + 2) + "})";
                }
                sb.append("+").append(value);
                if (idxSuffix < split[i].length() - 1) {
                    String afterValue = split[i].substring(idxSuffix + 1);
                    sb.append("+'").append(afterValue).append("'");
                }
            }
        }
        String res = sb.toString();
        if (res.startsWith("+")) {
            res = res.replaceFirst("\\+", "");
        }
        if (res.endsWith("+")) {
            res = res.substring(0, res.length() - 1);
        }
        return res;
    }

    public static String createFunctionCall(String funcName, Object... args) {
        StringBuilder res = new StringBuilder();
        res.append(funcName);
        res.append('(');
        boolean begin = true;
        for (Object arg : args) {
            if (!begin) res.append(",");
            //добавляем кавычки для строк
            String str = arg instanceof String ? "'" + arg.toString() + "'" : arg.toString();
            res.append(str);
            begin = false;
        }
        res.append(')');
        return res.toString();
    }

    public static String createSelfInvokingFunction(String code) {
        return "function(){" + code + "}()";
    }

    public static String buildExpressionForSwitch(N2oSwitch n2oSwitch) {
        if (n2oSwitch == null
                || (n2oSwitch.getCases() == null && n2oSwitch.getDefaultCase() == null)
                || (n2oSwitch.getValueFieldId() == null || n2oSwitch.getValueFieldId().length() == 0))
            return null;
        StringBuilder b = new StringBuilder("`");
        for (String key : n2oSwitch.getCases().keySet()) {
            b.append(n2oSwitch.getValueFieldId() + " == '" + key + "' ? ");
            b.append("'" + n2oSwitch.getCases().get(key) + "' : ");
        }
        if (n2oSwitch.getDefaultCase() != null) {
            b.append("'" + n2oSwitch.getDefaultCase() + "'");
        } else {
            b.append("null");
        }
        return b.toString().trim() + "`";
    }


    public String buildIsNullExpression(String variable) {
        StringBuilder res = null;
        StringBuilder tmpVar = null;
        for (String s : variable.split("\\.")) {
            if (tmpVar == null)
                tmpVar = new StringBuilder();
            else
                tmpVar.append(".");
            tmpVar.append(s);
            if (res == null)
                res = new StringBuilder();
            else
                res.append(" || ");
            res.append(buildUndefinedExpression(tmpVar.toString()));
        }
        res.append(" || (" + variable + " === null)");
        return res.toString();
    }


    public String buildIsNotNullExpression(String variable) {
        return variable + " != null";
    }


    private String buildUndefinedExpression(String variable) {
        return "(typeof " + variable + " === 'undefined')";
    }

    public String buildEqualExpression(String variable, Object value) {
        return variable + " == " + getString(value);
    }

    public String buildLikeExpression(String variable, String value) {
        String res = "%s.indexOf(%s) !== -1";
        return String.format(res, variable, getString(value));
    }

    public String buildLikeStartExpression(String variable, String value) {
        String res = "%s.indexOf(%s) === 0";
        return String.format(res, variable, getString(value));
    }

    public String buildInListExpression(String variable, List<Object> values) {
        String res = "_.indexOf(_.isArray(%s) ? %s : [%s], %s) >= 0";
        String array = values.stream().map(this::getString).collect(Collectors.toList()).toString();
        return String.format(res, array, array, array, variable);
    }

    public String buildOverlapListExpression(String variable, List<Object> values) {
        String res = "_.intersection(_.isArray(%s) ? %s : [%s], %s).length > 0";
        return String.format(res, variable, variable, variable, values.stream().map(this::getString).collect(Collectors.toList()).toString());
    }

    public String buildContainsListExpression(String variable, List<Object> values) {
        String res = "_.intersection(_.isArray(%s) ? %s : [%s], %s).length === (%s).length";
        String vals = values.stream().map(this::getString).collect(Collectors.toList()).toString();
        return String.format(res, variable, variable, variable, vals, vals);
    }

    public String buildNotInListExpression(String variable, List<Object> values) {
        return "!(" + buildInListExpression(variable, values) + ")";
    }

    public String buildLessExpression(String variable, Comparable comparable) {
        if (comparable instanceof Date) return buildLessExpressionForDate(variable,
                (Date) comparable);
        return variable + " < " + comparable;
    }

    public String buildNotEqExpression(String variable, Object value) {
        return variable + " != " + getString(value);
    }

    private String buildLessExpressionForDate(String variable, Date date) {
        StringBuilder exp = new StringBuilder();
        String var = "new Date(%s.replace(/(\\d{2})\\.(\\d{2})\\.(\\d{4})/,'$3-$2-$1')).getTime()";
        var = String.format(var, variable);
        exp.append(var);
        exp.append(" < ");
        String s = "new Date('%s').getTime()";
        exp.append(String.format(s, new SimpleDateFormat("MM.dd.yyyy HH:mm:SS").format(date)));
        return exp.toString();
    }

    public String buildMoreExpression(String variable, Comparable comparable) {
        if (comparable instanceof Date) return buildMoreExpressionForDate(variable,
                (Date) comparable);
        return variable + " > " + comparable;
    }

    private String buildMoreExpressionForDate(String variable, Date date) {
        StringBuilder exp = new StringBuilder();
        String var = "new Date(%s.replace(/(\\d{2})\\.(\\d{2})\\.(\\d{4})/,'$3-$2-$1')).getTime()";
        var = String.format(var, variable);
        exp.append(var);
        exp.append(" > ");
        String s = "new Date('%s').getTime()";
        exp.append(String.format(s, new SimpleDateFormat("MM.dd.yyyy HH:mm:SS").format(date)));
        return exp.toString();
    }

    public String buildInIntervalExpression(String variable, Interval interval) {
        if (interval.getDomain().equals(Date.class)) return buildInDateIntervalExpression(variable,
                (Interval<Date>) interval);
        StringBuilder exp = new StringBuilder();
        exp.append(variable);
        exp.append(" > ");
        exp.append(interval.getBegin());
        exp.append(" && ");
        exp.append(variable);
        exp.append(" < ");
        exp.append(interval.getEnd());
        return exp.toString();
    }


    private String buildInDateIntervalExpression(String variable, Interval<Date> interval) {
        StringBuilder exp = new StringBuilder();
        String var = "new Date(%s.replace(/(\\d{2})\\.(\\d{2})\\.(\\d{4})/,'$3-$2-$1')).getTime()";
        var = String.format(var, variable);
        exp.append(var);
        exp.append(" > ");
        String s = "new Date('%s').getTime()";
        exp.append(String.format(s, new SimpleDateFormat("MM.dd.yyyy HH:mm:SS").format(interval.getBegin())));
        exp.append(" && ");
        exp.append(var);
        exp.append(" < ");
        exp.append(String.format(s, new SimpleDateFormat("MM.dd.yyyy HH:mm:SS").format(interval.getEnd())));
        return exp.toString();
    }


    public static String buildAddConjunctionCondition(String condition, String addedCondition) {
        StringBuilder exp = new StringBuilder();
        exp.append('(').append(condition).append(')').append(" && ").append('(').append(addedCondition).append(')');
        return exp.toString();
    }

    public static Set<String> extractVars(String script) {
        final Set<String> names = new HashSet<>();
        class Visitor implements NodeVisitor {
            @Override
            public boolean visit(AstNode node) {
                if (node instanceof PropertyGet) {
                    names.add(node.toSource());
                    return false;
                }
                if (node instanceof Name) {
                    names.add(node.toSource());
                }
                return true;
            }
        }
        AstNode node = null;
        try {
            node = new Parser().parse(script, null, 1);
        } catch (EvaluatorException e) {
            throw new ScriptParserException(script, e);
        }
        node.visit(new Visitor());
        return names;
    }

    public static Map<String, Set<String>> extractPropertiesOf(String script, final Collection<String> vars) {
        final Map<String, Set<String>> result = new LinkedHashMap<>();
        final Set<String> names = extractVars(script);
        for (String name : names) {
            for (String var : vars) {
                String prefix = var + ".";
                int idx = name.indexOf(prefix);
                if (idx >= 0) {
                    Set<String> properties = result.get(var);
                    if (properties == null) {
                        properties = new LinkedHashSet<>();
                        result.put(var, properties);
                    }
                    String afterGet = name.substring(idx + prefix.length());
                    int endIdx = afterGet.indexOf(".");
                    if (endIdx >= 0) {
                        properties.add(afterGet.substring(0, endIdx));
                    } else {
                        properties.add(afterGet);
                    }
                }
            }
        }
        return result;
    }


    public static String addContextFor(String script, final String context, Predicate<String> predicate) {
        class Visitor implements NodeVisitor {
            @Override
            public boolean visit(AstNode node) {
                if (node instanceof PropertyGet) {
                    PropertyGet left = (PropertyGet) node;
                    while (left.getLeft() instanceof PropertyGet) {
                        left = (PropertyGet) left.getLeft();
                    }
                    if (!predicate.test(left.getTarget().getString())) return false;
                    left.setLeft(new PropertyGet(new Name(1, context), (Name) left.getLeft()));
                    return false;
                }

                if (node instanceof Name) {
                    if (predicate.test(node.getString())) {
                        ((Name) node).setIdentifier(context + '.' + ((Name) node).getIdentifier());
                    }
                    return false;
                }
                return true;
            }
        }
        AstNode node = new Parser().parse(script, null, 1);
        node.visit(new Visitor());
        return node.toSource();
    }

    public static String addContextFor(String script, final String context, final Collection<String> vars) {
        return addContextFor(script, context, vars::contains);
    }

    public static String addContextForAll(String script, final String context) {
        return addContextFor(script, context, (s) -> true);
    }

    //    'id, gender[0].id' to 'id, gender'
    public static String simplifyArrayLinks(String src) {
        StringBuilder sb = new StringBuilder();
        Set<String> buffer = new HashSet<>();
        boolean begin = true;
        for (String tmp : src.split("\\,")) {
            String s[] = tmp.split("\\[");
            if (s.length > 1) tmp = s[0];
            else {
                s = tmp.split("\\*");
                if (s.length > 1) tmp = s[0];
            }
            if (!buffer.contains(tmp)) {
                if (!begin) sb.append(',');
                begin = false;
                buffer.add(tmp);
                sb.append(tmp);
            }
        }
        return sb.toString();
    }


    @SuppressWarnings("unchecked")
    public static <T> T eval(String script, DataSet dataSet) throws ScriptException {
        ScriptEngine scriptEngine = getScriptEngine();
        Bindings bindings = scriptEngine.createBindings();
        for (String key : dataSet.keySet()) {
            Object var = dataSet.get(key);
            if (var instanceof Collection) {
                bindings.put(key, ((Collection) var).toArray());
            } else {
                bindings.put(key, var);
            }
        }
        addJsLibs(script, bindings);
        return (T) scriptEngine.eval(script, bindings);
    }

    public static boolean evalForBoolean(String script, DataSet dataSet) {
        try {
            Boolean eval = eval(script, dataSet);
            if (eval == null) return false;
            return eval;
        } catch (ScriptException e) {
            return false;
        }
    }


    public static String ifNotUndefined(String exp, String... fields) {
        StringBuilder res = new StringBuilder();
        for (String field : retrieve(fields)) {
            res.append("(typeof ");
            res.append(field);
            res.append(" === 'undefined')");
            res.append(" || ");
        }
        res.append('(');
        res.append(exp);
        res.append(')');
        return res.toString();
    }

    private static List<String> retrieve(String[] fields) {
        List<String> res = new ArrayList<>();
        for (String field : fields) {
            res.addAll(retrieve(field));
        }
        return res;
    }


    private static List<String> retrieve(String field) {
        List<String> res = new ArrayList<>();
        String[] tmp = field.split("\\.");
        for (int i = 0; i < tmp.length; i++) {
            res.add(toString(Arrays.copyOfRange(tmp, 0, i + 1)));
        }
        return res;
    }

    private static String toString(String[] array) {
        StringBuilder res = new StringBuilder();
        boolean begin = true;
        for (int i = 0; i < array.length; i++) {
            if (!begin) res.append('.');
            res.append(array[i]);
            begin = false;
        }
        return res.toString();
    }

    public static ScriptEngine getScriptEngine() {
        if (scriptEngine == null) {
            createScriptEngine();
        }
        return scriptEngine;
    }

    private static synchronized void createScriptEngine() {
        if (scriptEngine == null) {
            scriptEngine = engineMgr.getEngineByName("JavaScript");
//            URL underscoreIo = this.getClass().getClassLoader().getResource("META-INF/resources/lib/underscore.js");
//            URL numeralIo = this.getClass().getClassLoader().getResource("META-INF/resources/lib/numeral.min-1.5.3.js");
//            try {
//                scriptEngine.eval(IOUtils.toString(underscoreIo, "UTF-8"));
//                underscoreJs = scriptEngine.eval("_");
//                scriptEngine.eval(IOUtils.toString(numeralIo, "UTF-8"));
//                numeralJs = scriptEngine.eval("numeral");
//            } catch (IOException e) {
//                throw new N2oException(e);
//            }
        }
    }

    private static void addJsLibs(String script, Bindings bindings) throws ScriptException {
        bindings.put("_", underscoreJs);
        bindings.put("numeral", numeralJs);
        if (isNeedMoment(script)) {
            URL momentUrl = ScriptProcessor.class.getClassLoader().getResource("META-INF/resources/lib/moment.min.js");
            URL momentTzUrl = ScriptProcessor.class.getClassLoader().getResource("META-INF/resources/lib/moment-timezone-with-data.min.js");
            URL globalDateFuncUrl = ScriptProcessor.class.getClassLoader().getResource("META-INF/resources/lib/globalDateFunc.js");
            try {
                scriptEngine.eval(IOUtils.toString(momentUrl, "UTF-8"), bindings);
                scriptEngine.eval(IOUtils.toString(globalDateFuncUrl, "UTF-8"), bindings);
                scriptEngine.eval(IOUtils.toString(momentTzUrl, "UTF-8"), bindings);
            } catch (IOException e) {
                throw new N2oException(e);
            }
        }
    }


    private String getString(Object value) {
        if (value instanceof String)
            return "'" + value.toString() + "'";
        else if (value instanceof Date)
            return "'" + new SimpleDateFormat(getDateFormat()).format((Date) value) + "'";
        return value.toString();
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }

    protected String getDateFormat() {
        if (dateFormat == null)
            dateFormat = StaticProperties.getProperty("n2o.format.date");
        return dateFormat;
    }

    private static boolean isNeedMoment(String script) {
        return momentFuncs.stream().anyMatch(f -> script.contains(f));
    }


}