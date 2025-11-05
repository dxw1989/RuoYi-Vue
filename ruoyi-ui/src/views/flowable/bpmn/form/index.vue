<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="表单标识" prop="formKey">
        <el-input v-model="queryParams.formKey" placeholder="请输入表单标识" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="表单名称" prop="formName">
        <el-input v-model="queryParams.formName" placeholder="请输入表单名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="表单类型" prop="formType">
        <el-input v-model="queryParams.formType" placeholder="请输入表单类型" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.label" :value="dict.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['flowable:form:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['flowable:form:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['flowable:form:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="formList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="表单标识" prop="formKey" align="center" min-width="160" show-overflow-tooltip />
      <el-table-column label="表单名称" prop="formName" align="center" min-width="160" show-overflow-tooltip />
      <el-table-column label="表单类型" prop="formType" align="center" min-width="120" show-overflow-tooltip />
      <el-table-column label="状态" prop="status" align="center" width="100">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_normal_disable" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="更新时间" prop="updateTime" align="center" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['flowable:form:query']">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['flowable:form:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['flowable:form:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="720px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="表单标识" prop="formKey">
          <el-input v-model="form.formKey" placeholder="请输入表单标识" maxlength="64" />
        </el-form-item>
        <el-form-item label="表单名称" prop="formName">
          <el-input v-model="form.formName" placeholder="请输入表单名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="表单类型" prop="formType">
          <el-input v-model="form.formType" placeholder="请输入表单类型" maxlength="50" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio v-for="dict in dict.type.sys_normal_disable" :key="dict.value" :label="dict.value">{{ dict.label }}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="表单描述" prop="description">
          <el-input type="textarea" v-model="form.description" placeholder="请输入描述" :rows="3" />
        </el-form-item>
        <el-form-item label="表单配置(JSON)" prop="formConfig">
          <el-input type="textarea" v-model="form.formConfig" placeholder="请输入表单配置 JSON" :rows="10" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="表单详情" :visible.sync="detailOpen" width="720px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="表单标识">{{ detail.formKey }}</el-descriptions-item>
        <el-descriptions-item label="表单名称">{{ detail.formName }}</el-descriptions-item>
        <el-descriptions-item label="表单类型">{{ detail.formType }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <dict-tag :options="dict.type.sys_normal_disable" :value="detail.status" />
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(detail.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="更新时间">{{ parseTime(detail.updateTime) }}</el-descriptions-item>
        <el-descriptions-item label="描述" :span="2">{{ detail.description }}</el-descriptions-item>
      </el-descriptions>
      <div class="mt20">
        <el-card shadow="never">
          <div slot="header" class="clearfix">
            <span>表单配置</span>
          </div>
          <pre class="json-preview">{{ formatJson(detail.formConfig) }}</pre>
        </el-card>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listForm, getForm, addForm, updateForm, delForm } from '@/api/flowable/form'

export default {
  name: 'FlowableForm',
  dicts: ['sys_normal_disable'],
  data() {
    return {
      loading: false,
      showSearch: true,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        formKey: undefined,
        formName: undefined,
        formType: undefined,
        status: undefined
      },
      formList: [],
      total: 0,
      ids: [],
      single: true,
      multiple: true,
      title: '',
      open: false,
      form: {},
      rules: {
        formKey: [
          { required: true, message: '表单标识不能为空', trigger: 'blur' }
        ],
        formName: [
          { required: true, message: '表单名称不能为空', trigger: 'blur' }
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
      listForm(this.queryParams).then(response => {
        this.formList = response.rows || []
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
        formName: undefined,
        formType: undefined,
        status: undefined
      }
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.formId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    resetFormData() {
      this.form = {
        formId: undefined,
        formKey: undefined,
        formName: undefined,
        formType: undefined,
        status: '0',
        description: undefined,
        formConfig: ''
      }
      this.$nextTick(() => {
        if (this.$refs.formRef) {
          this.$refs.formRef.resetFields()
        }
      })
    },
    handleAdd() {
      this.resetFormData()
      this.title = '新增表单'
      this.open = true
    },
    handleUpdate(row) {
      const formId = row?.formId || this.ids[0]
      if (!formId) {
        return
      }
      this.resetFormData()
      getForm(formId).then(response => {
        const data = response.data || {}
        this.form = {
          formId: data.formId,
          formKey: data.formKey,
          formName: data.formName,
          formType: data.formType,
          status: data.status || '0',
          description: data.description,
          formConfig: this.formatJson(data.formConfig)
        }
        this.title = '修改表单'
        this.open = true
      })
    },
    handleDelete(row) {
      const formIds = row?.formId ? [row.formId] : this.ids
      if (!formIds.length) {
        this.$message.warning('请选择要删除的表单')
        return
      }
      this.$modal.confirm('确认删除选中的表单吗？').then(() => delForm(formIds.join(','))).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    handleView(row) {
      const formId = row?.formId
      if (!formId) {
        return
      }
      getForm(formId).then(response => {
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
          formConfig: this.buildConfigPayload(this.form.formConfig)
        })
        const request = this.form.formId ? updateForm : addForm
        request(payload).then(() => {
          this.$modal.msgSuccess('保存成功')
          this.open = false
          this.getList()
        })
      })
    },
    buildConfigPayload(value) {
      if (!value) {
        return ''
      }
      if (typeof value === 'string') {
        try {
          JSON.parse(value)
          return value
        } catch (error) {
          this.$message.warning('表单配置不是合法 JSON，已按原文本保存')
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
