<template>
  <div>
    <div class="card" style="margin-bottom: 10px">
      <el-select
        v-model="data.hallId"
        placeholder="请选择影厅"
        style="width: 300px; margin-right: 10px"
        @change="loadHallAndSeats"
      >
        <el-option
          v-for="item in data.hallList"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        ></el-option>
      </el-select>
      <el-button type="primary" @click="loadHallAndSeats">查询</el-button>
      <el-button type="info" @click="reset">重置</el-button>
    </div>

    <!-- 影厅座位布局视图 -->
    <div v-if="data.hallId" class="card">
      <div class="hall-info">
        <h3>{{ data.currentHall.name }} - 座位管理</h3>
        <p>
          行数: {{ data.currentHall.rowCount }}, 列数:
          {{ data.currentHall.columnCount }}
        </p>
      </div>

      <div class="screen-container">
        <div class="screen">银幕</div>
      </div>

      <div class="seat-container">
        <div
          v-for="row in data.currentHall.rowCount"
          :key="'row-' + row"
          class="seat-row"
        >
          <div class="row-label">{{ row }}排</div>
          <div
            v-for="col in data.currentHall.columnCount"
            :key="'col-' + col"
            class="seat"
            :class="{
              'seat-unavailable': !isSeatAvailable(row, col),
            }"
            @click="handleSeatClick(row, col)"
          >
            {{ col }}
          </div>
        </div>
      </div>

      <div class="legend">
        <div class="legend-item">
          <div class="seat-legend-sample available"></div>
          <span>可用</span>
        </div>
        <div class="legend-item">
          <div class="seat-legend-sample unavailable"></div>
          <span>不可用</span>
        </div>
      </div>
    </div>

    <!-- 列表视图（可选） -->
    <div v-if="data.hallId && data.showListView" class="card">
      <div class="view-toggle">
        <el-button
          type="primary"
          @click="data.showListView = !data.showListView"
        >
          {{ data.showListView ? "隐藏列表视图" : "显示列表视图" }}
        </el-button>
      </div>

      <el-table :data="data.tableData" stripe border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="hallName" label="影厅名称"></el-table-column>
        <el-table-column prop="row" label="行号"></el-table-column>
        <el-table-column prop="column" label="列号"></el-table-column>
        <el-table-column prop="status" label="状态">
          <template #default="scope">
            <el-tag type="success" v-if="scope.row.status === 1">可用</el-tag>
            <el-tag type="danger" v-else>不可用</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" @click="handleEdit(scope.row)"
              >编辑</el-button
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
      title="座位信息"
      v-model="data.formVisible"
      width="40%"
      :close-on-click-modal="false"
    >
      <el-form
        :model="data.form"
        label-width="100px"
        style="padding-right: 50px"
      >
        <el-form-item label="影厅名称">
          <el-input v-model="data.form.hallName" disabled autocomplete="off" />
        </el-form-item>
        <el-form-item label="行号">
          <el-input v-model="data.form.row" disabled autocomplete="off" />
        </el-form-item>
        <el-form-item label="列号">
          <el-input v-model="data.form.column" disabled autocomplete="off" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="data.form.status">
            <el-radio :label="1">可用</el-radio>
            <el-radio :label="0">不可用</el-radio>
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
import { reactive, onMounted, computed } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";

const data = reactive({
  tableData: [],
  total: 0,
  pageNum: 1,
  pageSize: 10,
  hallId: null,
  hallList: [],
  formVisible: false,
  form: {},
  currentHall: {}, // 当前选中的影厅信息
  seats: [], // 当前影厅的所有座位
  showListView: false, // 是否显示列表视图
});

const loadHalls = () => {
  request.get("/hall/selectAll").then((res) => {
    data.hallList = res.data;
  });
};

// 加载座位列表（分页）
const load = () => {
  if (!data.hallId) return;

  request
    .get("/seat/selectPage", {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        hallId: data.hallId,
      },
    })
    .then((res) => {
      data.tableData = res.data.list;
      data.total = res.data.total;
    });
};

// 加载影厅信息和所有座位
const loadHallAndSeats = () => {
  if (!data.hallId) return;

  // 加载影厅信息
  request.get(`/hall/selectById/${data.hallId}`).then((res) => {
    if (res.code === "200") {
      data.currentHall = res.data;

      // 加载该影厅的所有座位
      loadAllSeats();
    } else {
      ElMessage.error("获取影厅信息失败");
    }
  });
};

// 加载影厅的所有座位（不分页）
const loadAllSeats = () => {
  request.get(`/seat/selectByHallId/${data.hallId}`).then((res) => {
    if (res.code === "200") {
      data.seats = res.data;
      // 同时更新列表视图数据
      load();
    } else {
      ElMessage.error("获取座位信息失败");
    }
  });
};

// 判断座位是否可用
const isSeatAvailable = (row, col) => {
  const seat = data.seats.find((s) => s.row === row && s.column === col);
  return seat && seat.status === 1;
};

// 点击座位
const handleSeatClick = (row, col) => {
  const seat = data.seats.find((s) => s.row === row && s.column === col);
  if (seat) {
    handleEdit(seat);
  } else {
    ElMessage.warning("座位数据不存在，请刷新页面");
  }
};

onMounted(() => {
  loadHalls();
});

const reset = () => {
  data.hallId = null;
  data.currentHall = {};
  data.seats = [];
  data.tableData = [];
  data.total = 0;
};

const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  data.formVisible = true;
};

const save = () => {
  request.put("/seat/update", data.form).then((res) => {
    if (res.code === "200") {
      ElMessage.success("保存成功");
      data.formVisible = false;
      // 重新加载座位数据
      loadAllSeats();
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

.hall-info {
  margin-bottom: 20px;
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

.view-toggle {
  margin-bottom: 15px;
}
</style>
