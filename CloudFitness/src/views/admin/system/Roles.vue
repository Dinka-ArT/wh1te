<template>
  <div class="page-container">
    <div class="page-header">
      <h2 class="page-title">角色管理</h2>
      <el-button type="primary" :icon="Plus" @click="handleAdd">
        新增角色
      </el-button>
    </div>

    <!-- 搜索区 -->
    <div class="search-container card">
      <el-form :model="searchForm" :inline="true">
        <el-form-item label="角色名称">
          <el-input
            v-model="searchForm.role_name"
            placeholder="请输入角色名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">
            搜索
          </el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </div>

    <!-- 列表区 -->
    <div class="table-container card">
      <el-table
        v-loading="loading"
        :data="tableData"
        stripe
        style="width: 100%"
      >
        <el-table-column prop="role_id" label="角色ID" width="80" />
        <el-table-column prop="role_name" label="角色名称" width="200" />
        <el-table-column label="权限数量" width="120">
          <template #default="{ row }">
            <el-tag type="info">{{ row.permission_count || 0 }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="创建时间" width="180">
          <template #default="{ row }">
            {{ formatDate(row.created_at) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="280" fixed="right">
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              size="small"
              :icon="View"
              @click="handleView(row)"
            >
              查看详情
            </el-button>
            <el-button
              type="primary"
              link
              size="small"
              :icon="Edit"
              @click="handleEdit(row)"
            >
              编辑
            </el-button>
            <el-button
              type="info"
              link
              size="small"
              :icon="Setting"
              @click="handleAssignPermissions(row)"
            >
              分配权限
            </el-button>
            <el-button
              type="danger"
              link
              size="small"
              :icon="Delete"
              @click="handleDelete(row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-container">
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.page_size"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="handleSizeChange"
          @current-change="handlePageChange"
        />
      </div>
    </div>

    <!-- 新增/编辑表单弹窗 -->
    <el-dialog
      v-model="formVisible"
      :title="formTitle"
      width="500px"
      @close="handleFormClose"
    >
      <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
      >
        <el-form-item label="角色名称" prop="role_name">
          <el-input
            v-model="formData.role_name"
            placeholder="请输入角色名称（1-50位，唯一）"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="formVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>

    <!-- 分配权限弹窗 -->
    <el-dialog
      v-model="permissionsVisible"
      title="分配权限"
      width="600px"
    >
      <el-checkbox-group v-model="selectedPermissionIds">
        <el-checkbox
          v-for="permission in allPermissions"
          :key="permission.permission_id"
          :label="permission.permission_id"
        >
          {{ permission.permission_name }}
          <span v-if="permission.permission_description" class="permission-desc">
            （{{ permission.permission_description }}）
          </span>
        </el-checkbox>
      </el-checkbox-group>
      <template #footer>
        <el-button @click="permissionsVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSavePermissions">确定</el-button>
      </template>
    </el-dialog>

    <!-- 详情弹窗 -->
    <el-dialog
      v-model="detailVisible"
      title="角色详情"
      width="700px"
    >
      <div v-if="selectedRow" class="detail-content">
        <el-descriptions :column="2" border>
          <el-descriptions-item label="角色ID">
            {{ selectedRow.role_id }}
          </el-descriptions-item>
          <el-descriptions-item label="角色名称">
            {{ selectedRow.role_name }}
          </el-descriptions-item>
          <el-descriptions-item label="权限数量">
            <el-tag type="info">{{ selectedRow.permission_count || 0 }}</el-tag>
          </el-descriptions-item>
          <el-descriptions-item label="创建时间">
            {{ formatDate(selectedRow.created_at) }}
          </el-descriptions-item>
          <el-descriptions-item label="权限列表" :span="2">
            <el-tag
              v-for="permission in selectedRow.permissions"
              :key="permission.permission_id"
              type="info"
              style="margin-right: 8px; margin-bottom: 8px"
            >
              {{ permission.permission_name }}
            </el-tag>
            <span v-if="!selectedRow.permissions || selectedRow.permissions.length === 0">-</span>
          </el-descriptions-item>
        </el-descriptions>
      </div>
      <template #footer>
        <el-button @click="detailVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { formatDate } from '@/utils/date'
import {
  Plus,
  Search,
  Refresh,
  View,
  Edit,
  Delete,
  Setting
} from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  getAdminRoles,
  createAdminRole,
  updateAdminRole,
  deleteAdminRole,
  updateAdminRolePermissions,
  getAllPermissions
} from '@/api/admin'

const loading = ref(false)
const formVisible = ref(false)
const detailVisible = ref(false)
const permissionsVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)
const selectedRow = ref(null)
const selectedPermissionIds = ref([])

const searchForm = reactive({
  role_name: ''
})

const pagination = reactive({
  page: 1,
  page_size: 20,
  total: 0
})

const formData = reactive({
  role_id: null,
  role_name: ''
})

const allPermissions = ref([])

const formRules = {
  role_name: [
    { required: true, message: '请输入角色名称', trigger: 'blur' },
    { min: 1, max: 50, message: '角色名称长度为1-50位', trigger: 'blur' }
  ]
}


const tableData = ref([])

const formTitle = computed(() => isEdit.value ? '编辑角色' : '新增角色')

const fetchData = async () => {
  loading.value = true
  try {
    const params = {
      page: pagination.page,
      page_size: pagination.page_size
    }
    if (searchForm.role_name) {
      params.role_name = searchForm.role_name
    }
    const response = await getAdminRoles(params)
    tableData.value = response.list || []
    pagination.total = response.total || 0
  } catch (error) {
    ElMessage.error('获取角色列表失败')
  } finally {
    loading.value = false
  }
}

const handleSearch = () => {
  pagination.page = 1
  fetchData()
}

const handleReset = () => {
  searchForm.role_name = ''
  handleSearch()
}

const handleAdd = () => {
  isEdit.value = false
  Object.assign(formData, {
    role_id: null,
    role_name: ''
  })
  formVisible.value = true
}

const handleEdit = (row) => {
  isEdit.value = true
  Object.assign(formData, {
    role_id: row.role_id,
    role_name: row.role_name
  })
  formVisible.value = true
}

const handleView = (row) => {
  selectedRow.value = row
  detailVisible.value = true
}

const handleAssignPermissions = async (row) => {
  selectedRow.value = row
  try {
    const response = await getAdminRoleDetail(row.role_id)
    selectedPermissionIds.value = response.permissions
      ? response.permissions.map(p => p.permission_id)
      : []
    permissionsVisible.value = true
  } catch (error) {
    ElMessage.error('获取角色详情失败')
  }
}

const handleSavePermissions = async () => {
  if (selectedPermissionIds.value.length === 0) {
    ElMessage.warning('请至少选择一个权限')
    return
  }
  try {
    await updateAdminRolePermissions(selectedRow.value.role_id, {
      permission_ids: selectedPermissionIds.value
    })
    ElMessage.success('权限分配成功')
    permissionsVisible.value = false
    fetchData()
  } catch (error) {
    ElMessage.error('权限分配失败')
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  
  await formRef.value.validate(async (valid) => {
    if (valid) {
      try {
        if (isEdit.value) {
          await updateAdminRole(formData.role_id, formData)
          ElMessage.success('编辑成功')
        } else {
          await createAdminRole(formData)
          ElMessage.success('新增成功')
        }
        formVisible.value = false
        fetchData()
      } catch (error) {
        ElMessage.error(isEdit.value ? '编辑失败' : '新增失败')
      }
    }
  })
}

const handleFormClose = () => {
  formRef.value?.resetFields()
}

const handleDelete = (row) => {
  ElMessageBox.confirm('确定要删除该角色吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteAdminRole(row.role_id)
      ElMessage.success('删除成功')
      fetchData()
    } catch (error) {
      ElMessage.error('删除失败')
    }
  }).catch(() => {})
}

const handleSizeChange = (size) => {
  pagination.page_size = size
  fetchData()
}

const handlePageChange = (page) => {
  pagination.page = page
  fetchData()
}

const loadPermissions = async () => {
  try {
    const response = await getAllPermissions()
    allPermissions.value = response.list || []
  } catch (error) {
    console.error('加载权限列表失败', error)
  }
}

onMounted(() => {
  fetchData()
  loadPermissions()
})
</script>

<style lang="scss" scoped>
.page-container {
  padding: 24px;
  height: 100%;
  overflow-y: auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  
  .page-title {
    font-size: 24px;
    font-weight: 600;
    color: #303133;
  }
}

.search-container {
  margin-bottom: 20px;
  padding: 20px;
}

.table-container {
  padding: 20px;
}

.pagination-container {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.detail-content {
  padding: 10px 0;
}

.permission-desc {
  color: #909399;
  font-size: 12px;
}
</style>

