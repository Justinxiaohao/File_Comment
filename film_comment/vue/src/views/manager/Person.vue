<template>
  <div style="width: 50%">
    <div class="card" style="padding: 30px">
      <el-form
        :model="data.user"
        label-width="100px"
        style="padding-right: 50px"
      >
        <div style="margin: 20px 0; text-align: center">
          <el-upload
            :show-file-list="false"
            class="avatar-uploader"
            :action="uploadUrl"
            :on-success="handleFileUpload"
          >
            <img
              v-if="data.user.avatar"
              :src="data.user.avatar"
              class="avatar"
            />
            <el-icon v-else class="avatar-uploader-icon"><Plus /></el-icon>
          </el-upload>
        </div>
        <el-form-item label="账号">
          <el-input disabled v-model="data.user.username" autocomplete="off" />
        </el-form-item>
        <el-form-item label="名称">
          <el-input v-model="data.user.name" autocomplete="off" />
        </el-form-item>

        <!-- 会员等级信息 -->
        <el-form-item v-if="data.user.role === 'USER'" label="会员状态">
          <el-tag v-if="data.user.isVip === 1" type="success">VIP会员</el-tag>
          <el-tag v-else type="info">普通用户</el-tag>
        </el-form-item>

        <el-form-item v-if="data.user.role === 'USER'" label="当前等级">
          <el-tag v-if="data.user.vipLevel === 0" type="info">普通用户</el-tag>
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
        </el-form-item>

        <el-form-item v-if="data.user.role === 'USER'" label="会员积分">
          <span>{{ data.user.points || 0 }} 积分</span>
        </el-form-item>

        <div style="text-align: center">
          <el-button type="primary" @click="save">保存</el-button>
        </div>
      </el-form>
    </div>

    <!-- 会员等级说明 -->
    <div
      v-if="data.user.role === 'USER'"
      class="card"
      style="padding: 30px; margin-top: 20px"
    >
      <h3>会员等级与折扣说明</h3>
      <el-divider />
      <el-table :data="levelDiscountInfo" stripe style="width: 100%">
        <el-table-column prop="level" label="会员等级">
          <template #default="scope">
            <el-tag v-if="scope.row.level === 0" type="info">{{
              scope.row.name
            }}</el-tag>
            <el-tag v-else-if="scope.row.level === 1" type="success">{{
              scope.row.name
            }}</el-tag>
            <el-tag v-else-if="scope.row.level === 2" type="warning">{{
              scope.row.name
            }}</el-tag>
            <el-tag v-else-if="scope.row.level === 3" type="danger">{{
              scope.row.name
            }}</el-tag>
            <el-tag v-else-if="scope.row.level === 4" type="primary">{{
              scope.row.name
            }}</el-tag>
            <el-tag v-else-if="scope.row.level === 5" color="#722ed1">{{
              scope.row.name
            }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="points" label="所需积分"></el-table-column>
        <el-table-column prop="discount" label="享受折扣"></el-table-column>
      </el-table>
      <div style="margin-top: 15px; color: #666; font-size: 14px">
        <p>* VIP会员购票可享受相应折扣</p>
        <p>* 积分可通过购票获得，每消费1元可获得1积分</p>
        <p>* 会员等级会根据积分自动调整</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { reactive, computed } from "vue";
import request from "@/utils/request";
import { ElMessage } from "element-plus";
import { Plus } from "@element-plus/icons-vue";

// 文件上传的接口地址
const uploadUrl = import.meta.env.VITE_BASE_URL + "/files/upload";

const data = reactive({
  user: JSON.parse(localStorage.getItem("system-user") || "{}"),
});

// 会员等级折扣信息
const levelDiscountInfo = [
  { level: 0, name: "普通用户", points: "0-999", discount: "无折扣" },
  { level: 1, name: "青铜会员", points: "1000-2999", discount: "9.5折" },
  { level: 2, name: "白银会员", points: "3000-5999", discount: "9折" },
  { level: 3, name: "黄金会员", points: "6000-9999", discount: "8.5折" },
  { level: 4, name: "钻石会员", points: "10000-19999", discount: "8折" },
  { level: 5, name: "至尊会员", points: "20000+", discount: "7.5折" },
];

// 获取当前用户的会员等级信息
const userLevelInfo = computed(() => {
  const level = data.user.vipLevel || 0;
  return levelDiscountInfo.find((item) => item.level === level);
});

const handleFileUpload = (file) => {
  data.user.avatar = file.data;
};

const emit = defineEmits(["updateUser"]);
// 把当前修改的用户信息存储到后台数据库
const save = () => {
  if (data.user.role === "ADMIN") {
    request.put("/admin/update", data.user).then((res) => {
      if (res.code === "200") {
        ElMessage.success("更新成功");
        //把更新后的用户信息存储到缓存
        localStorage.setItem("system-user", JSON.stringify(data.user));
        emit("updateUser");
      } else {
        ElMessage.error(res.msg);
      }
    });
  } else if (data.user.role === "USER") {
    request.put("/user/update", data.user).then((res) => {
      if (res.code === "200") {
        ElMessage.success("更新成功");
        //把更新后的用户信息存储到缓存
        localStorage.setItem("system-user", JSON.stringify(data.user));
        emit("updateUser");
      } else {
        ElMessage.error(res.msg);
      }
    });
  }
};
</script>

<style scoped>
.avatar-uploader .avatar {
  width: 120px;
  height: 120px;
  display: block;
}

.card {
  background-color: #fff;
  border-radius: 5px;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}
</style>

<style>
.avatar-uploader .el-upload {
  border: 1px dashed var(--el-border-color);
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
  transition: var(--el-transition-duration-fast);
}

.avatar-uploader .el-upload:hover {
  border-color: var(--el-color-primary);
}

.el-icon.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 120px;
  height: 120px;
  text-align: center;
}
</style>
