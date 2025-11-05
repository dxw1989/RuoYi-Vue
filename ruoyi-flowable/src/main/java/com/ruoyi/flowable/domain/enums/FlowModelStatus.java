package com.ruoyi.flowable.domain.enums;

/**
 * 流程模型发布状态
 */
public enum FlowModelStatus
{
    /** 草稿 */
    DRAFT(0, "草稿"),

    /** 待发布 */
    PENDING(1, "待发布"),

    /** 已发布 */
    PUBLISHED(2, "已发布");

    private final int code;
    private final String info;

    FlowModelStatus(int code, String info)
    {
        this.code = code;
        this.info = info;
    }

    public int getCode()
    {
        return code;
    }

    public String getInfo()
    {
        return info;
    }

    public static FlowModelStatus valueOfCode(Integer code)
    {
        if (code == null)
        {
            return null;
        }
        for (FlowModelStatus status : values())
        {
            if (status.code == code)
            {
                return status;
            }
        }
        throw new IllegalArgumentException("Unsupported FlowModelStatus code: " + code);
    }
}
