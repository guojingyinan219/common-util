package com.touniba.common.util;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;

/**
 * Created by Magoo on 2016/9/8.
 */
public class ScriptUtils {

    /**
     * build a nashorn engine.
     *
     * @return
     */
    public static ScriptEngine buildNashornEngine() {
        return buildScriptEngine("nashorn");
    }

    /**
     * Build a script engine by name.
     *
     * @param engineName
     * @return
     */
    public static ScriptEngine buildScriptEngine(String engineName) {
        ScriptEngineManager scriptEngineManager =
                new ScriptEngineManager();
        return scriptEngineManager.getEngineByName(engineName);
    }
}
