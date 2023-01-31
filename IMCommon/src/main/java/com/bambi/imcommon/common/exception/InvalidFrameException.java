package com.bambi.imcommon.common.exception;

/**
 * 描述：
 *      口令出错等非法访问时触发
 * <pre>
 * HISTORY
 * ****************************************************************************
 *  ID          DATE              PERSON          REASON
 *  001699   2022/12/11 23:20    WangJinhan        Create
 * ****************************************************************************
 * </pre>
 *
 * @author WangJinhan
 * @since 1.0
 */
public class InvalidFrameException extends Exception{
    public InvalidFrameException(String message) {
        super(message);
    }
}
