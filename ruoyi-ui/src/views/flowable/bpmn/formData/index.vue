<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="表单标识" prop="formKey">
        <el-input v-model="queryParams.formKey" placeholder="请输入表单标识" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="业务主键" prop="businessKey">
        <el-input v-model="queryParams.businessKey" placeholder="请输入业务主键" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="流程实例" prop="processInstanceId">
        <el-input v-model="queryParams.processInstanceId" placeholder="请输入流程实例ID" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['flowable:formData:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['flowable:formData:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['flowable:formData:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="数据ID" prop="dataId" align="center" width="100" />
      <el-table-column label="表单标识" prop="formKey" align="center" min-width="150" show-overflow-tooltip />
      <el-table-column label="业务主键" prop="businessKey" align="center" min-width="150" show-overflow-tooltip />
      <el-table-column label="流程实例" prop="processInstanceId" align="center" min-width="180" show-overflow-tooltip />
      <el-table-column label="任务节点" prop="taskKey" align="center" min-width="140" show-overflow-tooltip />
      <el-table-column label="提交人" prop="submitBy" align="center" width="120" />
      <el-table-column label="提交时间" prop="submitTime" align="center" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.submitTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['flowable:formData:query']">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['flowable:formData:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['flowable:formData:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="720px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="表单ID">
          <el-input v-model="form.formId" placeholder="请输入表单ID" />
        </el-form-item>
        <el-form-item label="表单标识" prop="formKey">
          <el-input v-model="form.formKey" placeholder="请输入表单标识" />
        </el-form-item>
        <el-form-item label="业务主键" prop="businessKey">
          <el-input v-model="form.businessKey" placeholder="请输入业务主键" />
        </el-form-item>
        <el-form-item label="流程实例">
          <el-input v-model="form.processInstanceId" placeholder="请输入流程实例ID" />
        </el-form-item>
        <el-form-item label="任务ID">
          <el-input v-model="form.taskId" placeholder="请输入任务ID" />
        </el-form-item>
        <el-form-item label="任务节点">
          <el-input v-model="form.taskKey" placeholder="请输入任务节点标识" />
        </el-form-item>
        <el-form-item label="表单值(JSON)" prop="formValues">
          <el-input type="textarea" v-model="form.formValues" placeholder="请输入表单值 JSON" :rows="8" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="表单数据详情" :visible.sync="detailOpen" width="720px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="数据ID">{{ detail.dataId }}</el-descriptions-item>
        <el-descriptions-item label="表单标识">{{ detail.formKey }}</el-descriptions-item>
        <el-descriptions-item label="业务主键">{{ detail.businessKey }}</el-descriptions-item>
        <el-descriptions-item label="流程实例">{{ detail.processInstanceId }}</el-descriptions-item>
        <el-descriptions-item label="任务节点">{{ detail.taskKey }}</el-descriptions-item>
        <el-descriptions-item label="提交人">{{ detail.submitBy }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ parseTime(detail.submitTime) }}</el-descriptions-item>
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
  </div>
</template>

<script>
import { listFormData, getFormData, addFormData, updateFormData, delFormData } from '@/api/flowable/formData'

export default {
  name: 'FlowableFormData',
  data() {
    return {
      loading: false,
      showSearch: true,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        formKey: undefined,
        businessKey: undefined,
        processInstanceId: undefined
      },
      dataList: [],
      total: 0,
      ids: [],
      single: true,
      multiple: true,
      open: false,
      title: '',
      form: {},
      rules: {
        formKey: [
          { required: true, message: '表单标识不能为空', trigger: 'blur' }
        ],
        businessKey: [
          { required: true, message: '业务主键不能为空', trigger: 'blur' }
        ]
      },
      detailOpen: false,
      detail: {}
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listFormData(this.queryParams).then(response => {
        this.dataList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
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
        formKey: undefined,
        businessKey: undefined,
        processInstanceId: undefined
      }
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.dataId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    resetFormData() {
      this.form = {
        dataId: undefined,
        formId: undefined,
        formKey: undefined,
        businessKey: undefined,
        processInstanceId: undefined,
        taskId: undefined,
        taskKey: undefined,
        formValues: ''
      }
      this.$nextTick(() => {
        if (this.$refs.formRef) {
          this.$refs.formRef.resetFields()
        }
      })
    },
    handleAdd() {
      this.resetFormData()
      this.title = '新增表单数据'
      this.open = true
    },
    handleUpdate(row) {
      const dataId = row?.dataId || this.ids[0]
      if (!dataId) {
        return
      }
      this.resetFormData()
      getFormData(dataId).then(response => {
        const data = response.data || {}
        this.form = {
          dataId: data.dataId,
          formId: data.formId,
          formKey: data.formKey,
          businessKey: data.businessKey,
          processInstanceId: data.processInstanceId,
          taskId: data.taskId,
          taskKey: data.taskKey,
          formValues: this.formatJson(data.formValues)
        }
        this.title = '修改表单数据'
        this.open = true
      })
    },
    handleDelete(row) {
      const dataIds = row?.dataId ? [row.dataId] : this.ids
      if (!dataIds.length) {
        this.$message.warning('请选择要删除的数据')
        return
      }
      this.$modal.confirm('确认删除选中的表单数据吗？').then(() => delFormData(dataIds.join(','))).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    handleView(row) {
      const dataId = row?.dataId
      if (!dataId) {
        return
      }
      getFormData(dataId).then(response => {
        this.detail = Object.assign({}, response.data)
        this.detailOpen = true
      })
    },
    submitForm() {
      this.$refs.formRef.validate(valid => {
        if (!valid) {
          return
        }
        const payload = Object.assign({}, this.form, {
          formValues: this.buildJsonPayload(this.form.formValues)
        })
        const request = this.form.dataId ? updateFormData : addFormData
        request(payload).then(() => {
          this.$modal.msgSuccess('保存成功')
          this.open = false
          this.getList()
        })
      })
    },
    buildJsonPayload(value) {
      if (!value) {
        return ''
      }
      if (typeof value === 'string') {
        try {
          JSON.parse(value)
          return value
        } catch (error) {
          this.$message.warning('表单值不是合法 JSON，已按原文本保存')
          return value
        }
      }
      try {
        return JSON.stringify(value)
      } catch (error) {
        return ''
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
  word-break: break-word;
  margin: 0;
}
</style>
