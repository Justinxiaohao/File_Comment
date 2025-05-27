<template>
  <div>
    <div class="card" style="margin-bottom: 10px">
      <el-input
        v-model="data.orderNo"
        placeholder="请输入订单号查询"
        style="width: 300px; margin-right: 10px"
      ></el-input>
      <el-select
        v-model="data.status"
        placeholder="订单状态"
        style="width: 200px; margin-right: 10px"
      >
        <el-option label="全部" :value="null"></el-option>
        <el-option label="未支付" :value="0"></el-option>
        <el-option label="已支付" :value="1"></el-option>
        <el-option label="已取消" :value="2"></el-option>
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" @click="reset">重置</el-button>
    </div>

    <div class="card">
      <el-table :data="data.tableData" stripe border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column
          prop="orderNo"
          label="订单号"
          width="180"
        ></el-table-column>
        <el-table-column prop="userName" label="用户名称"></el-table-column>
        <el-table-column prop="filmName" label="电影名称"></el-table-column>
        <el-table-column
          prop="screeningInfo"
          label="场次信息"
        ></el-table-column>
        <el-table-column prop="seatInfo" label="座位信息"></el-table-column>
        <el-table-column prop="totalPrice" label="总价"></el-table-column>
        <el-table-column
          prop="discountPrice"
          label="折扣金额"
        ></el-table-column>
        <el-table-column prop="actualPrice" label="实际支付"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag type="warning" v-if="scope.row.status === 0">未支付</el-tag>
            <el-tag type="success" v-if="scope.row.status === 1">已支付</el-tag>
            <el-tag type="info" v-if="scope.row.status === 2">已取消</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间"></el-table-column>
        <el-table-column prop="payTime" label="支付时间"></el-table-column>
        <el-table-column
          label="操作"
          width="200"
          v-if="data.user.role === 'USER'"
        >
          <template #default="scope">
            <el-button
              type="primary"
              @click="handlePay(scope.row)"
              v-if="scope.row.status === 0"
              >支付</el-button
            >
            <el-button
              type="danger"
              @click="handleCancel(scope.row)"
              v-if="scope.row.status === 0"
              >取消</el-button
            >
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
      title="支付订单"
      v-model="data.payVisible"
      width="40%"
      :close-on-click-modal="false"
    >
      <div style="text-align: center; padding: 20px">
        <h2>订单支付</h2>
        <p>订单号：{{ data.currentOrder.orderNo }}</p>
        <p>电影：{{ data.currentOrder.filmName }}</p>
        <p>场次：{{ data.currentOrder.screeningInfo }}</p>
        <p>座位：{{ data.currentOrder.seatInfo }}</p>
        <p>应付金额：¥{{ data.currentOrder.actualPrice }}</p>
        <div style="margin-top: 20px">
          <el-radio-group v-model="data.payMethod">
            <el-radio label="alipay">支付宝</el-radio>
            <el-radio label="wechat">微信支付</el-radio>
            <el-radio label="card">银行卡</el-radio>
          </el-radio-group>
        </div>
        <div style="margin-top: 20px">
          <el-button type="primary" @click="confirmPay">确认支付</el-button>
          <el-button @click="data.payVisible = false">取消</el-button>
        </div>
      </div>
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
  orderNo: null,
  status: null,
  user: JSON.parse(localStorage.getItem("system-user") || "{}"),
  payVisible: false,
  currentOrder: {},
  payMethod: "alipay",
});

const load = () => {
  const params = {
    pageNum: data.pageNum,
    pageSize: data.pageSize,
    orderNo: data.orderNo,
    status: data.status,
  };

  // 如果是普通用户，只查询自己的订单
  if (data.user.role === "USER") {
    params.userId = data.user.id;
  }

  request
    .get("/order/selectPage", {
      params: params,
    })
    .then((res) => {
      data.tableData = res.data.list;
      data.total = res.data.total;

      // Process each order to get seat information
      data.tableData.forEach((order) => {
        if (order.seatIds) {
          loadSeatInfo(order);
        }
      });
    });
};

const loadSeatInfo = (order) => {
  // Since there's no direct API to get seats by IDs, we'll format the seat info manually
  // The seatIds are stored as comma-separated values
  if (!order.seatIds) return;

  // Split the seatIds string into an array
  const seatIdArray = order.seatIds.split(",");

  // For each seat ID, fetch the seat information
  const seatPromises = seatIdArray.map((seatId) => {
    return request.get(`/seat/selectById/${seatId}`);
  });

  // Wait for all seat information to be fetched
  Promise.all(seatPromises).then((responses) => {
    // Filter successful responses and extract seat data
    const seats = responses
      .filter((res) => res.code === "200" && res.data)
      .map((res) => res.data);

    if (seats.length > 0) {
      // Format seat information
      const seatInfo = seats
        .map((seat) => `${seat.row}排${seat.column}座`)
        .join(", ");

      // Update the order object with formatted seat info
      order.seatInfo = seatInfo;
    }
  });
};

onMounted(() => {
  load();
});

const reset = () => {
  data.orderNo = null;
  data.status = null;
  load();
};

const handlePay = (row) => {
  data.currentOrder = row;
  data.payVisible = true;
};

const confirmPay = () => {
  request.post(`/order/pay/${data.currentOrder.orderNo}`).then((res) => {
    if (res.code === "200") {
      ElMessage.success("支付成功");
      data.payVisible = false;
      load();
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const handleCancel = (row) => {
  request.post(`/order/cancel/${row.orderNo}`).then((res) => {
    if (res.code === "200") {
      ElMessage.success("取消成功");
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
