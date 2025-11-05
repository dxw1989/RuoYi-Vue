<template>
  <div class="app-container">
    <el-tabs v-model="activeTab" @tab-click="handleTabChange">
      <el-tab-pane label="待办任务" name="todo" />
      <el-tab-pane label="已办任务" name="done" />
    </el-tabs>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="任务名称" prop="taskName">
        <el-input v-model="queryParams.taskName" placeholder="请输入任务名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="流程名称" prop="processDefinitionName">
        <el-input v-model="queryParams.processDefinitionName" placeholder="请输入流程名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="业务主键" prop="businessKey">
        <el-input v-model="queryParams.businessKey" placeholder="请输入业务主键" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="创建时间">
        <el-date-picker v-model="dateRange" type="daterange" range-separator="-" start-placeholder="开始日期" end-placeholder="结束日期" value-format="yyyy-MM-dd" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8" v-if="activeTab === 'todo'">
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-circle-check" size="mini" :disabled="single" @click="handleClaim" v-hasPermi="['flowable:task:claim']">签收</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-refresh-left" size="mini" :disabled="single" @click="handleUnclaim" v-hasPermi="['flowable:task:unclaim']">退签</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-share" size="mini" :disabled="single" @click="openDelegate" v-hasPermi="['flowable:task:delegate']">委派</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-switch-button" size="mini" :disabled="single" @click="openTransfer" v-hasPermi="['flowable:task:transfer']">转办</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-finished" size="mini" :disabled="single" @click="openComplete" v-hasPermi="['flowable:task:complete']">办理完成</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-row :gutter="10" class="mb8" v-else>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="taskList" @selection-change="handleSelectionChange">
      <el-table-column v-if="activeTab === 'todo'" type="selection" width="55" align="center" />
      <el-table-column label="任务名称" prop="taskName" align="center" min-width="160" show-overflow-tooltip />
      <el-table-column label="流程名称" prop="processDefinitionName" align="center" min-width="180" show-overflow-tooltip />
      <el-table-column label="流程标识" prop="processDefinitionKey" align="center" min-width="140" show-overflow-tooltip />
      <el-table-column label="业务主键" prop="businessKey" align="center" min-width="140" show-overflow-tooltip />
      <el-table-column label="办理人" prop="assignee" align="center" width="120" />
      <el-table-column label="候选人" align="center" min-width="160" show-overflow-tooltip>
        <template slot-scope="scope">
          <span>{{ renderCandidates(scope.row) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" prop="createTime" align="center" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column v-if="activeTab === 'done'" label="完成时间" prop="endTime" align="center" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.endTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['flowable:task:query']">详情</el-button>
          <el-button v-if="activeTab === 'todo'" size="mini" type="text" icon="el-icon-circle-check" @click="handleClaim(scope.row)" v-hasPermi="['flowable:task:claim']">签收</el-button>
          <el-button v-if="activeTab === 'todo'" size="mini" type="text" icon="el-icon-finished" @click="openComplete(scope.row)" v-hasPermi="['flowable:task:complete']">完成</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="任务详情" :visible.sync="detailOpen" width="720px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="任务ID">{{ detail.taskId }}</el-descriptions-item>
        <el-descriptions-item label="任务节点">{{ detail.taskKey }}</el-descriptions-item>
        <el-descriptions-item label="任务名称">{{ detail.taskName }}</el-descriptions-item>
        <el-descriptions-item label="流程名称">{{ detail.processDefinitionName }}</el-descriptions-item>
        <el-descriptions-item label="流程标识">{{ detail.processDefinitionKey }}</el-descriptions-item>
        <el-descriptions-item label="业务主键">{{ detail.businessKey }}</el-descriptions-item>
        <el-descriptions-item label="办理人">{{ detail.assignee }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="完成时间">{{ parseTime(detail.endTime) }}</el-descriptions-item>
        <el-descriptions-item label="当前状态">{{ detail.completed ? '已完成' : '待办理' }}</el-descriptions-item>
      </el-descriptions>
      <div class="mt20">
        <el-card shadow="never">
          <div slot="header" class="clearfix">
            <span>表单数据</span>
          </div>
          <pre class="json-preview">{{ formatJson(detail.formData && detail.formData.formValues) }}</pre>
        </el-card>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="任务委派" :visible.sync="delegateOpen" width="500px" append-to-body>
      <el-form ref="delegateFormRef" :model="delegateForm" :rules="assignRules" label-width="120px">
        <el-form-item label="目标用户" prop="userId">
          <el-input v-model="delegateForm.userId" placeholder="请输入用户账号" />
        </el-form-item>
        <el-form-item label="意见">
          <el-input type="textarea" v-model="delegateForm.comment" placeholder="请输入意见" :rows="4" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitDelegate">确 定</el-button>
        <el-button @click="delegateOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="任务转办" :visible.sync="transferOpen" width="500px" append-to-body>
      <el-form ref="transferFormRef" :model="transferForm" :rules="assignRules" label-width="120px">
        <el-form-item label="目标用户" prop="userId">
          <el-input v-model="transferForm.userId" placeholder="请输入用户账号" />
        </el-form-item>
        <el-form-item label="意见">
          <el-input type="textarea" v-model="transferForm.comment" placeholder="请输入意见" :rows="4" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitTransfer">确 定</el-button>
        <el-button @click="transferOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="办理任务" :visible.sync="completeOpen" width="720px" append-to-body>
      <el-form ref="completeFormRef" :model="completeForm" :rules="completeRules" label-width="120px">
        <el-form-item label="任务意见">
          <el-input type="textarea" v-model="completeForm.comment" placeholder="请输入办理意见" :rows="4" />
        </el-form-item>
        <el-form-item label="表单标识">
          <el-input v-model="completeForm.formKey" placeholder="表单标识，可选" />
        </el-form-item>
        <el-form-item label="表单ID">
          <el-input v-model="completeForm.formId" placeholder="表单ID，可选" />
        </el-form-item>
        <el-form-item label="表单值(JSON)" prop="formValues">
          <el-input type="textarea" v-model="completeForm.formValues" placeholder="请输入表单值 JSON" :rows="5" />
        </el-form-item>
        <el-form-item label="全局变量(JSON)" prop="variables">
          <el-input type="textarea" v-model="completeForm.variables" placeholder="请输入流程变量 JSON" :rows="5" />
        </el-form-item>
        <el-form-item label="本地变量(JSON)" prop="localVariables">
          <el-input type="textarea" v-model="completeForm.localVariables" placeholder="请输入本地变量 JSON" :rows="5" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitComplete">提 交</el-button>
        <el-button @click="completeOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTodoTask, listDoneTask, getTask, claimTask, unclaimTask, delegateTask, transferTask, completeTask } from '@/api/flowable/task'

export default {
  name: 'FlowableTask',
  data() {
    return {
      activeTab: 'todo',
      loading: false,
      showSearch: true,
      taskList: [],
      total: 0,
      ids: [],
      single: true,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        taskName: undefined,
        processDefinitionName: undefined,
        processDefinitionKey: undefined,
        businessKey: undefined
      },
      dateRange: [],
      detailOpen: false,
      detail: {},
      delegateOpen: false,
      delegateForm: {
        taskId: undefined,
        userId: undefined,
        comment: undefined
      },
      transferOpen: false,
      transferForm: {
        taskId: undefined,
        userId: undefined,
        comment: undefined
      },
      completeOpen: false,
      completeForm: {
        taskId: undefined,
        comment: undefined,
        businessKey: undefined,
        formId: undefined,
        formKey: undefined,
        formValues: '',
        variables: '',
        localVariables: ''
      },
      assignRules: {
        userId: [
          { required: true, message: '请选择目标用户', trigger: 'blur' }
        ]
      },
      completeRules: {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      const query = Object.assign({}, this.queryParams)
      if (this.dateRange && this.dateRange.length === 2) {
        query.beginTime = this.dateRange[0]
        query.endTime = this.dateRange[1]
      } else {
        query.beginTime = undefined
        query.endTime = undefined
      }
      const loader = this.activeTab === 'todo' ? listTodoTask : listDoneTask
      loader(query).then(response => {
        this.taskList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    handleTabChange() {
      this.resetSelection()
      this.handleQuery()
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.dateRange = []
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        taskName: undefined,
        processDefinitionName: undefined,
        processDefinitionKey: undefined,
        businessKey: undefined
      }
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.taskId)
      this.single = selection.length !== 1
    },
    resetSelection() {
      this.ids = []
      this.single = true
    },
    getSelectedTaskId(row) {
      return row?.taskId || this.ids[0]
    },
    handleView(row) {
      const taskId = this.getSelectedTaskId(row)
      if (!taskId) {
        return
      }
      getTask(taskId).then(response => {
        const data = response.data || {}
        if (data.formData && data.formData.formValues && typeof data.formData.formValues !== 'string') {
          data.formData.formValues = JSON.stringify(data.formData.formValues, null, 2)
        }
        this.detail = data
        this.detailOpen = true
      })
    },
    handleClaim(row) {
      const taskId = this.getSelectedTaskId(row)
      if (!taskId) {
        return
      }
      this.$modal.confirm('确认签收该任务吗？').then(() => claimTask(taskId)).then(() => {
        this.$modal.msgSuccess('签收成功')
        this.getList()
      }).catch(() => {})
    },
    handleUnclaim(row) {
      const taskId = this.getSelectedTaskId(row)
      if (!taskId) {
        return
      }
      this.$modal.confirm('确认退签该任务吗？').then(() => unclaimTask(taskId)).then(() => {
        this.$modal.msgSuccess('退签成功')
        this.getList()
      }).catch(() => {})
    },
    openDelegate(row) {
      const taskId = this.getSelectedTaskId(row)
      if (!taskId) {
        return
      }
      this.delegateForm = {
        taskId,
        userId: undefined,
        comment: undefined
      }
      this.delegateOpen = true
    },
    openTransfer(row) {
      const taskId = this.getSelectedTaskId(row)
      if (!taskId) {
        return
      }
      this.transferForm = {
        taskId,
        userId: undefined,
        comment: undefined
      }
      this.transferOpen = true
    },
    openComplete(row) {
      const taskId = this.getSelectedTaskId(row)
      if (!taskId) {
        return
      }
      const populateForm = (data = {}) => {
        const formInfo = data.formInfo || {}
        this.completeForm = {
          taskId,
          comment: undefined,
          businessKey: data.businessKey ?? row?.businessKey,
          formId: formInfo.formId ?? row?.formInfo?.formId,
          formKey: formInfo.formKey ?? row?.formInfo?.formKey,
          formValues: '',
          variables: '',
          localVariables: ''
        }
        this.completeOpen = true
      }
      if (row && row.formInfo) {
        populateForm(row)
      } else {
        getTask(taskId).then(response => {
          populateForm(response.data)
        }).catch(() => {
          populateForm()
        })
      }
    },
    submitDelegate() {
      this.$refs.delegateFormRef.validate(valid => {
        if (!valid) {
          return
        }
        delegateTask(this.delegateForm).then(() => {
          this.$modal.msgSuccess('委派成功')
          this.delegateOpen = false
          this.getList()
        })
      })
    },
    submitTransfer() {
      this.$refs.transferFormRef.validate(valid => {
        if (!valid) {
          return
        }
        transferTask(this.transferForm).then(() => {
          this.$modal.msgSuccess('转办成功')
          this.transferOpen = false
          this.getList()
        })
      })
    },
    submitComplete() {
      const payload = {
        taskId: this.completeForm.taskId,
        comment: this.completeForm.comment,
        businessKey: this.completeForm.businessKey,
        formId: this.completeForm.formId,
        formKey: this.completeForm.formKey,
        formValues: this.parseJsonSafely(this.completeForm.formValues),
        variables: this.parseJsonSafely(this.completeForm.variables),
        localVariables: this.parseJsonSafely(this.completeForm.localVariables)
      }
      completeTask(payload).then(() => {
        this.$modal.msgSuccess('任务办理完成')
        this.completeOpen = false
        this.getList()
      })
    },
    renderCandidates(row) {
      const users = row.candidateUsers ? Array.from(row.candidateUsers) : []
      const groups = row.candidateGroups ? Array.from(row.candidateGroups) : []
      return [...users, ...groups].join('、')
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
