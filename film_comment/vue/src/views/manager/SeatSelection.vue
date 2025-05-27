<template>
  <div>
    <div class="card" style="margin-bottom: 10px">
      <div style="display: flex; align-items: center">
        <div style="flex: 1">
          <h2>{{ data.film.name }}</h2>
          <p>
            场次：{{ data.screening.hallName }}
            {{ formatDateTime(data.screening.startTime) }}
          </p>
          <p>票价：¥{{ data.screening.price }}</p>
          <!-- 显示VIP折扣信息 -->
          <div v-if="isVipUser" class="vip-discount-badge">
            <el-tag type="success">VIP会员</el-tag>
            <span class="vip-level-tag">
              <el-tag v-if="data.user.vipLevel === 1" type="success"
                >青铜会员</el-tag
              >
              <el-tag v-else-if="data.user.vipLevel === 2" type="warning"
                >白银会员</el-tag
              >
              <el-tag v-else-if="data.user.vipLevel === 3" type="danger"
                >黄金会员</el-tag
              >
              <el-tag v-else-if="data.user.vipLevel === 4" type="primary"
                >钻石会员</el-tag
              >
              <el-tag v-else-if="data.user.vipLevel === 5" color="#722ed1"
                >至尊会员</el-tag
              >
            </span>
            <span class="vip-discount-text">享{{ getVipDiscount }}优惠</span>
          </div>
        </div>
        <div>
          <el-button
            type="primary"
            @click="confirmSelection"
            :disabled="data.selectedSeats.length === 0"
            >确认选座</el-button
          >
        </div>
      </div>
    </div>

    <div class="card">
      <div class="screen-container">
        <div class="screen">银幕</div>
      </div>

      <div class="seat-container">
        <div v-for="row in data.maxRow" :key="'row-' + row" class="seat-row">
          <div class="row-label">{{ row }}排</div>
          <div
            v-for="col in data.maxCol"
            :key="'col-' + col"
            class="seat"
            :class="{
              'seat-unavailable': !isSeatAvailable(row, col),
              'seat-selected': isSeatSelected(row, col),
              'seat-booked': isBooked(row, col),
            }"
            @click="toggleSeat(row, col)"
          >
            {{ col }}
          </div>
        </div>
      </div>

      <div class="legend">
        <div class="legend-item">
          <div class="seat-legend-sample available"></div>
          <span>可选</span>
        </div>
        <div class="legend-item">
          <div class="seat-legend-sample unavailable"></div>
          <span>不可选</span>
        </div>
        <div class="legend-item">
          <div class="seat-legend-sample selected"></div>
          <span>已选</span>
        </div>
        <div class="legend-item">
          <div class="seat-legend-sample booked"></div>
          <span>已售</span>
        </div>
      </div>

      <div class="selected-seats" v-if="data.selectedSeats.length > 0">
        <h3>已选座位</h3>
        <div class="selected-seat-list">
          <div
            v-for="(seat, index) in data.selectedSeats"
            :key="index"
            class="selected-seat-item"
          >
            {{ seat.row }}排{{ seat.column }}座
            <el-icon @click="removeSeat(index)"><Close /></el-icon>
          </div>
        </div>
        <div class="price-calculation">
          <div class="price-row">
            <span class="price-label">原价：</span>
            <span class="price-value">¥{{ calculateTotalPrice() }}</span>
          </div>

          <!-- 会员等级折扣 -->
          <div v-if="data.user.vipLevel > 0" class="price-row discount-row">
            <span class="price-label"
              >会员等级折扣({{ getLevelDiscount }})：</span
            >
            <span class="price-value discount-value"
              >-¥{{ calculateLevelDiscount() }}</span
            >
          </div>

          <!-- VIP额外折扣 -->
          <div
            v-if="isVipUser && data.user.vipLevel > 0"
            class="price-row discount-row vip-extra"
          >
            <span class="price-label">VIP额外折扣(9.5折)：</span>
            <span class="price-value discount-value"
              >-¥{{ calculateVipExtraDiscount() }}</span
            >
          </div>

          <div class="price-row total-row">
            <span class="price-label">预计支付：</span>
            <span class="price-value total-price"
              >¥{{ calculateFinalPrice() }}</span
            >
          </div>
          <div class="price-note">
            * 最终价格以订单确认为准，可能受优惠券等因素影响
          </div>
        </div>
      </div>
    </div>

    <el-dialog
      title="确认订单"
      v-model="data.orderVisible"
      width="40%"
      :close-on-click-modal="false"
    >
      <div style="padding: 20px">
        <h3>电影：{{ data.film.name }}</h3>
        <p>
          场次：{{ data.screening.hallName }}
          {{ formatDateTime(data.screening.startTime) }}
        </p>
        <p>座位：{{ formatSelectedSeats() }}</p>
        <!-- 价格计算详情 -->
        <div class="order-price-calculation">
          <div class="price-item">
            <span class="price-item-label">票价：</span>
            <span class="price-item-value"
              >¥{{ data.screening.price }} ×
              {{ data.selectedSeats.length }}</span
            >
            <span class="price-item-total">¥{{ calculateTotalPrice() }}</span>
          </div>

          <!-- 会员等级折扣 -->
          <div v-if="data.user.vipLevel > 0" class="price-item discount-item">
            <span class="price-item-label">会员等级折扣：</span>
            <span class="price-item-value">{{ getLevelDiscount }}</span>
            <span class="price-item-total discount-amount"
              >-¥{{ calculateLevelDiscount() }}</span
            >
          </div>

          <!-- VIP额外折扣 -->
          <div
            v-if="isVipUser && data.user.vipLevel > 0"
            class="price-item discount-item vip-extra-discount"
          >
            <span class="price-item-label">VIP额外折扣：</span>
            <span class="price-item-value">9.5折</span>
            <span class="price-item-total discount-amount"
              >-¥{{ calculateVipExtraDiscount() }}</span
            >
          </div>

          <!-- 选择的优惠券折扣 -->
          <div v-if="selectedCouponDiscount" class="price-item discount-item">
            <span class="price-item-label">优惠券折扣：</span>
            <span class="price-item-value">{{ selectedCouponDiscount }}</span>
            <span class="price-item-total discount-amount"
              >-¥{{ calculateCouponDiscount() }}</span
            >
          </div>

          <!-- 最终价格 -->
          <div class="price-item final-price">
            <span class="price-item-label">预计支付：</span>
            <span class="price-item-total final-amount"
              >¥{{ calculateOrderFinalPrice() }}</span
            >
          </div>
        </div>

        <!-- VIP会员折扣信息 -->
        <div v-if="isVipUser" class="discount-info">
          <div class="discount-tag">
            <el-tag type="success">VIP会员专享</el-tag>
          </div>
          <p>
            <span class="discount-label">会员等级：</span>
            <el-tag v-if="data.user.vipLevel === 0" type="info"
              >普通用户</el-tag
            >
            <el-tag v-else-if="data.user.vipLevel === 1" type="success"
              >青铜会员</el-tag
            >
            <el-tag v-else-if="data.user.vipLevel === 2" type="warning"
              >白银会员</el-tag
            >
            <el-tag v-else-if="data.user.vipLevel === 3" type="danger"
              >黄金会员</el-tag
            >
            <el-tag v-else-if="data.user.vipLevel === 4" type="primary"
              >钻石会员</el-tag
            >
            <el-tag v-else-if="data.user.vipLevel === 5" color="#722ed1"
              >至尊会员</el-tag
            >
          </p>
          <p>
            <span class="discount-label">会员折扣：</span>
            <span class="discount-value">{{ getVipDiscount }}</span>
          </p>
          <p class="discount-note">
            * VIP会员在会员等级折扣基础上再享9.5折优惠（折上折）
          </p>
        </div>

        <div v-if="data.coupons.length > 0">
          <h3>可用优惠券</h3>
          <el-select
            v-model="data.selectedCoupon"
            placeholder="请选择优惠券"
            style="width: 100%"
          >
            <el-option label="不使用优惠券" :value="null"></el-option>
            <el-option
              v-for="coupon in data.coupons"
              :key="coupon.id"
              :label="
                coupon.couponName + ' (' + formatDiscount(coupon.discount) + ')'
              "
              :value="coupon.couponId"
            ></el-option>
          </el-select>
        </div>

        <div style="margin-top: 20px; text-align: right">
          <el-button @click="data.orderVisible = false">取消</el-button>
          <el-button type="primary" @click="createOrder">提交订单</el-button>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup>
import { reactive, onMounted, computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { Close } from "@element-plus/icons-vue";

const route = useRoute();
const router = useRouter();

const data = reactive({
  screeningId: parseInt(route.query.screeningId),
  film: {},
  screening: {},
  seats: [],
  maxRow: 0,
  maxCol: 0,
  selectedSeats: [],
  bookedSeats: [], // Add array to track already booked seats
  user: JSON.parse(localStorage.getItem("system-user") || "{}"),
  orderVisible: false,
  coupons: [],
  selectedCoupon: null,
});

const loadScreening = async () => {
  try {
    const res = await request.get(`/screening/selectById/${data.screeningId}`);
    if (res.code === "200") {
      data.screening = res.data;
      // Load hall name first to ensure it's available
      await loadHallName(data.screening.hallId);
      // Then load other data
      loadFilm(data.screening.filmId);
      loadSeats(data.screening.hallId);
    } else {
      ElMessage.error("获取场次信息失败");
    }
  } catch (error) {
    ElMessage.error("获取场次信息失败");
  }
};

const loadHallName = (hallId) => {
  return new Promise((resolve) => {
    request
      .get(`/hall/selectById/${hallId}`)
      .then((res) => {
        if (res.code === "200") {
          // Add hall name to screening object
          data.screening.hallName = res.data.name;
          // 保存影厅行列数信息
          data.screening.hallRowCount = res.data.rowCount;
          data.screening.hallColumnCount = res.data.columnCount;
          resolve();
        } else {
          ElMessage.error("获取影厅信息失败");
          resolve();
        }
      })
      .catch(() => {
        resolve();
      });
  });
};

const loadFilm = (filmId) => {
  request.get(`/film/selectById/${filmId}`).then((res) => {
    if (res.code === "200") {
      data.film = res.data;
    } else {
      ElMessage.error("获取电影信息失败");
    }
  });
};

const loadSeats = (hallId) => {
  request.get(`/seat/selectByHallId/${hallId}`).then((res) => {
    if (res.code === "200") {
      data.seats = res.data;

      // 使用影厅的行列数信息，而不是从座位计算
      if (data.screening.hallRowCount && data.screening.hallColumnCount) {
        data.maxRow = data.screening.hallRowCount;
        data.maxCol = data.screening.hallColumnCount;
      } else {
        // 如果没有影厅行列数信息，则从座位计算（兼容旧数据）
        data.maxRow = Math.max(...data.seats.map((seat) => seat.row));
        data.maxCol = Math.max(...data.seats.map((seat) => seat.column));
      }

      console.log(`影厅行数: ${data.maxRow}, 列数: ${data.maxCol}`);

      // Load booked seats for this screening
      loadBookedSeats();
    } else {
      ElMessage.error("获取座位信息失败");
    }
  });
};

// Function to load already booked seats for this screening
const loadBookedSeats = () => {
  // 直接调用后端API获取该场次的所有订单
  request
    .get("/order/selectAll", {
      params: {
        screeningId: data.screeningId,
      },
    })
    .then((res) => {
      if (res.code === "200" && res.data) {
        // 提取所有已订座位的ID
        const bookedSeatIds = [];

        // 遍历所有订单（包括未支付和已支付的）
        res.data.forEach((order) => {
          // 只处理未取消的订单（状态0-未支付，1-已支付）
          if ((order.status === 0 || order.status === 1) && order.seatIds) {
            const seatIdsArray = order.seatIds.split(",");
            // 将字符串ID转换为数字并添加到已订座位列表
            seatIdsArray.forEach((id) => {
              bookedSeatIds.push(parseInt(id));
            });
          }
        });

        // 更新已订座位数组
        data.bookedSeats = bookedSeatIds;
        console.log("已订座位:", data.bookedSeats);
      }
    })
    .catch((error) => {
      console.error("获取已订座位失败:", error);
    });
};

const loadUserCoupons = () => {
  request
    .get(`/userCoupon/selectUnusedByUserId/${data.user.id}`)
    .then((res) => {
      if (res.code === "200") {
        data.coupons = res.data;
        console.log("用户优惠券:", data.coupons);
      }
    });
};

onMounted(() => {
  if (!data.screeningId) {
    ElMessage.error("参数错误");
    router.push("/filmView");
    return;
  }

  loadScreening();
  loadUserCoupons();
});

const isSeatAvailable = (row, col) => {
  // 检查座位是否在数据库中存在
  const seat = data.seats.find((s) => s.row === row && s.column === col);

  // 如果座位不存在于数据库中，但在影厅行列范围内，则认为是可用的
  // 这是为了处理影厅行列数增加后，新增的座位默认可用的情况
  if (!seat) {
    // 检查是否在影厅行列范围内
    if (row > 0 && row <= data.maxRow && col > 0 && col <= data.maxCol) {
      // 在范围内但数据库中不存在，可能是新增的座位，默认可用
      return true;
    }
    return false;
  }

  // 检查座位状态是否可用
  if (seat.status !== 1) {
    return false;
  }

  // 检查座位是否已被预订
  return !data.bookedSeats.includes(seat.id);
};

const isSeatSelected = (row, col) => {
  return data.selectedSeats.some((s) => s.row === row && s.column === col);
};

const isBooked = (row, col) => {
  const seat = data.seats.find((s) => s.row === row && s.column === col);
  if (!seat) return false;

  // Check if the seat ID is in the bookedSeats array
  return data.bookedSeats.includes(seat.id);
};

const toggleSeat = (row, col) => {
  // 获取座位信息
  let seat = data.seats.find((s) => s.row === row && s.column === col);

  // 如果座位不存在于数据库中，但在影厅行列范围内，则创建一个临时座位对象
  if (!seat && row > 0 && row <= data.maxRow && col > 0 && col <= data.maxCol) {
    // 创建一个临时座位对象，id设为null，表示这是一个新座位
    seat = {
      id: null,
      row: row,
      column: col,
      hallId: data.screening.hallId,
      status: 1, // 默认可用
    };
  }

  // 如果座位不存在或超出范围，则不处理
  if (!seat) return;

  // 检查座位是否已被预订
  if (seat.id && data.bookedSeats.includes(seat.id)) {
    ElMessage.warning("该座位已被预订，请选择其他座位");
    return;
  }

  // 检查座位是否可用
  if (seat.id && seat.status !== 1) {
    ElMessage.warning("该座位不可用，请选择其他座位");
    return;
  }

  // 处理座位选择/取消选择
  const seatIndex = data.selectedSeats.findIndex(
    (s) => s.row === row && s.column === col
  );
  if (seatIndex >= 0) {
    // 已选中，取消选择
    data.selectedSeats.splice(seatIndex, 1);
  } else {
    // 未选中，添加选择
    data.selectedSeats.push({
      id: seat.id, // 可能为null，表示新座位
      row: row,
      column: col,
      isNew: !seat.id, // 标记是否为新座位
    });
  }
};

const removeSeat = (index) => {
  data.selectedSeats.splice(index, 1);
};

const calculateTotalPrice = () => {
  return (data.screening.price * data.selectedSeats.length).toFixed(2);
};

// 计算会员等级折扣金额
const calculateLevelDiscount = () => {
  const totalPrice = parseFloat(calculateTotalPrice());
  const vipLevel = data.user.vipLevel || 0;
  let discountRate = 0;

  switch (vipLevel) {
    case 1:
      discountRate = 0.05;
      break; // 9.5折 = 5%折扣
    case 2:
      discountRate = 0.1;
      break; // 9折 = 10%折扣
    case 3:
      discountRate = 0.15;
      break; // 8.5折 = 15%折扣
    case 4:
      discountRate = 0.2;
      break; // 8折 = 20%折扣
    case 5:
      discountRate = 0.25;
      break; // 7.5折 = 25%折扣
    default:
      discountRate = 0;
  }

  return (totalPrice * discountRate).toFixed(2);
};

// 计算VIP额外折扣金额（在会员等级折扣基础上再打95折）
const calculateVipExtraDiscount = () => {
  if (!isVipUser.value) return "0.00";

  // 先计算会员等级折扣后的价格
  const totalPrice = parseFloat(calculateTotalPrice());
  const levelDiscount = parseFloat(calculateLevelDiscount());
  const priceAfterLevelDiscount = totalPrice - levelDiscount;

  // VIP额外折扣率为5%
  const vipExtraDiscountRate = 0.05;

  return (priceAfterLevelDiscount * vipExtraDiscountRate).toFixed(2);
};

// 计算VIP折扣总金额（会员等级折扣 + VIP额外折扣）
const calculateVipDiscount = () => {
  if (!isVipUser.value) return "0.00";

  const levelDiscount = parseFloat(calculateLevelDiscount());
  const vipExtraDiscount = parseFloat(calculateVipExtraDiscount());

  return (levelDiscount + vipExtraDiscount).toFixed(2);
};

// 计算最终价格（含VIP折扣）
const calculateFinalPrice = () => {
  const totalPrice = parseFloat(calculateTotalPrice());
  const levelDiscount = parseFloat(calculateLevelDiscount());
  const vipExtraDiscount = isVipUser.value
    ? parseFloat(calculateVipExtraDiscount())
    : 0;

  return (totalPrice - levelDiscount - vipExtraDiscount).toFixed(2);
};

// 获取选中的优惠券折扣信息
const selectedCouponDiscount = computed(() => {
  if (!data.selectedCoupon) return null;

  const selectedCoupon = data.coupons.find(
    (c) => c.couponId === data.selectedCoupon
  );
  if (!selectedCoupon) return null;

  return formatDiscount(selectedCoupon.discount);
});

// 计算优惠券折扣金额
const calculateCouponDiscount = () => {
  if (!data.selectedCoupon) return "0.00";

  const selectedCoupon = data.coupons.find(
    (c) => c.couponId === data.selectedCoupon
  );
  if (!selectedCoupon) return "0.00";

  // 在会员等级折扣和VIP额外折扣基础上再计算优惠券折扣
  const priceAfterAllDiscounts = parseFloat(calculateFinalPrice());
  const couponDiscountRate = 1 - parseFloat(selectedCoupon.discount);

  return (priceAfterAllDiscounts * couponDiscountRate).toFixed(2);
};

// 计算订单最终价格（含会员等级折扣、VIP额外折扣和优惠券折扣）
const calculateOrderFinalPrice = () => {
  const priceAfterAllDiscounts = parseFloat(calculateFinalPrice());
  const couponDiscount = parseFloat(calculateCouponDiscount());

  return (priceAfterAllDiscounts - couponDiscount).toFixed(2);
};

const formatDateTime = (dateTimeStr) => {
  if (!dateTimeStr) return "";
  const date = new Date(dateTimeStr);
  return `${date.getFullYear()}-${(date.getMonth() + 1)
    .toString()
    .padStart(2, "0")}-${date.getDate().toString().padStart(2, "0")} ${date
    .getHours()
    .toString()
    .padStart(2, "0")}:${date.getMinutes().toString().padStart(2, "0")}`;
};

const formatSelectedSeats = () => {
  return data.selectedSeats
    .map((seat) => `${seat.row}排${seat.column}座`)
    .join(", ");
};

// 判断是否是VIP用户
const isVipUser = computed(() => {
  return data.user && data.user.isVip === 1;
});

// 获取会员等级折扣
const getLevelDiscount = computed(() => {
  const vipLevel = data.user.vipLevel || 0;
  switch (vipLevel) {
    case 1:
      return "9.5折";
    case 2:
      return "9折";
    case 3:
      return "8.5折";
    case 4:
      return "8折";
    case 5:
      return "7.5折";
    default:
      return "无折扣";
  }
});

// 获取VIP额外折扣
const getVipExtraDiscount = computed(() => {
  if (!isVipUser.value) return "无折扣";
  return "9.5折";
});

// 获取VIP会员折扣（显示用）
const getVipDiscount = computed(() => {
  if (!isVipUser.value) return "无折扣";

  // 如果有会员等级，显示"等级折扣+VIP额外9.5折"
  if (data.user.vipLevel > 0) {
    return `${getLevelDiscount.value}+额外9.5折`;
  }

  return "9.5折";
});

const formatDiscount = (discount) => {
  if (!discount) return "无折扣";
  // Try to convert to number and format as percentage
  const discountNum = parseFloat(discount);
  if (isNaN(discountNum)) return discount;
  return (discountNum * 10).toFixed(1) + "折";
};

const confirmSelection = () => {
  if (data.selectedSeats.length === 0) {
    ElMessage.warning("请至少选择一个座位");
    return;
  }

  data.orderVisible = true;
};

const createOrder = () => {
  // 检查是否有新座位需要先创建
  const hasNewSeats = data.selectedSeats.some((seat) => seat.isNew);

  if (hasNewSeats) {
    // 先创建新座位，然后再创建订单
    createNewSeatsAndOrder();
  } else {
    // 直接创建订单
    submitOrder();
  }
};

// 创建新座位
const createNewSeatsAndOrder = () => {
  // 获取所有需要创建的新座位
  const newSeats = data.selectedSeats.filter((seat) => seat.isNew);
  const createPromises = [];

  // 为每个新座位创建一个Promise
  newSeats.forEach((seat) => {
    const seatData = {
      row: seat.row,
      column: seat.column,
      hallId: data.screening.hallId,
      status: 1, // 默认可用
    };

    const promise = request.post("/seat/add", seatData);
    createPromises.push(promise);
  });

  // 等待所有座位创建完成
  Promise.all(createPromises)
    .then(() => {
      // 重新加载座位数据
      return new Promise((resolve) => {
        request
          .get(`/seat/selectByHallId/${data.screening.hallId}`)
          .then((res) => {
            if (res.code === "200") {
              data.seats = res.data;
              resolve();
            } else {
              ElMessage.error("获取座位信息失败");
              resolve();
            }
          });
      });
    })
    .then(() => {
      // 更新选中座位的ID
      data.selectedSeats.forEach((selectedSeat) => {
        if (selectedSeat.isNew) {
          const dbSeat = data.seats.find(
            (s) =>
              s.row === selectedSeat.row && s.column === selectedSeat.column
          );
          if (dbSeat) {
            selectedSeat.id = dbSeat.id;
            selectedSeat.isNew = false;
          }
        }
      });

      // 创建订单
      submitOrder();
    })
    .catch((err) => {
      console.error("创建座位失败:", err);
      ElMessage.error("创建座位失败，请重试");
    });
};

// 提交订单
const submitOrder = () => {
  const seatIds = data.selectedSeats.map((seat) => seat.id).join(",");

  const order = {
    userId: data.user.id,
    screeningId: data.screeningId,
    seatIds: seatIds,
    couponId: data.selectedCoupon,
  };

  request.post("/order/add", order).then((res) => {
    if (res.code === "200") {
      ElMessage.success("订单创建成功");
      data.orderVisible = false;
      router.push({
        path: "/payment",
        query: {
          orderNo: res.data.orderNo,
        },
      });
    } else {
      ElMessage.error(res.msg || "订单创建失败");
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

.screen-container {
  display: flex;
  justify-content: center;
  margin-bottom: 30px;
}

.screen {
  width: 80%;
  height: 30px;
  background-color: #f0f0f0;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 5px;
  box-shadow: 0 5px 10px rgba(0, 0, 0, 0.1);
}

.seat-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  margin-bottom: 20px;
}

.seat-row {
  display: flex;
  margin-bottom: 10px;
  align-items: center;
}

.row-label {
  width: 40px;
  text-align: center;
  font-weight: bold;
}

.seat {
  width: 30px;
  height: 30px;
  margin: 0 5px;
  background-color: #e1f3d8;
  border: 1px solid #67c23a;
  border-radius: 5px;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  font-size: 12px;
}

.seat-unavailable {
  background-color: #f0f0f0;
  border-color: #dcdcdc;
  cursor: not-allowed;
  color: #999;
}

.seat-selected {
  background-color: #f64848;
  border-color: #f64848;
  color: white;
}

.seat-booked {
  background-color: #909399;
  border-color: #909399;
  cursor: not-allowed;
  color: white;
}

.legend {
  display: flex;
  justify-content: center;
  margin-bottom: 20px;
}

.legend-item {
  display: flex;
  align-items: center;
  margin: 0 10px;
}

.seat-legend-sample {
  width: 20px;
  height: 20px;
  margin-right: 5px;
  border-radius: 3px;
}

.seat-legend-sample.available {
  background-color: #e1f3d8;
  border: 1px solid #67c23a;
}

.seat-legend-sample.unavailable {
  background-color: #f0f0f0;
  border: 1px solid #dcdcdc;
}

.seat-legend-sample.selected {
  background-color: #f64848;
  border: 1px solid #f64848;
}

.seat-legend-sample.booked {
  background-color: #909399;
  border: 1px solid #909399;
}

.selected-seats {
  border-top: 1px solid #eee;
  padding-top: 20px;
}

.selected-seat-list {
  display: flex;
  flex-wrap: wrap;
  margin-bottom: 10px;
}

.selected-seat-item {
  background-color: #f0f0f0;
  padding: 5px 10px;
  border-radius: 15px;
  margin-right: 10px;
  margin-bottom: 10px;
  display: flex;
  align-items: center;
}

.selected-seat-item .el-icon {
  margin-left: 5px;
  cursor: pointer;
}

.total-price {
  font-size: 18px;
  font-weight: bold;
  text-align: right;
}

/* VIP折扣信息样式 */
.discount-info {
  background-color: #f8f8f8;
  border-radius: 5px;
  padding: 15px;
  margin: 15px 0;
  border-left: 4px solid #67c23a;
}

.discount-tag {
  margin-bottom: 10px;
}

.discount-label {
  font-weight: bold;
  margin-right: 10px;
  display: inline-block;
  width: 80px;
}

.discount-value {
  color: #f56c6c;
  font-weight: bold;
}

.discount-note {
  color: #909399;
  font-size: 12px;
  margin-top: 10px;
  font-style: italic;
}

/* VIP折扣标签样式 */
.vip-discount-badge {
  margin-top: 5px;
  display: flex;
  align-items: center;
  flex-wrap: wrap;
}

.vip-level-tag {
  margin: 0 5px;
}

.vip-discount-text {
  color: #f56c6c;
  font-weight: bold;
}

/* 价格计算样式 */
.price-calculation {
  margin-top: 15px;
  border-top: 1px dashed #ddd;
  padding-top: 15px;
}

.price-row {
  display: flex;
  justify-content: space-between;
  margin-bottom: 8px;
}

.price-label {
  color: #606266;
}

.price-value {
  font-weight: bold;
}

.discount-row .price-value {
  color: #f56c6c;
}

.total-row {
  margin-top: 10px;
  border-top: 1px solid #eee;
  padding-top: 10px;
}

.total-price {
  font-size: 18px;
  color: #f56c6c;
  font-weight: bold;
}

.price-note {
  font-size: 12px;
  color: #909399;
  margin-top: 5px;
  font-style: italic;
}

/* 订单价格计算样式 */
.order-price-calculation {
  background-color: #f8f8f8;
  border-radius: 5px;
  padding: 15px;
  margin: 15px 0;
}

.price-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  line-height: 24px;
}

.price-item-label {
  flex: 1;
  color: #606266;
}

.price-item-value {
  flex: 2;
  text-align: right;
  padding-right: 15px;
}

.price-item-total {
  flex: 1;
  text-align: right;
  font-weight: bold;
}

.discount-item {
  color: #67c23a;
}

.discount-amount {
  color: #f56c6c;
}

.final-price {
  margin-top: 10px;
  border-top: 1px solid #ddd;
  padding-top: 10px;
  font-weight: bold;
}

.final-amount {
  color: #f56c6c;
  font-size: 18px;
}

/* VIP额外折扣样式 */
.vip-extra-discount {
  background-color: #f0f9eb;
  border-left: 3px solid #67c23a;
  padding-left: 10px;
}

.price-row.vip-extra {
  background-color: #f0f9eb;
  border-radius: 4px;
  padding: 5px;
  margin: 5px 0;
}
</style>
