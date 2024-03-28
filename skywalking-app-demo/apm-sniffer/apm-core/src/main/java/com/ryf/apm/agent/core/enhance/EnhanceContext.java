package com.ryf.apm.agent.core.enhance;

/**
 * @author ryf
 * @version 1.0
 * @project bytebuddy-study
 * @description 增强类过程的上下文
 * @date 2024/3/28
 */
public class EnhanceContext {
    /**
     * 是否被增强
     */
    private boolean isEnhanced = false;
    /**
     * 是否被扩展
     */
    private boolean objectExtended = false;

    public boolean isEnhanced() {
        return isEnhanced;
    }

    public void initializationStageCompleted() {
        this.isEnhanced = true;
    }

    public boolean isObjectExtended() {
        return objectExtended;
    }

    public void extendObjectCompleted() {
        this.objectExtended = true;
    }
}
