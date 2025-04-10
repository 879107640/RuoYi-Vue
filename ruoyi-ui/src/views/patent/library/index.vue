<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="指导价" prop="guidancePrice">
        <el-input
          v-model="queryParams.guidancePrice"
          placeholder="请输入指导价"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['patent:library:add']"
        >新增
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['patent:library:edit']"
        >修改
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['patent:library:remove']"
        >删除
        </el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['patent:library:export']"
        >导出
        </el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="libraryList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center"/>
      <el-table-column label="主键ID" align="center" prop="id"/>
      <el-table-column label="专利号" align="center" prop="patentNo"/>
      <el-table-column label="专利名称" align="center" prop="patentName"/>
      <el-table-column label="专利类型" align="center" prop="patentTypeKey">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.g_patent_type" :value="scope.row.patentTypeKey"/>
        </template>
      </el-table-column>
      <el-table-column label="领域" align="center" prop="domain"/>
      <el-table-column label="缴费日期" align="center" prop="feeDate" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.feeDate, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="指导价" align="center" prop="guidancePrice"/>
      <el-table-column label="资源方" align="center" prop="resourceProvider"/>
      <el-table-column label="备注" align="center" prop="remark"/>
      <el-table-column label="状态" align="center" prop="statusKey"/>
      <el-table-column label="预定人" align="center" prop="bookerKey"/>
      <el-table-column label="天数" align="center" prop="days"/>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['patent:library:edit']"
          >修改
          </el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['patent:library:remove']"
          >删除
          </el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改专利库数据对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="专利号" prop="patentNo">
          <el-input v-model="form.patentNo" placeholder="请输入专利号"/>
        </el-form-item>
        <el-form-item label="专利名称" prop="patentName">
          <el-input v-model="form.patentName" placeholder="请输入专利名称"/>
        </el-form-item>
        <el-form-item label="专利类型" prop="patentTypeKey">
          <el-select v-model="form.patentTypeKey" placeholder="请选择专利类型">
            <el-option
              v-for="dict in dict.type.g_patent_type"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="领域" prop="domain">
          <el-input v-model="form.domain" placeholder="请输入领域"/>
        </el-form-item>
        <el-form-item label="缴费日期" prop="feeDate">
          <el-date-picker clearable
                          v-model="form.feeDate"
                          type="date"
                          value-format="yyyy-MM-dd"
                          placeholder="请选择缴费日期">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="指导价" prop="guidancePrice">
          <el-input v-model="form.guidancePrice" placeholder="请输入指导价"/>
        </el-form-item>
        <el-form-item label="是否报过高企" prop="highTechReportedKey">
          <el-select v-model="form.highTechReportedKey" placeholder="请选择是否报过高企">
            <el-option
              v-for="dict in dict.type.sys_yes_no"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
            ></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="资源方" prop="resourceProvider">
          <el-input v-model="form.resourceProvider" placeholder="请输入资源方"/>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import {addLibrary, delLibrary, getLibrary, listLibrary, updateLibrary} from "@/api/patent/library";

export default {
  name: "Library",
  dicts: ['sys_yes_no', 'g_patent_type'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 专利库数据表格数据
      libraryList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        guidancePrice: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        patentNo: [
          {required: true, message: "专利号不能为空", trigger: "blur"}
        ],
        patentName: [
          {required: true, message: "专利名称不能为空", trigger: "blur"}
        ],
        patentTypeKey: [
          {required: true, message: "专利类型不能为空", trigger: "change"}
        ],
        resourceProvider: [
          {required: true, message: "资源方不能为空", trigger: "blur"}
        ],
      }
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询专利库数据列表 */
    getList() {
      this.loading = true;
      listLibrary(this.queryParams).then(response => {
        this.libraryList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        patentNo: null,
        patentName: null,
        patentTypeKey: null,
        patentTypeValue: null,
        domain: null,
        feeDate: null,
        guidancePrice: null,
        highTechReportedKey: null,
        highTechReportedValue: null,
        resourceProvider: null,
        remark: null,
        statusKey: null,
        statusValue: null,
        bookerKey: null,
        bookerValue: null,
        days: null,
        deadline: null,
        createTime: null,
        updateTime: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加专利库数据";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const id = row.id || this.ids
      getLibrary(id).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改专利库数据";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateLibrary(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addLibrary(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids;
      this.$modal.confirm('是否确认删除专利库数据编号为"' + ids + '"的数据项？').then(function () {
        return delLibrary(ids);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {
      });
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('patent/library/export', {
        ...this.queryParams
      }, `library_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
