package com.ruoyi.web.controller.flowable;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.flowable.domain.FlowTask;
import com.ruoyi.flowable.domain.dto.FlowTaskAssignRequest;
import com.ruoyi.flowable.domain.dto.FlowTaskCompleteRequest;
import com.ruoyi.flowable.service.IFlowTaskService;

/**
 * Flowable 任务接口
 */
@RestController
@RequestMapping("/flowable/task")
public class FlowTaskController extends BaseController
{
    @Autowired
    private IFlowTaskService flowTaskService;

    @PreAuthorize("@ss.hasPermi('flowable:task:todo:list')")
    @GetMapping("/todoList")
    public TableDataInfo todoList(FlowTask query)
    {
        startPage();
        List<FlowTask> list = flowTaskService.selectTodoTaskList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('flowable:task:done:list')")
    @GetMapping("/doneList")
    public TableDataInfo doneList(FlowTask query)
    {
        startPage();
        List<FlowTask> list = flowTaskService.selectDoneTaskList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('flowable:task:query')")
    @GetMapping("/{taskId}")
    public AjaxResult getInfo(@PathVariable String taskId)
    {
        return AjaxResult.success(flowTaskService.selectTaskById(taskId));
    }

    @PreAuthorize("@ss.hasPermi('flowable:task:claim')")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @PostMapping("/{taskId}/claim")
    public AjaxResult claim(@PathVariable String taskId)
    {
        flowTaskService.claimTask(taskId, getUsername());
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('flowable:task:unclaim')")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @PostMapping("/{taskId}/unclaim")
    public AjaxResult unclaim(@PathVariable String taskId)
    {
        flowTaskService.unclaimTask(taskId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('flowable:task:delegate')")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @PostMapping("/delegate")
    public AjaxResult delegate(@Validated @RequestBody FlowTaskAssignRequest request)
    {
        request.setOperator(getUsername());
        flowTaskService.delegateTask(request);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('flowable:task:transfer')")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @PostMapping("/transfer")
    public AjaxResult transfer(@Validated @RequestBody FlowTaskAssignRequest request)
    {
        request.setOperator(getUsername());
        flowTaskService.transferTask(request);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('flowable:task:complete')")
    @Log(title = "任务管理", businessType = BusinessType.UPDATE)
    @PostMapping("/complete")
    public AjaxResult complete(@Validated @RequestBody FlowTaskCompleteRequest request)
    {
        request.setSubmitBy(getUsername());
        flowTaskService.completeTask(request);
        return AjaxResult.success();
    }
}
