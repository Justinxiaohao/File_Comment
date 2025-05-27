<template>
  <div>
    <div class="card" style="margin-bottom: 10px">
      <el-select
        v-model="data.filmId"
        placeholder="请选择电影"
        style="width: 200px; margin-right: 10px"
      >
        <el-option
          v-for="item in data.filmList"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        ></el-option>
      </el-select>
      <el-select
        v-model="data.hallId"
        placeholder="请选择影厅"
        style="width: 200px; margin-right: 10px"
      >
        <el-option
          v-for="item in data.hallList"
          :key="item.id"
          :label="item.name"
          :value="item.id"
        ></el-option>
      </el-select>
      <el-button type="primary" @click="load">查询</el-button>
      <el-button type="info" @click="reset">重置</el-button>
      <el-button type="success" @click="handleAdd">新增</el-button>
    </div>

    <div class="card">
      <el-table :data="data.tableData" stripe border>
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="filmName" label="电影名称"></el-table-column>
        <el-table-column prop="hallName" label="影厅名称"></el-table-column>
        <el-table-column prop="startTime" label="开始时间"></el-table-column>
        <el-table-column prop="endTime" label="结束时间"></el-table-column>
        <el-table-column prop="price" label="票价"></el-table-column>
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
      title="场次信息"
      v-model="data.formVisible"
      width="40%"
      :close-on-click-modal="false"
    >
      <el-form
        :model="data.form"
        label-width="100px"
        style="padding-right: 50px"
      >
        <el-form-item label="电影" prop="filmId">
          <el-select
            v-model="data.form.filmId"
            placeholder="请选择电影"
            style="width: 100%"
          >
            <el-option
              v-for="item in data.filmList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="影厅" prop="hallId">
          <el-select
            v-model="data.form.hallId"
            placeholder="请选择影厅"
            style="width: 100%"
          >
            <el-option
              v-for="item in data.hallList"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-date-picker
            v-model="data.form.startTime"
            type="datetime"
            placeholder="选择开始时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-date-picker
            v-model="data.form.endTime"
            type="datetime"
            placeholder="选择结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="票价" prop="price">
          <el-input-number
            v-model="data.form.price"
            :min="0"
            :precision="2"
            :step="10"
            style="width: 100%"
          />
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
import { reactive, onMounted } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";

const data = reactive({
  tableData: [],
  total: 0,
  pageNum: 1,
  pageSize: 10,
  filmId: null,
  hallId: null,
  filmList: [],
  hallList: [],
  formVisible: false,
  form: {},
});

const loadFilms = () => {
  request.get("/film/selectAll").then((res) => {
    data.filmList = res.data;
  });
};

const loadHalls = () => {
  request.get("/hall/selectAll").then((res) => {
    data.hallList = res.data;
  });
};

const load = () => {
  request
    .get("/screening/selectPage", {
      params: {
        pageNum: data.pageNum,
        pageSize: data.pageSize,
        filmId: data.filmId,
        hallId: data.hallId,
      },
    })
    .then((res) => {
      data.tableData = res.data.list;
      data.total = res.data.total;
    });
};

onMounted(() => {
  loadFilms();
  loadHalls();
  load();
});

const reset = () => {
  data.filmId = null;
  data.hallId = null;
  load();
};

const handleAdd = () => {
  data.form = {
    price: 50,
  };
  data.formVisible = true;
};

const handleEdit = (row) => {
  data.form = JSON.parse(JSON.stringify(row));
  data.formVisible = true;
};

const handleDelete = (id) => {
  request.delete("/screening/delete/" + id).then((res) => {
    if (res.code === "200") {
      ElMessage.success("删除成功");
      load();
    } else {
      ElMessage.error(res.msg);
    }
  });
};

const save = () => {
  if (!data.form.filmId) {
    ElMessage.warning("请选择电影");
    return;
  }
  if (!data.form.hallId) {
    ElMessage.warning("请选择影厅");
    return;
  }
  if (!data.form.startTime) {
    ElMessage.warning("请选择开始时间");
    return;
  }
  if (!data.form.endTime) {
    ElMessage.warning("请选择结束时间");
    return;
  }
  if (!data.form.price) {
    ElMessage.warning("请输入票价");
    return;
  }

  // 检查开始时间是否晚于结束时间
  if (new Date(data.form.startTime) >= new Date(data.form.endTime)) {
    ElMessage.warning("开始时间必须早于结束时间");
    return;
  }

  // 创建一个新对象来避免可能的引用问题
  const formData = JSON.parse(JSON.stringify(data.form));
  console.log("提交场次数据:", formData);

  // 检查同一影厅的场次时间是否重叠
  checkTimeOverlap().then((hasOverlap) => {
    if (hasOverlap) {
      ElMessage.warning("该影厅在所选时间段内已有其他场次，请选择其他时间");
      return;
    }

    // 没有时间重叠，可以保存
    request[formData.id ? "put" : "post"](
      formData.id ? "/screening/update" : "/screening/add",
      formData
    )
      .then((res) => {
        console.log("场次保存响应:", res);
        if (res.code === "200") {
          ElMessage.success("保存成功");
          data.formVisible = false;
          load();
        } else {
          ElMessage.error(res.msg || "保存失败");
        }
      })
      .catch((err) => {
        console.error("场次保存错误:", err);
        ElMessage.error("保存过程中发生错误");
      });
  });
};

// 检查时间是否重叠
const checkTimeOverlap = () => {
  return new Promise((resolve) => {
    // 获取同一影厅的所有场次
    request
      .get("/screening/selectAll", {
        params: {
          hallId: data.form.hallId,
        },
      })
      .then((res) => {
        if (res.code === "200" && res.data) {
          const screenings = res.data;
          const startTime = new Date(data.form.startTime);
          const endTime = new Date(data.form.endTime);

          // 检查是否与现有场次时间重叠
          let hasOverlap = false;

          for (const screening of screenings) {
            // 跳过当前正在编辑的场次
            if (data.form.id && screening.id === data.form.id) {
              continue;
            }

            const existingStartTime = new Date(screening.startTime);
            const existingEndTime = new Date(screening.endTime);

            // 检查时间重叠的四种情况
            // 1. 新场次开始时间在现有场次时间范围内
            // 2. 新场次结束时间在现有场次时间范围内
            // 3. 新场次完全包含现有场次
            // 4. 新场次完全被现有场次包含
            if (
              (startTime >= existingStartTime && startTime < existingEndTime) ||
              (endTime > existingStartTime && endTime <= existingEndTime) ||
              (startTime <= existingStartTime && endTime >= existingEndTime) ||
              (startTime >= existingStartTime && endTime <= existingEndTime)
            ) {
              hasOverlap = true;
              break;
            }
          }

          resolve(hasOverlap);
        } else {
          // 如果获取场次失败，默认不阻止保存
          resolve(false);
        }
      })
      .catch(() => {
        // 如果请求出错，默认不阻止保存
        resolve(false);
      });
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
