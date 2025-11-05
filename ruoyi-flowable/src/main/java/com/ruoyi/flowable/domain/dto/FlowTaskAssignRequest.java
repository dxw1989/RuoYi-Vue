package com.ruoyi.flowable.domain.dto;

import javax.validation.constraints.NotBlank;

/**
 * 任务指派请求
 */
public class FlowTaskAssignRequest
{
    /** 任务ID */
    @NotBlank(message = "任务ID不能为空")
    private String taskId;

    /** 用户ID */
    @NotBlank(message = "用户不能为空")
    private String userId;

    /** 操作者 */
    private String operator;

    /** 意见 */
    private String comment;

    public String getTaskId()
    {
        return taskId;
    }

    public void setTaskId(String taskId)
    {
        this.taskId = taskId;
    }

    public String getUserId()
    {
        return userId;
    }

    public void setUserId(String userId)
    {
        this.userId = userId;
    }

    public String getOperator()
    {
        return operator;
    }

    public void setOperator(String operator)
    {
        this.operator = operator;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        this.comment = comment;
    }
}
