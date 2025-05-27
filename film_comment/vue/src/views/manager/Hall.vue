<template>
  <div>
    <div class="card" style="margin-bottom: 10px">
      <el-input
        v-model="data.name"
        placeholder="请输入影厅名称查询"
        style="width: 300px; margin-right: 10px"
      ></el-input>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" @click="reset">重置</el-button>
      <el-button type="success" @click="handleAdd">新增</el-button>
    </div>

    <div class="card">
      <el-table :data="data.tableData" stripe border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="影厅名称"></el-table-column>
        <el-table-column prop="rowCount" label="行数"></el-table-column>
        <el-table-column prop="columnCount" label="列数"></el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" @click="handleEdit(scope.row)"
              >编辑</el-button
            >
            <el-popconfirm
              title="确定删除吗？"
              @confirm="handleDelete(scope.row.id)"
            >
              <template #reference>
                <el-button type="danger">删除</el-button>
              </template>
            </el-popconfirm>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 10px">
        <el-pagination
          background
          layout="total, prev, pager, next"
          v-model:current-page="data.pageNum"
          v-model:page-size="data.pageSize"
          :total="data.total"
          @current-change="load"
        />
      </div>
    </div>

    <el-dialog
      title="影厅信息"
      v-model="data.formVisible"
      width="40%"
      :close-on-click-modal="false"
    >
      <el-form
        :model="data.form"
        label-width="100px"
        style="padding-right: 50px"
      >
        <el-form-item label="影厅名称" prop="name">
          <el-input v-model="data.form.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="行数" prop="rowCount">
          <el-input-number v-model="data.form.rowCount" :min="1" :max="20" />
        </el-form-item>
        <el-form-item label="列数" prop="columnCount">
          <el-input-number v-model="data.form.columnCount" :min="1" :max="20" />
        </el-form-item>
      </el-form>
      <template #footer>
        <span class="dialog-footer">
          <el-button @click="data.formVisible = false">取 消</el-button>
          <el-button type="primary" @click="save">确 定</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";

const data = reactive({
  tableData: [],
  total: 0,
  pageNum: 1,
  pageSize: 10,
  name: null,
  formVisible: false,
  form: {},
});

const load = () => {
  request
    .get("/hall/selectPage", {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        name: data.name,
      },
    })
    .then((res) => {
      data.tableData = res.data.list;
      data.total = res.data.total;
    });
};
load();

const reset = () => {
  data.name = null;
  load();
};

const handleAdd = () => {
  data.form = {
    rowCount: 10,
    columnCount: 12,
  };
  data.formVisible = true;
};

const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  data.formVisible = true;
};

const handleDelete = (id) => {
  request.delete("/hall/delete/" + id).then((res) => {
    if (res.code === "200") {
      ElMessage.success("删除成功");
      load();
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const save = () => {
  if (!data.form.name) {
    ElMessage.warning("请输入影厅名称");
    return;
  }
  if (!data.form.rowCount || data.form.rowCount < 1) {
    ElMessage.warning("请输入有效的行数");
    return;
  }
  if (!data.form.columnCount || data.form.columnCount < 1) {
    ElMessage.warning("请输入有效的列数");
    return;
  }

  // 创建一个新对象来避免可能的引用问题
  const formData = JSON.parse(JSON.stringify(data.form));

  console.log("提交影厅数据:", formData);

  request[formData.id ? "put" : "post"](
    formData.id ? "/hall/update" : "/hall/add",
    formData
  )
    .then((res) => {
      console.log("影厅保存响应:", res);
      if (res.code === "200") {
        ElMessage.success("保存成功");
        data.formVisible = false;
        load();
      } else {
        ElMessage.error(res.msg || "保存失败");
      }
    })
    .catch((err) => {
      console.error("影厅保存错误:", err);
      ElMessage.error("保存过程中发生错误");
    });
};
</script>

<style scoped>
.card {
  background-color: #fff;
  border-radius: 5px;
  padding: 20px;
  margin-bottom: 10px;
}
</style>
