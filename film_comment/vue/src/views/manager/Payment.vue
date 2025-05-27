<template>
  <div>
    <div class="payment-container">
      <div class="payment-header">
        <h2>订单支付</h2>
      </div>

      <div class="payment-info">
        <div class="order-info">
          <h3>订单信息</h3>
          <div class="info-item">
            <span class="label">订单号：</span>
            <span class="value">{{ data.order.orderNo }}</span>
          </div>
          <div class="info-item">
            <span class="label">电影：</span>
            <span class="value">{{ data.order.filmName }}</span>
          </div>
          <div class="info-item">
            <span class="label">场次：</span>
            <span class="value">{{ data.order.screeningInfo }}</span>
          </div>
          <div class="info-item">
            <span class="label">座位：</span>
            <span class="value">{{ data.order.seatInfo }}</span>
          </div>
          <div class="info-item">
            <span class="label">总价：</span>
            <span class="value">¥{{ data.order.totalPrice }}</span>
          </div>
          <div class="info-item">
            <span class="label">折扣：</span>
            <span class="value">¥{{ data.order.discountPrice }}</span>
          </div>
          <div class="info-item">
            <span class="label">应付金额：</span>
            <span class="value price">¥{{ data.order.actualPrice }}</span>
          </div>
        </div>

        <div class="payment-methods">
          <h3>支付方式</h3>
          <div class="payment-method-list">
            <div class="payment-method-item"
                 :class="{ 'active': data.payMethod === 'alipay' }"
                 @click="data.payMethod = 'alipay'">
              <img src="@/assets/imgs/alipay.png" alt="支付宝">
              <span>支付宝</span>
            </div>
            <div class="payment-method-item"
                 :class="{ 'active': data.payMethod === 'wechat' }"
                 @click="data.payMethod = 'wechat'">
              <img src="@/assets/imgs/wechat.png" alt="微信支付">
              <span>微信支付</span>
            </div>
            <div class="payment-method-item"
                 :class="{ 'active': data.payMethod === 'card' }"
                 @click="data.payMethod = 'card'">
              <img src="@/assets/imgs/card.png" alt="银行卡">
              <span>银行卡</span>
            </div>
          </div>
        </div>

        <div class="payment-actions">
          <el-button type="primary" size="large" @click="pay">立即支付</el-button>
          <el-button size="large" @click="cancel">取消订单</el-button>
        </div>
      </div>
    </div>

    <el-dialog title="支付确认" v-model="data.payConfirmVisible" width="30%" :close-on-click-modal="false">
      <div style="text-align: center; padding: 20px;">
        <el-icon style="font-size: 50px; color: #67c23a;"><CircleCheck /></el-icon>
        <h2 style="margin-top: 20px;">支付成功</h2>
        <p>订单号：{{ data.order.orderNo }}</p>
        <p>支付金额：¥{{ data.order.actualPrice }}</p>
        <div style="margin-top: 30px;">
          <el-button type="primary" @click="goToOrders">查看订单</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import request from "@/utils/request";
import { ElMessage } from "element-plus";

const route = useRoute();
const router = useRouter();

const data = reactive({
  orderNo: route.query.orderNo,
  order: {},
  payMethod: 'alipay',
  payConfirmVisible: false
});

const loadOrder = () => {
  request.get(`/order/selectByOrderNo/${data.orderNo}`).then(res => {
    if (res.code === "200") {
      data.order = res.data;

      // 如果订单已支付或已取消，跳转到订单列表
      if (data.order.status !== 0) {
        ElMessage.warning("订单状态已变更，无需支付");
        router.push("/order");
        return;
      }

      // Load seat information if not already available
      if (!data.order.seatInfo && data.order.seatIds) {
        loadSeatInfo(data.order);
      }

      // Load screening information if not already available
      if (!data.order.screeningInfo && data.order.screeningId) {
        loadScreeningInfo(data.order);
      }
    } else {
      ElMessage.error("获取订单信息失败");
      router.push("/order");
    }
  });
};

// Function to load seat information
const loadSeatInfo = (order) => {
  if (!order.seatIds) return;

  // Split the seatIds string into an array
  const seatIdArray = order.seatIds.split(",");

  // For each seat ID, fetch the seat information
  const seatPromises = seatIdArray.map(seatId => {
    return request.get(`/seat/selectById/${seatId}`);
  });

  // Wait for all seat information to be fetched
  Promise.all(seatPromises).then(responses => {
    // Filter successful responses and extract seat data
    const seats = responses
      .filter(res => res.code === "200" && res.data)
      .map(res => res.data);

    if (seats.length > 0) {
      // Format seat information
      const seatInfo = seats
        .map(seat => `${seat.row}排${seat.column}座`)
        .join(", ");

      // Update the order object with formatted seat info
      data.order.seatInfo = seatInfo;
    }
  });
};

// Function to load screening information
const loadScreeningInfo = (order) => {
  request.get(`/screening/selectById/${order.screeningId}`).then(res => {
    if (res.code === "200" && res.data) {
      const screening = res.data;

      // Load hall name
      request.get(`/hall/selectById/${screening.hallId}`).then(hallRes => {
        if (hallRes.code === "200" && hallRes.data) {
          const hallName = hallRes.data.name;
          const startTime = formatDateTime(screening.startTime);

          // Format screening info
          data.order.screeningInfo = `${hallName} ${startTime}`;

          // Load film information
          request.get(`/film/selectById/${screening.filmId}`).then(filmRes => {
            if (filmRes.code === "200" && filmRes.data) {
              data.order.filmName = filmRes.data.name;
            }
          });
        }
      });
    }
  });
};

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return '';
  const date = new Date(dateTimeStr);
  return `${date.getFullYear()}-${(date.getMonth() + 1).toString().padStart(2, '0')}-${date.getDate().toString().padStart(2, '0')} ${date.getHours().toString().padStart(2, '0')}:${date.getMinutes().toString().padStart(2, '0')}`;
};

onMounted(() => {
  if (!data.orderNo) {
    ElMessage.error("参数错误");
    router.push("/order");
    return;
  }

  loadOrder();
});

const pay = () => {
  request.post(`/order/pay/${data.orderNo}`).then(res => {
    if (res.code === "200") {
      data.payConfirmVisible = true;
    } else {
      ElMessage.error(res.msg || "支付失败");
    }
  });
};

const cancel = () => {
  request.post(`/order/cancel/${data.orderNo}`).then(res => {
    if (res.code === "200") {
      ElMessage.success("订单已取消");
      router.push("/order");
    } else {
      ElMessage.error(res.msg || "取消订单失败");
    }
  });
};

const goToOrders = () => {
  router.push("/order");
};
</script>

<style scoped>
.payment-container {
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.payment-header {
  background-color: #409eff;
  color: white;
  padding: 20px;
  text-align: center;
}

.payment-info {
  padding: 30px;
}

.order-info {
  margin-bottom: 30px;
}

.info-item {
  display: flex;
  margin-bottom: 10px;
}

.label {
  width: 100px;
  color: #666;
}

.value {
  flex: 1;
}

.value.price {
  font-size: 20px;
  color: #f56c6c;
  font-weight: bold;
}

.payment-methods {
  margin-bottom: 30px;
}

.payment-method-list {
  display: flex;
  margin-top: 15px;
}

.payment-method-item {
  width: 120px;
  height: 80px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  margin-right: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.3s;
}

.payment-method-item img {
  width: 40px;
  height: 40px;
  margin-bottom: 5px;
}

.payment-method-item.active {
  border-color: #409eff;
  background-color: #ecf5ff;
}

.payment-actions {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

.payment-actions .el-button {
  width: 150px;
  margin: 0 10px;
}
</style>
