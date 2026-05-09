<template>
  <div class="global-search">
    <section class="search-band" :class="{ floating: overlayOpen }">
      <div class="search-shell">
        <div class="search-row">
          <div class="search-box">
            <span class="search-icon">⌕</span>
            <input
              v-model.trim="keyword"
              class="search-input"
              placeholder="输入工单号、样本号、项目编号、物种、申请人"
              @focus="openOverlay"
              @keyup.enter="search"
            />
            <button v-if="keyword" class="clear-button" type="button" @click="clearSearch">×</button>
          </div>
          <button class="primary-button" type="button" @click="search">搜索</button>
        </div>

        <div v-if="searched && businessTabs.length > 1" class="tabs">
          <button
            v-for="item in businessTabs"
            :key="item.code"
            class="tab"
            :class="{ active: activeBusiness === item.code }"
            type="button"
            @click="activeBusiness = item.code"
          >
            {{ item.name }}
          </button>
        </div>
      </div>
    </section>

    <div v-if="overlayOpen" class="scrim" @click="closeOverlay"></div>

    <main v-if="searched" class="main" :class="{ floating: overlayOpen }">
      <aside class="filters">
        <section class="filter-section">
          <h2 class="section-title">业务</h2>
          <label v-for="item in availableBusinesses" :key="item.code" class="check-row">
            <input v-model="selectedBusinesses" type="checkbox" :value="item.code" />
            {{ item.name }}
          </label>
        </section>
        <section class="filter-section">
          <h2 class="section-title">时间</h2>
          <select v-model="dateFilter" class="select" @change="search">
            <option value="all">全部时间</option>
            <option value="today">今天</option>
            <option value="week">近 7 天</option>
            <option value="month">近 30 天</option>
          </select>
        </section>
      </aside>

      <section class="result-panel">
        <div class="meta-line">
          <span>{{ total }} 条结果</span>
          <span>{{ lastCost }} ms</span>
        </div>

        <div class="result-list">
          <div v-if="loading && !results.length" class="empty">正在查询...</div>
          <div v-else-if="errorMessage" class="empty">{{ errorMessage }}</div>
          <div v-else-if="!visibleResults.length" class="empty">未找到匹配数据</div>

          <template v-else>
            <article
              v-for="(row, index) in visibleResults"
              :key="rowKey(row, index)"
              class="result-card"
              :class="{ active: selectedIndex === index }"
              @click="selectedIndex = index"
            >
              <div class="result-main">
                <div class="title-row">
                  <span class="badge" :class="row.business_code">{{ businessName(row.business_code) }}</span>
                  <h3 class="result-title" v-html="highlight(row.title || row.biz_id || '')"></h3>
                </div>
                <p class="summary" v-html="highlight(row.summary || '')"></p>
                <div class="fields">
                  <span class="field">ID：{{ row.biz_id }}</span>
                  <span class="field">表：{{ row.table_name }}</span>
                  <span v-for="field in displayFields(row)" :key="field.key" class="field">
                    {{ field.key }}：{{ field.value }}
                  </span>
                </div>
              </div>
              <button class="result-action" type="button" title="打开" @click.stop="$emit('open', row)">↗</button>
            </article>
          </template>
        </div>

        <div v-if="hasNext" class="pager">
          <button class="load-more" type="button" :disabled="loadingMore" @click="loadMore">
            {{ loadingMore ? '加载中...' : '加载更多' }}
          </button>
        </div>
      </section>

      <aside v-if="selectedRow" class="preview">
        <div class="preview-head">
          <h2 class="preview-title">{{ selectedRow.title }}</h2>
          <p class="preview-subtitle">
            {{ businessName(selectedRow.business_code) }} / {{ selectedRow.table_name }}
          </p>
        </div>
        <section class="preview-section">
          <h3 class="section-title">字段</h3>
          <div class="kv">
            <div v-for="field in previewFields" :key="field.key" class="kv-row">
              <div class="kv-key">{{ field.key }}</div>
              <div class="kv-value" :class="statusClass(field.key, field.value)">{{ field.value }}</div>
            </div>
          </div>
        </section>
        <section class="preview-section">
          <h3 class="section-title">定位</h3>
          <div class="kv">
            <div class="kv-row"><div class="kv-key">业务 ID</div><div class="kv-value">{{ selectedRow.biz_id }}</div></div>
            <div class="kv-row"><div class="kv-key">路由</div><div class="kv-value">{{ selectedRow.route }}</div></div>
            <div class="kv-row"><div class="kv-key">时间</div><div class="kv-value">{{ selectedRow.create_time }}</div></div>
          </div>
        </section>
      </aside>
    </main>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'

const props = defineProps({
  apiUrl: {
    type: String,
    default: 'http://localhost:8093/es/search/listPage'
  },
  systemCode: {
    type: String,
    default: 'drqi'
  },
  pageSize: {
    type: Number,
    default: 20
  },
  headers: {
    type: Object,
    default: () => ({
      userId: '1',
      username: 'zoujun',
      nickname: 'zoujun',
      jobNum: '1'
    })
  }
})

defineEmits(['open'])

const businessMap = ref({
  all: '全部',
  task: '任务',
  biotest: '检测',
  project: '项目',
  plant: '植物',
  seed: '种子'
})

const keyword = ref('')
const activeBusiness = ref('all')
const selectedBusinesses = ref([])
const dateFilter = ref('all')
const results = ref([])
const total = ref(0)
const nextSearchAfter = ref(null)
const hasNext = ref(false)
const loading = ref(false)
const loadingMore = ref(false)
const searched = ref(false)
const overlayOpen = ref(false)
const selectedIndex = ref(0)
const lastCost = ref(0)
const errorMessage = ref('')

const availableBusinesses = computed(() => {
  const seen = new Set()
  results.value.forEach(row => {
    if (row.business_code) {
      seen.add(row.business_code)
      if (!businessMap.value[row.business_code]) {
        businessMap.value[row.business_code] = row.business_code
      }
    }
  })
  return Array.from(seen).map(code => ({ code, name: businessName(code) }))
})

const businessTabs = computed(() => [{ code: 'all', name: '全部' }, ...availableBusinesses.value])

const visibleResults = computed(() => {
  if (activeBusiness.value === 'all') {
    return results.value
  }
  return results.value.filter(row => row.business_code === activeBusiness.value)
})

const selectedRow = computed(() => visibleResults.value[selectedIndex.value])

const previewFields = computed(() => {
  const display = selectedRow.value?.display || {}
  return Object.keys(display).map(key => ({ key, value: display[key] }))
})

function openOverlay() {
  overlayOpen.value = true
}

function closeOverlay() {
  overlayOpen.value = false
  results.value = []
  total.value = 0
  nextSearchAfter.value = null
  hasNext.value = false
  loading.value = false
  loadingMore.value = false
  selectedIndex.value = 0
  searched.value = false
  errorMessage.value = ''
}

function clearSearch() {
  keyword.value = ''
  closeOverlay()
}

async function search() {
  openOverlay()
  if (!keyword.value) {
    closeOverlay()
    return
  }
  searched.value = true
  loading.value = true
  errorMessage.value = ''
  nextSearchAfter.value = null
  hasNext.value = false
  const started = Date.now()
  try {
    const data = await requestPage()
    results.value = normalizeRecords(data)
    total.value = typeof data.total === 'number' ? data.total : results.value.length
    nextSearchAfter.value = data.nextSearchAfter || null
    hasNext.value = !!data.hasNext
    activeBusiness.value = 'all'
    selectedBusinesses.value = availableBusinesses.value.map(item => item.code)
    selectedIndex.value = 0
  } catch (error) {
    results.value = []
    total.value = 0
    errorMessage.value = `接口请求失败：${error.message}`
  } finally {
    lastCost.value = Date.now() - started
    loading.value = false
  }
}

async function loadMore() {
  if (!hasNext.value || loadingMore.value) {
    return
  }
  loadingMore.value = true
  const started = Date.now()
  try {
    const data = await requestPage(nextSearchAfter.value)
    results.value = results.value.concat(normalizeRecords(data))
    total.value = typeof data.total === 'number' ? data.total : results.value.length
    nextSearchAfter.value = data.nextSearchAfter || null
    hasNext.value = !!data.hasNext
  } catch (error) {
    errorMessage.value = `接口请求失败：${error.message}`
  } finally {
    lastCost.value = Date.now() - started
    loadingMore.value = false
  }
}

async function requestPage(searchAfter) {
  const body = {
    systemCode: props.systemCode,
    keyword: keyword.value,
    businessCodes: selectedBusinesses.value.length ? selectedBusinesses.value : undefined,
    pageSize: props.pageSize
  }
  if (searchAfter) {
    body.searchAfter = searchAfter
  }
  const response = await fetch(props.apiUrl, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
      ...props.headers
    },
    body: JSON.stringify(body)
  })
  if (!response.ok) {
    throw new Error(`HTTP ${response.status}`)
  }
  const payload = await response.json()
  return payload?.data || payload
}

function normalizeRecords(data) {
  const records = Array.isArray(data?.records) ? data.records : []
  return records.map(item => item.source || item)
}

function businessName(code) {
  return businessMap.value[code] || code || ''
}

function rowKey(row, index) {
  return `${row.system_code || ''}_${row.business_code || ''}_${row.table_name || ''}_${row.biz_id || index}`
}

function displayFields(row) {
  const display = row.display || {}
  return Object.keys(display).slice(0, 4).map(key => ({ key, value: display[key] }))
}

function highlight(value) {
  const text = escapeHtml(String(value || ''))
  if (!keyword.value) {
    return text
  }
  const pattern = keyword.value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
  return text.replace(new RegExp(pattern, 'gi'), match => `<mark>${match}</mark>`)
}

function statusClass(key, value) {
  if (!isStatusKey(key)) {
    return ''
  }
  if (value === '未同步') {
    return 'error'
  }
  if (value === '审批中' || value === '待检测') {
    return 'pending'
  }
  return 'status'
}

function isStatusKey(key) {
  return key === '状态' || key === '结果' || /状态$/.test(key) || /结果$/.test(key)
}

function escapeHtml(value) {
  return value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;')
}
</script>

<style scoped>
.global-search {
  color: #1f2933;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Microsoft YaHei", Arial, sans-serif;
}

button,
input,
select {
  font: inherit;
}

button {
  cursor: pointer;
}

.search-band {
  padding: 16px 0;
}

.search-band.floating {
  position: fixed;
  z-index: 1001;
  left: 50%;
  top: 24px;
  width: min(1180px, calc(100vw - 48px));
  padding: 0;
  transform: translateX(-50%);
}

.search-shell {
  width: 100%;
}

.search-band.floating .search-shell {
  padding: 0;
  border-radius: 8px;
  background: transparent;
  box-shadow: none;
}

.search-row {
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 10px;
}

.search-box {
  height: 48px;
  display: grid;
  grid-template-columns: 46px 1fr auto;
  align-items: center;
  border: 1px solid #cfd7e3;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 10px 24px rgba(31, 41, 51, 0.08);
}

.search-icon {
  color: #657282;
  text-align: center;
  font-size: 22px;
}

.search-input {
  min-width: 0;
  border: 0;
  outline: 0;
  font-size: 16px;
}

.clear-button {
  width: 36px;
  height: 36px;
  margin-right: 6px;
  border: 0;
  background: transparent;
  color: #657282;
  font-size: 22px;
}

.primary-button {
  height: 48px;
  min-width: 92px;
  border: 0;
  border-radius: 8px;
  color: #fff;
  background: #246bfe;
  font-weight: 650;
}

.tabs {
  display: flex;
  gap: 6px;
  padding-top: 14px;
  overflow-x: auto;
}

.tab {
  height: 34px;
  padding: 0 12px;
  border: 1px solid transparent;
  border-radius: 7px;
  background: transparent;
  color: #657282;
  white-space: nowrap;
}

.tab.active {
  border-color: #b9c8df;
  background: #fff;
  color: #246bfe;
  font-weight: 650;
}

.scrim {
  position: fixed;
  inset: 0;
  z-index: 1000;
  background: rgba(15, 23, 42, 0.82);
  backdrop-filter: blur(4px);
}

.main {
  display: grid;
  grid-template-columns: 244px minmax(0, 1fr) 292px;
  gap: 16px;
}

.main.floating {
  position: fixed;
  z-index: 1001;
  left: 50%;
  top: 142px;
  width: min(1180px, calc(100vw - 48px));
  height: calc(100vh - 166px);
  transform: translateX(-50%);
  overflow: hidden;
}

.filters,
.preview {
  align-self: start;
  border: 1px solid rgba(255, 255, 255, 0.3);
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.78);
  backdrop-filter: blur(12px);
  box-shadow: 0 18px 44px rgba(15, 23, 42, 0.18);
}

.filter-section,
.preview-section {
  padding: 14px;
  border-bottom: 1px solid #dde2ea;
}

.filter-section:last-child,
.preview-section:last-child {
  border-bottom: 0;
}

.section-title {
  margin: 0 0 10px;
  font-size: 13px;
  color: #657282;
  font-weight: 700;
}

.check-row {
  min-height: 32px;
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 14px;
}

.select {
  height: 38px;
  width: 100%;
  border: 1px solid #cfd7e3;
  border-radius: 8px;
  padding: 0 12px;
  background: #fff;
}

.result-panel {
  min-width: 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
}

.meta-line {
  height: 32px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #657282;
  font-size: 13px;
  flex: 0 0 auto;
}

.result-list {
  display: grid;
  gap: 10px;
  min-height: 0;
  overflow-y: auto;
  padding-right: 4px;
  overscroll-behavior: contain;
  flex: 1 1 auto;
}

.result-card {
  border: 1px solid #dde2ea;
  border-radius: 8px;
  background: #fff;
  padding: 14px;
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 12px;
  box-shadow: 0 4px 14px rgba(31, 41, 51, 0.04);
}

.result-card:hover,
.result-card.active {
  border-color: #9bb8f2;
  box-shadow: 0 8px 24px rgba(36, 107, 254, 0.12);
}

.title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  margin-bottom: 7px;
}

.badge {
  height: 22px;
  padding: 0 7px;
  border-radius: 6px;
  display: inline-flex;
  align-items: center;
  flex: 0 0 auto;
  font-size: 12px;
  font-weight: 650;
  background: #eef2f7;
  color: #657282;
}

.badge.task { color: #184b88; background: #e9f1ff; }
.badge.biotest { color: #1f6b50; background: #e8f6ef; }
.badge.project { color: #7b4b11; background: #fff2df; }
.badge.plant { color: #2e6a2f; background: #eef8e9; }
.badge.seed { color: #7b3f28; background: #fff0e8; }

.result-title {
  margin: 0;
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 16px;
}

.summary {
  margin: 0;
  color: #657282;
  font-size: 14px;
  line-height: 1.55;
  word-break: break-word;
}

:deep(mark) {
  color: #0f3f9a;
  background: #dce9ff;
  border-radius: 3px;
  padding: 0 2px;
}

.fields {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-top: 10px;
}

.field {
  height: 26px;
  display: inline-flex;
  align-items: center;
  gap: 5px;
  padding: 0 8px;
  border-radius: 6px;
  background: #f5f7fa;
  color: #657282;
  font-size: 12px;
}

.result-action {
  width: 34px;
  height: 34px;
  border: 1px solid #dde2ea;
  border-radius: 7px;
  background: #fff;
  color: #657282;
}

.pager {
  height: 48px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex: 0 0 auto;
}

.load-more {
  height: 34px;
  min-width: 112px;
  border: 1px solid #cfd7e3;
  border-radius: 7px;
  background: #fff;
}

.empty {
  min-height: 240px;
  border: 1px dashed #bcc7d5;
  border-radius: 8px;
  background: rgba(255, 255, 255, 0.92);
  display: grid;
  place-items: center;
  color: #657282;
  text-align: center;
  padding: 24px;
}

.preview-head {
  padding: 14px;
  border-bottom: 1px solid #dde2ea;
}

.preview-title {
  margin: 0 0 6px;
  font-size: 16px;
  word-break: break-word;
}

.preview-subtitle {
  margin: 0;
  color: #657282;
  font-size: 13px;
}

.kv {
  display: grid;
  gap: 10px;
  font-size: 13px;
}

.kv-row {
  display: grid;
  grid-template-columns: 76px minmax(0, 1fr);
  gap: 10px;
}

.kv-key {
  color: #657282;
}

.kv-value {
  min-width: 0;
  word-break: break-word;
}

.status {
  color: #16805b;
  font-weight: 650;
}

.pending {
  color: #a56714;
  font-weight: 650;
}

.error {
  color: #c24135;
  font-weight: 650;
}

@media (max-width: 980px) {
  .main,
  .main.floating {
    grid-template-columns: 220px minmax(0, 1fr);
  }

  .preview {
    display: none;
  }
}

@media (max-width: 720px) {
  .search-row {
    grid-template-columns: 1fr;
  }

  .search-band.floating {
    top: 12px;
    width: calc(100vw - 28px);
  }

  .main,
  .main.floating {
    top: 150px;
    width: calc(100vw - 28px);
    height: calc(100vh - 164px);
    grid-template-columns: 1fr;
  }

  .primary-button {
    width: 100%;
  }

  .filters {
    order: 2;
  }

  .result-panel {
    order: 1;
  }
}
</style>
