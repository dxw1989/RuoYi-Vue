package com.ruoyi.web.controller.flowable;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.flowable.domain.FlowProcessInstance;
import com.ruoyi.flowable.domain.dto.FlowProcessStartRequest;
import com.ruoyi.flowable.service.IFlowProcessInstanceService;

/**
 * Flowable 流程实例接口
 */
@RestController
@RequestMapping("/flowable/instance")
public class FlowInstanceController extends BaseController
{
    @Autowired
    private IFlowProcessInstanceService flowProcessInstanceService;

    @PreAuthorize("@ss.hasPermi('flowable:instance:list')")
    @GetMapping("/list")
    public TableDataInfo list(FlowProcessInstance query)
    {
        startPage();
        List<FlowProcessInstance> list = flowProcessInstanceService.selectProcessInstanceList(query);
        return getDataTable(list);
    }

    @PreAuthorize("@ss.hasPermi('flowable:instance:query')")
    @GetMapping("/{instanceId}")
    public AjaxResult getInfo(@PathVariable String instanceId)
    {
        return AjaxResult.success(flowProcessInstanceService.selectProcessInstanceById(instanceId));
    }

    @PreAuthorize("@ss.hasPermi('flowable:instance:start')")
    @Log(title = "流程实例", businessType = BusinessType.INSERT)
    @PostMapping("/start")
    public AjaxResult start(@Validated @RequestBody FlowProcessStartRequest request)
    {
        request.setStartUserId(getUsername());
        FlowProcessInstance instance = flowProcessInstanceService.startProcessInstance(request);
        return AjaxResult.success(instance);
    }

    @PreAuthorize("@ss.hasPermi('flowable:instance:remove')")
    @Log(title = "流程实例", businessType = BusinessType.DELETE)
    @DeleteMapping("/{instanceId}")
    public AjaxResult remove(@PathVariable String instanceId,
            @RequestParam(value = "reason", required = false) String reason)
    {
        flowProcessInstanceService.deleteProcessInstance(instanceId, reason);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('flowable:instance:suspend')")
    @Log(title = "流程实例", businessType = BusinessType.UPDATE)
    @PutMapping("/{instanceId}/suspend")
    public AjaxResult suspend(@PathVariable String instanceId)
    {
        flowProcessInstanceService.suspendProcessInstance(instanceId);
        return AjaxResult.success();
    }

    @PreAuthorize("@ss.hasPermi('flowable:instance:activate')")
    @Log(title = "流程实例", businessType = BusinessType.UPDATE)
    @PutMapping("/{instanceId}/activate")
    public AjaxResult activate(@PathVariable String instanceId)
    {
        flowProcessInstanceService.activateProcessInstance(instanceId);
        return AjaxResult.success();
    }
}
