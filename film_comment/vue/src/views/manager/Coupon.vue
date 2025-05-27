<template>
  <div>
    <div class="card" style="margin-bottom: 10px">
      <el-input
        v-model="data.name"
        placeholder="请输入优惠券名称查询"
        style="width: 300px; margin-right: 10px"
      ></el-input>
      <el-select
        v-model="data.isHoliday"
        placeholder="是否节日优惠券"
        style="width: 200px; margin-right: 10px"
      >
        <el-option label="全部" :value="null"></el-option>
        <el-option label="是" :value="1"></el-option>
        <el-option label="否" :value="0"></el-option>
      </el-select>
      <el-select
        v-model="data.status"
        placeholder="状态"
        style="width: 200px; margin-right: 10px"
      >
        <el-option label="全部" :value="null"></el-option>
        <el-option label="有效" :value="1"></el-option>
        <el-option label="无效" :value="0"></el-option>
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" @click="reset">重置</el-button>
      <el-button type="success" @click="handleAdd">新增</el-button>
    </div>

    <div class="card">
      <el-table :data="data.tableData" stripe border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="name" label="优惠券名称"></el-table-column>
        <el-table-column prop="discount" label="折扣率">
          <template #default="scope">
            {{ (scope.row.discount * 10).toFixed(1) }}折
          </template>
        </el-table-column>
        <el-table-column prop="startDate" label="开始日期"></el-table-column>
        <el-table-column prop="endDate" label="结束日期"></el-table-column>
        <el-table-column prop="isHoliday" label="是否节日优惠券">
          <template #default="scope">
            <el-tag type="success" v-if="scope.row.isHoliday === 1">是</el-tag>
            <el-tag type="info" v-else>否</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag type="success" v-if="scope.row.status === 1">有效</el-tag>
            <el-tag type="danger" v-else>无效</el-tag>
          </template>
        </el-table-column>
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
      title="优惠券信息"
      v-model="data.formVisible"
      width="40%"
      :close-on-click-modal="false"
    >
      <el-form
        :model="data.form"
        label-width="120px"
        style="padding-right: 50px"
      >
        <el-form-item label="优惠券名称" prop="name">
          <el-input v-model="data.form.name" autocomplete="off" />
        </el-form-item>
        <el-form-item label="折扣率" prop="discount">
          <el-slider
            v-model="data.form.discountValue"
            :min="1"
            :max="10"
            :step="0.1"
            :format-tooltip="formatTooltip"
          />
        </el-form-item>
        <el-form-item label="有效期" prop="dateRange">
          <el-date-picker
            v-model="data.form.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="是否节日优惠券" prop="isHoliday">
          <el-radio-group v-model="data.form.isHoliday">
            <el-radio :label="1">是</el-radio>
            <el-radio :label="0">否</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="data.form.status">
            <el-radio :label="1">有效</el-radio>
            <el-radio :label="0">无效</el-radio>
          </el-radio-group>
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
  isHoliday: null,
  status: null,
  formVisible: false,
  form: {},
});

const formatTooltip = (val) => {
  return val.toFixed(1) + "折";
};

const load = () => {
  request
    .get("/coupon/selectPage", {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        name: data.name,
        isHoliday: data.isHoliday,
        status: data.status,
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
  data.isHoliday = null;
  data.status = null;
  load();
};

const handleAdd = () => {
  data.form = {
    discountValue: 8.0,
    isHoliday: 0,
    status: 1,
    dateRange: [],
  };
  data.formVisible = true;
};

const handleEdit = (row) => {
  const form = JSON.parse(JSON.stringify(row));
  form.discountValue = form.discount * 10;
  form.dateRange = [new Date(form.startDate), new Date(form.endDate)];
  data.form = form;
  data.formVisible = true;
};

const handleDelete = (id) => {
  request.delete("/coupon/delete/" + id).then((res) => {
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
    ElMessage.warning("请输入优惠券名称");
    return;
  }
  if (!data.form.dateRange || data.form.dateRange.length !== 2) {
    ElMessage.warning("请选择有效期");
    return;
  }

  const coupon = {
    id: data.form.id,
    name: data.form.name,
    discount: data.form.discountValue / 10,
    startDate: data.form.dateRange[0],
    endDate: data.form.dateRange[1],
    isHoliday: data.form.isHoliday,
    status: data.form.status,
  };

  console.log("提交优惠券数据:", coupon);

  request[data.form.id ? "put" : "post"](
    data.form.id ? "/coupon/update" : "/coupon/add",
    coupon
  )
    .then((res) => {
      console.log("优惠券保存响应:", res);
      if (res.code === "200") {
        ElMessage.success("保存成功");
        data.formVisible = false;
        load();
      } else {
        ElMessage.error(res.msg || "保存失败");
      }
    })
    .catch((err) => {
      console.error("优惠券保存错误:", err);
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
