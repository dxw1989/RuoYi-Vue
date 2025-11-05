package com.ruoyi.flowable.service;

import java.util.List;
import com.ruoyi.flowable.domain.FlowTask;
import com.ruoyi.flowable.domain.dto.FlowTaskAssignRequest;
import com.ruoyi.flowable.domain.dto.FlowTaskCompleteRequest;

/**
 * Flowable 任务服务
 */
public interface IFlowTaskService
{
    /**
     * 待办任务列表
     */
    List<FlowTask> selectTodoTaskList(FlowTask query);

    /**
     * 已办任务列表
     */
    List<FlowTask> selectDoneTaskList(FlowTask query);

    /**
     * 查询任务详情
     */
    FlowTask selectTaskById(String taskId);

    /**
     * 签收任务
     */
    void claimTask(String taskId, String userId);

    /**
     * 释放任务
     */
    void unclaimTask(String taskId);

    /**
     * 委派任务
     */
    void delegateTask(FlowTaskAssignRequest request);

    /**
     * 转办任务
     */
    void transferTask(FlowTaskAssignRequest request);

    /**
     * 完成任务
     */
    void completeTask(FlowTaskCompleteRequest request);
}
