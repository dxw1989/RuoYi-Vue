<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="模型标识" prop="modelKey">
        <el-input v-model="queryParams.modelKey" placeholder="请输入模型标识" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="模型名称" prop="modelName">
        <el-input v-model="queryParams.modelName" placeholder="请输入模型名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="模型分类" prop="category">
        <el-input v-model="queryParams.category" placeholder="请输入模型分类" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="模型状态" clearable>
          <el-option v-for="option in modelStatusOptions" :key="option.value" :label="option.label" :value="option.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['flowable:model:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['flowable:model:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-upload2" size="mini" :disabled="single" @click="handleDesign" v-hasPermi="['flowable:model:design']">设计</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="info" plain icon="el-icon-position" size="mini" :disabled="single" @click="handleDeploy" v-hasPermi="['flowable:model:deploy']">发布</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['flowable:model:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="modelList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="模型标识" prop="modelKey" align="center" min-width="140" show-overflow-tooltip />
      <el-table-column label="模型名称" prop="modelName" align="center" min-width="160" show-overflow-tooltip />
      <el-table-column label="分类" prop="category" align="center" min-width="120" show-overflow-tooltip />
      <el-table-column label="状态" prop="status" align="center" width="100">
        <template slot-scope="scope">
          <dict-tag :options="modelStatusDict" :value="scope.row.status" />
        </template>
      </el-table-column>
      <el-table-column label="版本" prop="version" align="center" width="80" />
      <el-table-column label="表单标识" prop="formKey" align="center" min-width="140" show-overflow-tooltip />
      <el-table-column label="发布时间" prop="deployTime" align="center" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.deployTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" prop="updateTime" align="center" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="240" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['flowable:model:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit-outline" @click="handleDesign(scope.row)" v-hasPermi="['flowable:model:design']">设计</el-button>
          <el-button size="mini" type="text" icon="el-icon-upload2" @click="handleDeploy(scope.row)" v-hasPermi="['flowable:model:deploy']">发布</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['flowable:model:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="模型标识" prop="modelKey">
          <el-input v-model="form.modelKey" placeholder="请输入模型标识" maxlength="64" />
        </el-form-item>
        <el-form-item label="模型名称" prop="modelName">
          <el-input v-model="form.modelName" placeholder="请输入模型名称" maxlength="100" />
        </el-form-item>
        <el-form-item label="模型分类" prop="category">
          <el-input v-model="form.category" placeholder="请输入模型分类" maxlength="100" />
        </el-form-item>
        <el-form-item label="绑定表单">
          <el-select v-model="form.formId" placeholder="请选择绑定表单" clearable filterable>
            <el-option v-for="item in formOptions" :key="item.formId" :label="`${item.formName}(${item.formKey})`" :value="item.formId" />
          </el-select>
        </el-form-item>
        <el-form-item label="表单标识" prop="formKey">
          <el-input v-model="form.formKey" placeholder="请输入表单标识" maxlength="64" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input type="textarea" v-model="form.remark" placeholder="请输入备注" maxlength="500" rows="3" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="模型设计" :visible.sync="editorOpen" width="780px" append-to-body>
      <el-alert title="暂未集成可视化设计器，可直接编辑 BPMN XML 或设计器 JSON。" type="info" show-icon class="mb20" />
      <el-form ref="editorFormRef" :model="editorForm" label-width="120px">
        <el-form-item label="模型XML" prop="modelContent">
          <el-input type="textarea" v-model="editorForm.modelContent" placeholder="请输入 BPMN XML 内容" :rows="10" />
        </el-form-item>
        <el-form-item label="设计器JSON" prop="modelEditorJson">
          <el-input type="textarea" v-model="editorForm.modelEditorJson" placeholder="请输入设计器 JSON" :rows="6" />
        </el-form-item>
        <el-form-item label="绑定表单">
          <el-select v-model="editorForm.formId" placeholder="请选择绑定表单" clearable filterable>
            <el-option v-for="item in formOptions" :key="item.formId" :label="`${item.formName}(${item.formKey})`" :value="item.formId" />
          </el-select>
        </el-form-item>
        <el-form-item label="表单标识">
          <el-input v-model="editorForm.formKey" placeholder="请输入表单标识" maxlength="64" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitEditor">保 存</el-button>
        <el-button @click="editorOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listModel, getModel, addModel, updateModel, delModel, deployModel, getModelEditor, saveModelEditor } from '@/api/flowable/model'
import { listForm } from '@/api/flowable/form'

const statusMap = [
  { value: 0, label: '草稿' },
  { value: 1, label: '待发布' },
  { value: 2, label: '已发布' }
]

export default {
  name: 'FlowableModel',
  data() {
    return {
      loading: false,
      showSearch: true,
      total: 0,
      modelList: [],
      single: true,
      multiple: true,
      ids: [],
      formOptions: [],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        modelKey: undefined,
        modelName: undefined,
        category: undefined,
        status: undefined
      },
      form: {},
      title: '',
      open: false,
      editorOpen: false,
      editorForm: {},
      modelStatusOptions: statusMap,
      rules: {
        modelKey: [
          { required: true, message: '模型标识不能为空', trigger: 'blur' }
        ],
        modelName: [
          { required: true, message: '模型名称不能为空', trigger: 'blur' }
        ]
      }
    }
  },
  computed: {
    modelStatusDict() {
      return statusMap.map(item => ({ label: item.label, value: item.value }))
    }
  },
  created() {
    this.getList()
    this.loadFormOptions()
  },
  methods: {
    getList() {
      this.loading = true
      listModel(this.queryParams).then(response => {
        this.modelList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    loadFormOptions() {
      listForm({ pageNum: 1, pageSize: 100 }).then(response => {
        this.formOptions = response.rows || []
      })
    },
    resetQuery() {
      this.queryParams.pageNum = 1
      this.queryParams.modelKey = undefined
      this.queryParams.modelName = undefined
      this.queryParams.category = undefined
      this.queryParams.status = undefined
      this.handleQuery()
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetFormData() {
      this.form = {
        modelId: undefined,
        modelKey: undefined,
        modelName: undefined,
        category: undefined,
        formId: undefined,
        formKey: undefined,
        remark: undefined
      }
      this.$nextTick(() => {
        if (this.$refs.formRef) {
          this.$refs.formRef.resetFields()
        }
      })
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.modelId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.resetFormData()
      this.title = '新增流程模型'
      this.open = true
    },
    handleUpdate(row) {
      const modelId = row.modelId || this.ids[0]
      if (!modelId) {
        this.$message.warning('请选择需要修改的模型')
        return
      }
      this.resetFormData()
      getModel(modelId).then(response => {
        this.form = Object.assign({}, response.data)
        this.open = true
        this.title = '修改流程模型'
      })
    },
    handleDesign(row) {
      const modelId = row?.modelId || this.ids[0]
      if (!modelId) {
        this.$message.warning('请选择需要设计的模型')
        return
      }
      this.editorForm = {
        modelId: modelId,
        modelContent: '',
        modelEditorJson: '',
        formId: undefined,
        formKey: undefined
      }
      getModelEditor(modelId).then(response => {
        const data = response.data || {}
        this.editorForm = {
          modelId: modelId,
          modelContent: data.modelContent || '',
          modelEditorJson: typeof data.modelEditorJson === 'string' ? data.modelEditorJson : JSON.stringify(data.modelEditorJson || {}, null, 2),
          formId: data.formId,
          formKey: data.formKey
        }
        this.editorOpen = true
      })
    },
    handleDeploy(row) {
      const modelId = row?.modelId || this.ids[0]
      if (!modelId) {
        this.$message.warning('请选择需要发布的模型')
        return
      }
      this.$modal.confirm('是否确认发布选中的流程模型？').then(() => {
        return deployModel(modelId)
      }).then(() => {
        this.$modal.msgSuccess('发布成功')
        this.getList()
      }).catch(() => {})
    },
    handleDelete(row) {
      const modelIds = row?.modelId ? [row.modelId] : this.ids
      if (!modelIds.length) {
        this.$message.warning('请选择需要删除的模型')
        return
      }
      this.$modal.confirm(`是否确认删除模型编号为 "${modelIds}" 的数据项？`).then(() => {
        return delModel(modelIds.join(','))
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    },
    cancel() {
      this.open = false
    },
    submitForm() {
      this.$refs.formRef.validate(valid => {
        if (!valid) {
          return
        }
        const request = this.form.modelId ? updateModel : addModel
        request(this.form).then(() => {
          this.$modal.msgSuccess('保存成功')
          this.open = false
          this.getList()
        })
      })
    },
    submitEditor() {
      const payload = {
        modelContent: this.editorForm.modelContent,
        modelEditorJson: this.normalizeJson(this.editorForm.modelEditorJson),
        formId: this.editorForm.formId,
        formKey: this.editorForm.formKey
      }
      saveModelEditor(this.editorForm.modelId, payload).then(() => {
        this.$modal.msgSuccess('模型内容已保存')
        this.editorOpen = false
        this.getList()
      })
    },
    normalizeJson(value) {
      if (!value) {
        return ''
      }
      if (typeof value === 'object') {
        try {
          return JSON.stringify(value)
        } catch (error) {
          this.$message.warning('设计器 JSON 序列化失败，已按空字符串提交')
          return ''
        }
      }
      try {
        const parsed = JSON.parse(value)
        return JSON.stringify(parsed)
      } catch (e) {
        this.$message.warning('设计器 JSON 解析失败，已按原文本提交')
        return value
      }
    }
  }
}
</script>
