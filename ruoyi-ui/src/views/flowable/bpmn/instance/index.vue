<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="实例标识" prop="instanceId">
        <el-input v-model="queryParams.instanceId" placeholder="请输入实例ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="流程标识" prop="definitionKey">
        <el-input v-model="queryParams.definitionKey" placeholder="请输入流程标识" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="流程名称" prop="definitionName">
        <el-input v-model="queryParams.definitionName" placeholder="请输入流程名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="业务主键" prop="businessKey">
        <el-input v-model="queryParams.businessKey" placeholder="请输入业务主键" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="finished">
        <el-select v-model="queryParams.finished" placeholder="请选择" clearable>
          <el-option :value="false" label="运行中" />
          <el-option :value="true" label="已完成" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-video-play" size="mini" @click="openStart" v-hasPermi="['flowable:instance:start']">发起流程</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-video-pause" size="mini" :disabled="single" @click="handleSuspend" v-hasPermi="['flowable:instance:suspend']">挂起</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-video-play" size="mini" :disabled="single" @click="handleActivate" v-hasPermi="['flowable:instance:activate']">激活</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleRemove" v-hasPermi="['flowable:instance:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="instanceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="实例ID" prop="instanceId" align="center" min-width="200" show-overflow-tooltip />
      <el-table-column label="流程名称" prop="definitionName" align="center" min-width="180" show-overflow-tooltip />
      <el-table-column label="流程标识" prop="definitionKey" align="center" min-width="140" show-overflow-tooltip />
      <el-table-column label="业务主键" prop="businessKey" align="center" min-width="140" show-overflow-tooltip />
      <el-table-column label="发起人" prop="startUserName" align="center" width="120" />
      <el-table-column label="开始时间" prop="startTime" align="center" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.startTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="结束时间" prop="endTime" align="center" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" prop="finished" align="center" width="100">
        <template slot-scope="scope">
          <dict-tag :options="finishOptions" :value="scope.row.finished" />
        </template>
      </el-table-column>
      <el-table-column label="挂起" prop="suspended" align="center" width="90">
        <template slot-scope="scope">
          <dict-tag :options="suspendOptions" :value="scope.row.suspended" />
        </template>
      </el-table-column>
      <el-table-column label="当前节点" prop="currentTaskNames" align="center" min-width="180" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ (scope.row.currentTaskNames || []).join('、') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['flowable:instance:query']">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-video-pause" v-if="!scope.row.suspended" @click="handleSuspend(scope.row)" v-hasPermi="['flowable:instance:suspend']">挂起</el-button>
          <el-button size="mini" type="text" icon="el-icon-video-play" v-if="scope.row.suspended" @click="handleActivate(scope.row)" v-hasPermi="['flowable:instance:activate']">激活</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleRemove(scope.row)" v-hasPermi="['flowable:instance:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="流程实例详情" :visible.sync="detailOpen" width="720px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="实例ID">{{ detail.instanceId }}</el-descriptions-item>
        <el-descriptions-item label="业务主键">{{ detail.businessKey }}</el-descriptions-item>
        <el-descriptions-item label="流程名称">{{ detail.definitionName }}</el-descriptions-item>
        <el-descriptions-item label="流程标识">{{ detail.definitionKey }}</el-descriptions-item>
        <el-descriptions-item label="发起人">{{ detail.startUserName }}</el-descriptions-item>
        <el-descriptions-item label="发起时间">{{ parseTime(detail.startTime) }}</el-descriptions-item>
        <el-descriptions-item label="结束时间">{{ parseTime(detail.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="当前节点">{{ (detail.currentTaskNames || []).join('、') }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="finishOptions" :value="detail.finished" />
        </el-descriptions-item>
        <el-descriptions-item label="挂起">
          <dict-tag :options="suspendOptions" :value="detail.suspended" />
        </el-descriptions-item>
      </el-descriptions>
      <div class="mt20">
        <el-card shadow="never">
          <div slot="header" class="clearfix">
            <span>表单值</span>
          </div>
          <pre class="json-preview">{{ formatJson(detail.formValues) }}</pre>
        </el-card>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="发起流程" :visible.sync="startOpen" width="720px" append-to-body>
      <el-form ref="startFormRef" :model="startForm" :rules="startRules" label-width="120px">
        <el-form-item label="流程定义" prop="definitionKey">
          <el-select v-model="startForm.definitionKey" placeholder="请选择流程定义" filterable>
            <el-option v-for="item in definitionOptions" :key="item.definitionKey" :label="`${item.definitionName} (V${item.version})`" :value="item.definitionKey" />
          </el-select>
        </el-form-item>
        <el-form-item label="业务主键" prop="businessKey">
          <el-input v-model="startForm.businessKey" placeholder="请输入业务主键" maxlength="64" />
        </el-form-item>
        <el-form-item label="关联表单">
          <el-input v-model="startForm.formKey" placeholder="表单标识，可选" maxlength="64" />
        </el-form-item>
        <el-form-item label="表单ID">
          <el-input v-model="startForm.formId" placeholder="表单ID，可选" maxlength="32" />
        </el-form-item>
        <el-form-item label="表单值(JSON)" prop="formValues">
          <el-input type="textarea" v-model="startForm.formValues" placeholder="请输入表单值 JSON" :rows="6" />
        </el-form-item>
        <el-form-item label="流程变量(JSON)" prop="variables">
          <el-input type="textarea" v-model="startForm.variables" placeholder="请输入流程变量 JSON" :rows="6" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitStart">发 起</el-button>
        <el-button @click="startOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listInstance, getInstance, startInstance, removeInstance, suspendInstance, activateInstance } from '@/api/flowable/instance'
import { listDefinition } from '@/api/flowable/definition'

export default {
  name: 'FlowableInstance',
  data() {
    return {
      loading: false,
      showSearch: true,
      instanceList: [],
      total: 0,
      ids: [],
      single: true,
      multiple: true,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        instanceId: undefined,
        definitionKey: undefined,
        definitionName: undefined,
        businessKey: undefined,
        finished: undefined
      },
      finishOptions: [
        { label: '运行中', value: false },
        { label: '已完成', value: true }
      ],
      suspendOptions: [
        { label: '否', value: false },
        { label: '是', value: true }
      ],
      detailOpen: false,
      detail: {},
      startOpen: false,
      startForm: {
        definitionKey: undefined,
        businessKey: undefined,
        formId: undefined,
        formKey: undefined,
        formValues: '',
        variables: ''
      },
      startRules: {
        definitionKey: [
          { required: true, message: '流程定义不能为空', trigger: 'change' }
        ]
      },
      definitionOptions: []
    }
  },
  created() {
    this.getList()
    this.loadDefinitions()
  },
  methods: {
    getList() {
      this.loading = true
      listInstance(this.queryParams).then(response => {
        this.instanceList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    loadDefinitions() {
      listDefinition({ pageNum: 1, pageSize: 100 }).then(response => {
        this.definitionOptions = response.rows || []
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        instanceId: undefined,
        definitionKey: undefined,
        definitionName: undefined,
        businessKey: undefined,
        finished: undefined
      }
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.instanceId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleView(row) {
      const instanceId = row?.instanceId
      if (!instanceId) {
        return
      }
      getInstance(instanceId).then(response => {
        const data = response.data || {}
        if (data.formValues && typeof data.formValues !== 'string') {
          data.formValues = JSON.stringify(data.formValues, null, 2)
        }
        this.detail = data
        this.detailOpen = true
      })
    },
    openStart() {
      this.startForm = {
        definitionKey: undefined,
        businessKey: undefined,
        formId: undefined,
        formKey: undefined,
        formValues: '',
        variables: ''
      }
      this.startOpen = true
    },
    submitStart() {
      this.$refs.startFormRef.validate(valid => {
        if (!valid) {
          return
        }
        let formValues = this.parseJsonSafely(this.startForm.formValues)
        let variables = this.parseJsonSafely(this.startForm.variables)
        const payload = {
          definitionKey: this.startForm.definitionKey,
          businessKey: this.startForm.businessKey,
          formId: this.startForm.formId,
          formKey: this.startForm.formKey,
          formValues: formValues,
          variables: variables
        }
        startInstance(payload).then(() => {
          this.$modal.msgSuccess('流程启动成功')
          this.startOpen = false
          this.getList()
        })
      })
    },
    handleSuspend(row) {
      const instanceId = row?.instanceId || this.ids[0]
      if (!instanceId) {
        return
      }
      this.$modal.confirm('确认挂起该流程实例吗？').then(() => suspendInstance(instanceId)).then(() => {
        this.$modal.msgSuccess('挂起成功')
        this.getList()
      }).catch(() => {})
    },
    handleActivate(row) {
      const instanceId = row?.instanceId || this.ids[0]
      if (!instanceId) {
        return
      }
      this.$modal.confirm('确认激活该流程实例吗？').then(() => activateInstance(instanceId)).then(() => {
        this.$modal.msgSuccess('激活成功')
        this.getList()
      }).catch(() => {})
    },
    handleRemove(row) {
      const instanceIds = row?.instanceId ? [row.instanceId] : this.ids
      if (!instanceIds.length) {
        this.$message.warning('请选择需要删除的实例')
        return
      }
      this.$prompt('请输入删除原因，可留空', '删除流程实例', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        inputPlaceholder: '删除原因',
        inputValue: ''
      }).then(({ value }) => {
        return Promise.all(instanceIds.map(id => removeInstance(id, value ? { reason: value } : {})))
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    parseJsonSafely(text) {
      if (!text) {
        return undefined
      }
      try {
        return JSON.parse(text)
      } catch (e) {
        this.$message.warning('JSON 解析失败，已忽略无效内容')
        return undefined
      }
    },
    formatJson(value) {
      if (!value) {
        return ''
      }
      if (typeof value === 'string') {
        try {
          return JSON.stringify(JSON.parse(value), null, 2)
        } catch (error) {
          return value
        }
      }
      try {
        return JSON.stringify(value, null, 2)
      } catch (error) {
        return ''
      }
    }
  }
}
</script>

<style scoped>
.json-preview {
  white-space: pre-wrap;
  word-break: break-all;
  margin: 0;
}
</style>
