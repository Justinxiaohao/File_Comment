<template>
  <div>
    <div class="card" style="margin-bottom: 10px">
      <el-select v-model="data.isUsed" placeholder="使用状态" style="width: 200px; margin-right: 10px">
        <el-option label="全部" :value="null"></el-option>
        <el-option label="未使用" :value="0"></el-option>
        <el-option label="已使用" :value="1"></el-option>
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" @click="reset">重置</el-button>
      <el-button type="success" @click="handleReceive">领取优惠券</el-button>
    </div>

    <div class="card">
      <el-table :data="data.tableData" stripe border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="couponName" label="优惠券名称"></el-table-column>
        <el-table-column prop="discount" label="折扣率">
          <template #default="scope">
            {{ scope.row.discount }}
          </template>
        </el-table-column>
        <el-table-column prop="getTime" label="获取时间"></el-table-column>
        <el-table-column prop="useTime" label="使用时间"></el-table-column>
        <el-table-column prop="isUsed" label="状态">
          <template #default="scope">
            <el-tag type="success" v-if="scope.row.isUsed === 0">未使用</el-tag>
            <el-tag type="info" v-else>已使用</el-tag>
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

    <el-dialog title="领取优惠券" v-model="data.receiveVisible" width="40%" :close-on-click-modal="false">
      <el-table :data="data.couponList" stripe border>
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
        <el-table-column label="操作" width="120">
          <template #default="scope">
            <el-button type="primary" @click="receiveCoupon(scope.row.id)">领取</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";

const data = reactive({
  tableData: [],
  total: 0,
  pageNum: 1,
  pageSize: 10,
  isUsed: null,
  user: JSON.parse(localStorage.getItem('system-user') || '{}'),
  receiveVisible: false,
  couponList: []
});

const load = () => {
  request.get("/userCoupon/selectPage", {
    params: {
      pageNum: data.pageNum,
      pageSize: data.pageSize,
      userId: data.user.id,
      isUsed: data.isUsed
    }
  }).then(res => {
    data.tableData = res.data.list;
    data.total = res.data.total;
  });
};

const loadValidCoupons = () => {
  request.get("/coupon/selectValidCoupons").then(res => {
    data.couponList = res.data;
  });
};

onMounted(() => {
  load();
});

const reset = () => {
  data.isUsed = null;
  load();
};

const handleReceive = () => {
  loadValidCoupons();
  data.receiveVisible = true;
};

const receiveCoupon = (couponId) => {
  request.post(`/userCoupon/receive/${data.user.id}/${couponId}`).then(res => {
    if (res.code === "200") {
      ElMessage.success("领取成功");
      data.receiveVisible = false;
      load();
    } else {
      ElMessage.error(res.msg);
    }
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
