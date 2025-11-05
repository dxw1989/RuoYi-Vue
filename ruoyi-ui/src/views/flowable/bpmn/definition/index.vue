<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="流程标识" prop="definitionKey">
        <el-input v-model="queryParams.definitionKey" placeholder="请输入流程标识" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="流程名称" prop="definitionName">
        <el-input v-model="queryParams.definitionName" placeholder="请输入流程名称" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="流程分类" prop="category">
        <el-input v-model="queryParams.category" placeholder="请输入流程分类" clearable @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="挂起状态" prop="suspended">
        <el-select v-model="queryParams.suspended" placeholder="请选择" clearable>
          <el-option v-for="option in suspendOptions" :key="option.value" :label="option.label" :value="option.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleRemove" v-hasPermi="['flowable:definition:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="definitionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="流程标识" prop="definitionKey" align="center" min-width="150" show-overflow-tooltip />
      <el-table-column label="流程名称" prop="definitionName" align="center" min-width="180" show-overflow-tooltip />
      <el-table-column label="分类" prop="category" align="center" min-width="120" show-overflow-tooltip />
      <el-table-column label="版本" prop="version" align="center" width="80" />
      <el-table-column label="部署名称" prop="deploymentName" align="center" min-width="180" show-overflow-tooltip />
      <el-table-column label="部署时间" prop="deploymentTime" align="center" width="160">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.deploymentTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="挂起状态" prop="suspended" align="center" width="100">
        <template slot-scope="scope">
          <dict-tag :options="suspendOptions" :value="scope.row.suspended" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="260" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-view" @click="handleView(scope.row)" v-hasPermi="['flowable:definition:query']">详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-document" @click="handleViewXml(scope.row)" v-hasPermi="['flowable:definition:query']">XML</el-button>
          <el-button size="mini" type="text" icon="el-icon-video-pause" v-if="!scope.row.suspended" @click="handleSuspend(scope.row)" v-hasPermi="['flowable:definition:suspend']">挂起</el-button>
          <el-button size="mini" type="text" icon="el-icon-video-play" v-if="scope.row.suspended" @click="handleActivate(scope.row)" v-hasPermi="['flowable:definition:activate']">激活</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleRemove(scope.row)" v-hasPermi="['flowable:definition:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="流程定义详情" :visible.sync="detailOpen" width="620px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="流程标识">{{ detail.definitionKey }}</el-descriptions-item>
        <el-descriptions-item label="流程名称">{{ detail.definitionName }}</el-descriptions-item>
        <el-descriptions-item label="版本">{{ detail.version }}</el-descriptions-item>
        <el-descriptions-item label="分类">{{ detail.category }}</el-descriptions-item>
        <el-descriptions-item label="部署名称">{{ detail.deploymentName }}</el-descriptions-item>
        <el-descriptions-item label="部署时间">{{ parseTime(detail.deploymentTime) }}</el-descriptions-item>
        <el-descriptions-item label="挂起状态">
          <dict-tag :options="suspendOptions" :value="detail.suspended" />
        </el-descriptions-item>
        <el-descriptions-item label="关联模型">{{ detail.modelName }}</el-descriptions-item>
        <el-descriptions-item label="关联表单">{{ detail.formKey }}</el-descriptions-item>
        <el-descriptions-item label="流程描述">{{ detail.description }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="流程定义 XML" :visible.sync="xmlOpen" width="780px" append-to-body>
      <el-input type="textarea" :rows="18" v-model="xmlContent" readonly />
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="xmlOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listDefinition, getDefinition, getDefinitionXml, suspendDefinition, activateDefinition, removeDeployment } from '@/api/flowable/definition'

export default {
  name: 'FlowableDefinition',
  data() {
    return {
      loading: false,
      showSearch: true,
      definitionList: [],
      ids: [],
      single: true,
      multiple: true,
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        definitionKey: undefined,
        definitionName: undefined,
        category: undefined,
        suspended: undefined
      },
      suspendOptions: [
        { label: '激活', value: 'false' },
        { label: '挂起', value: 'true' }
      ],
      detailOpen: false,
      detail: {},
      xmlOpen: false,
      xmlContent: ''
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listDefinition(this.queryParams).then(response => {
        this.definitionList = response.rows || []
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
        definitionKey: undefined,
        definitionName: undefined,
        category: undefined,
        suspended: undefined
      }
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.deploymentId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleView(row) {
      const definitionId = row?.definitionId
      if (!definitionId) {
        return
      }
      getDefinition(definitionId).then(response => {
        this.detail = response.data || {}
        this.detailOpen = true
      })
    },
    handleViewXml(row) {
      const definitionId = row?.definitionId
      if (!definitionId) {
        return
      }
      getDefinitionXml(definitionId).then(response => {
        this.xmlContent = response.data || ''
        this.xmlOpen = true
      })
    },
    handleSuspend(row) {
      const definitionId = row?.definitionId
      if (!definitionId) {
        return
      }
      this.$modal.confirm('确认挂起该流程定义吗？').then(() => suspendDefinition(definitionId)).then(() => {
        this.$modal.msgSuccess('挂起成功')
        this.getList()
      }).catch(() => {})
    },
    handleActivate(row) {
      const definitionId = row?.definitionId
      if (!definitionId) {
        return
      }
      this.$modal.confirm('确认激活该流程定义吗？').then(() => activateDefinition(definitionId)).then(() => {
        this.$modal.msgSuccess('激活成功')
        this.getList()
      }).catch(() => {})
    },
    handleRemove(row) {
      const deploymentIds = row?.deploymentId ? [row.deploymentId] : this.ids
      if (!deploymentIds.length) {
        this.$message.warning('请选择需要删除的数据')
        return
      }
      this.$modal.confirm('确认删除选中的流程定义部署吗？').then(() => {
        return Promise.all(deploymentIds.map(id => removeDeployment(id)))
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      }).catch(() => {})
    }
  }
}
</script>
