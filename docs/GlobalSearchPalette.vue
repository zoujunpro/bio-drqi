<template>
  <div class="global-search-palette">
    <label class="launcher">
      <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
        <circle cx="11" cy="11" r="7" />
        <path d="m21 21-4.3-4.3" />
      </svg>
      <input
        v-model="launcherKeyword"
        placeholder="搜索工单、项目、样本、载体、物种..."
        autocomplete="off"
        @focus="openPalette"
      />
      <span class="shortcut">⌘ K</span>
    </label>

    <div v-if="open" class="scrim" @click="closePalette"></div>

    <section v-if="open" class="palette">
      <div class="palette-search">
        <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
          <circle cx="11" cy="11" r="7" />
          <path d="m21 21-4.3-4.3" />
        </svg>
        <input
          ref="keywordInputRef"
          v-model.trim="keyword"
          placeholder="输入关键词后回车搜索"
          autocomplete="off"
          @keydown="handleKeydown"
        />
        <button class="search-action" type="button" @click="search">搜索</button>
      </div>

      <div class="tabs">
        <button
          v-for="item in businessTabs"
          :key="item.code"
          class="tab"
          :class="{ active: activeBusiness === item.code }"
          type="button"
          @click="selectBusiness(item.code)"
        >
          {{ item.name }}
        </button>
      </div>

      <div class="content">
        <div v-if="loading" class="empty">正在查询...</div>
        <div v-else-if="errorMessage" class="empty">{{ errorMessage }}</div>
        <div v-else-if="!keyword" class="empty">输入关键词开始搜索</div>
        <div v-else-if="!visibleRecords.length" class="empty">未找到匹配数据</div>
        <template v-else>
          <section v-for="group in groupedRecords" :key="group.code" class="group">
            <div class="group-title">
              {{ businessName(group.code) }}
              <span class="count">{{ group.rows.length }}</span>
            </div>
            <button
              v-for="row in group.rows"
              :key="rowKey(row)"
              class="item"
              :class="{ active: visibleRecords[selectedIndex] === row }"
              type="button"
              @click="selectRow(row)"
              @dblclick="$emit('open', row)"
            >
              <div class="item-main">
                <div class="title-row">
                  <span class="badge" :class="row.business_code">{{ businessName(row.business_code) }}</span>
                  <div class="item-title" v-html="highlight(row.title || row.biz_id || '')"></div>
                </div>
                <p class="summary" v-html="highlight(row.summary || '')"></p>
              </div>
              <div class="side">
                <span>ID: {{ row.biz_id }}</span>
                <span>表: {{ row.table_name }}</span>
                <span v-if="resolveStatus(row)" class="status" :class="resolveStatus(row).className">
                  {{ resolveStatus(row).value }}
                </span>
                <span>{{ formatTime(row.create_time) }}</span>
              </div>
            </button>
          </section>
        </template>
      </div>

      <div class="footer">
        <span>{{ metaText }}</span>
        <button v-if="hasNext" class="load-more" type="button" :disabled="loadingMore" @click="loadMore">
          {{ loadingMore ? '加载中...' : '加载更多' }}
        </button>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, nextTick, onBeforeUnmount, onMounted, ref } from 'vue'

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

const open = ref(false)
const launcherKeyword = ref('')
const keyword = ref('')
const activeBusiness = ref('all')
const records = ref([])
const total = ref(0)
const selectedIndex = ref(0)
const nextSearchAfter = ref(null)
const hasNext = ref(false)
const loading = ref(false)
const loadingMore = ref(false)
const lastCost = ref(0)
const errorMessage = ref('')
const keywordInputRef = ref(null)

const availableBusinessCodes = computed(() => {
  const seen = new Set()
  records.value.forEach(row => {
    if (row.business_code) {
      seen.add(row.business_code)
      if (!businessMap.value[row.business_code]) {
        businessMap.value[row.business_code] = row.business_code
      }
    }
  })
  return Array.from(seen)
})

const businessTabs = computed(() => [
  { code: 'all', name: '全部' },
  ...availableBusinessCodes.value.map(code => ({ code, name: businessName(code) }))
])

const visibleRecords = computed(() => {
  if (activeBusiness.value === 'all') {
    return records.value
  }
  return records.value.filter(row => row.business_code === activeBusiness.value)
})

const groupedRecords = computed(() => {
  const map = new Map()
  visibleRecords.value.forEach(row => {
    const code = row.business_code || 'other'
    if (!map.has(code)) {
      map.set(code, [])
    }
    map.get(code).push(row)
  })
  return Array.from(map.entries()).map(([code, rows]) => ({ code, rows }))
})

const metaText = computed(() => {
  if (!keyword.value) {
    return '输入关键词开始搜索'
  }
  return `共 ${total.value} 条结果 · ${lastCost.value} ms`
})

function openPalette() {
  open.value = true
  keyword.value = launcherKeyword.value
  nextTick(() => {
    keywordInputRef.value?.focus()
    keywordInputRef.value?.select()
  })
}

function closePalette() {
  open.value = false
  launcherKeyword.value = ''
  keyword.value = ''
  activeBusiness.value = 'all'
  records.value = []
  total.value = 0
  selectedIndex.value = 0
  nextSearchAfter.value = null
  hasNext.value = false
  loading.value = false
  loadingMore.value = false
  errorMessage.value = ''
}

async function search() {
  keyword.value = keyword.value.trim()
  errorMessage.value = ''
  activeBusiness.value = 'all'
  nextSearchAfter.value = null
  hasNext.value = false
  selectedIndex.value = 0
  if (!keyword.value) {
    records.value = []
    total.value = 0
    return
  }
  loading.value = true
  const started = Date.now()
  try {
    const data = await requestPage()
    records.value = normalizeRecords(data)
    total.value = typeof data.total === 'number' ? data.total : records.value.length
    nextSearchAfter.value = data.nextSearchAfter || null
    hasNext.value = !!data.hasNext
  } catch (error) {
    records.value = []
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
    records.value = records.value.concat(normalizeRecords(data))
    total.value = typeof data.total === 'number' ? data.total : records.value.length
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
  const list = Array.isArray(data?.records) ? data.records : []
  return list.map(item => item.source || item)
}

function selectBusiness(code) {
  activeBusiness.value = code
  selectedIndex.value = 0
}

function selectRow(row) {
  selectedIndex.value = visibleRecords.value.indexOf(row)
}

function handleKeydown(event) {
  if (event.key === 'Enter') {
    search()
    return
  }
  if (event.key === 'Escape') {
    closePalette()
    return
  }
  if (event.key === 'ArrowDown') {
    event.preventDefault()
    selectedIndex.value = Math.min(visibleRecords.value.length - 1, selectedIndex.value + 1)
    return
  }
  if (event.key === 'ArrowUp') {
    event.preventDefault()
    selectedIndex.value = Math.max(0, selectedIndex.value - 1)
  }
}

function handleGlobalKeydown(event) {
  if ((event.metaKey || event.ctrlKey) && event.key.toLowerCase() === 'k') {
    event.preventDefault()
    openPalette()
  }
}

function businessName(code) {
  return businessMap.value[code] || code || ''
}

function rowKey(row) {
  return `${row.system_code || ''}_${row.business_code || ''}_${row.table_name || ''}_${row.biz_id || ''}`
}

function resolveStatus(row) {
  const display = row.display || {}
  const key = Object.keys(display).find(isStatusKey)
  if (!key) {
    return null
  }
  return {
    value: display[key],
    className: statusClass(display[key])
  }
}

function isStatusKey(key) {
  return key === '状态' || key === '结果' || /状态$/.test(key) || /结果$/.test(key)
}

function statusClass(value) {
  if (value === '未同步') {
    return 'error'
  }
  if (value === '审批中' || value === '待检测') {
    return 'pending'
  }
  return ''
}

function formatTime(value) {
  return value ? String(value).slice(0, 16) : ''
}

function highlight(value) {
  const safe = escapeHtml(String(value || ''))
  if (!keyword.value) {
    return safe
  }
  const pattern = keyword.value.replace(/[.*+?^${}()|[\]\\]/g, '\\$&')
  return safe.replace(new RegExp(pattern, 'gi'), match => `<mark>${match}</mark>`)
}

function escapeHtml(value) {
  return value
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;')
}

onMounted(() => {
  document.addEventListener('keydown', handleGlobalKeydown)
})

onBeforeUnmount(() => {
  document.removeEventListener('keydown', handleGlobalKeydown)
})
</script>

<style scoped>
.global-search-palette {
  color: #17202a;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", "PingFang SC", "Microsoft YaHei", Arial, sans-serif;
}

button,
input {
  font: inherit;
}

button {
  cursor: pointer;
}

.launcher {
  width: min(760px, 100%);
  height: 54px;
  display: grid;
  grid-template-columns: 48px 1fr auto;
  align-items: center;
  border: 1px solid #cfd7e3;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 16px 42px rgba(15, 23, 42, 0.1);
}

.launcher svg {
  margin-left: 18px;
  color: #667485;
}

.launcher input {
  width: 100%;
  min-width: 0;
  border: 0;
  outline: 0;
  font-size: 16px;
}

.shortcut {
  margin-right: 12px;
  height: 28px;
  min-width: 48px;
  display: inline-grid;
  place-items: center;
  border: 1px solid #dfe5ee;
  border-radius: 6px;
  color: #667485;
  background: #f8fafc;
  font-size: 12px;
}

.scrim {
  position: fixed;
  inset: 0;
  z-index: 1000;
  background: rgba(15, 23, 42, 0.76);
  backdrop-filter: blur(5px);
}

.palette {
  position: fixed;
  z-index: 1001;
  top: 56px;
  left: 50%;
  width: min(860px, calc(100vw - 32px));
  max-height: calc(100vh - 112px);
  display: grid;
  grid-template-rows: auto auto minmax(0, 1fr) auto;
  overflow: hidden;
  border: 1px solid rgba(255, 255, 255, 0.28);
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 28px 80px rgba(15, 23, 42, 0.28);
  transform: translateX(-50%);
}

.palette-search {
  height: 64px;
  display: grid;
  grid-template-columns: 54px 1fr auto;
  align-items: center;
  border-bottom: 1px solid #dfe5ee;
  background: #fff;
}

.palette-search svg {
  margin-left: 20px;
  color: #667485;
}

.palette-search input {
  min-width: 0;
  border: 0;
  outline: 0;
  font-size: 17px;
}

.search-action {
  height: 34px;
  margin-right: 14px;
  padding: 0 14px;
  border: 0;
  border-radius: 7px;
  color: #fff;
  background: #2563eb;
  font-weight: 650;
}

.tabs {
  min-height: 46px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 14px;
  overflow-x: auto;
  border-bottom: 1px solid #dfe5ee;
  background: rgba(248, 250, 252, 0.9);
}

.tab {
  height: 30px;
  padding: 0 10px;
  border: 1px solid transparent;
  border-radius: 7px;
  color: #667485;
  background: transparent;
  white-space: nowrap;
}

.tab.active {
  border-color: #bcd0f7;
  color: #2563eb;
  background: #fff;
  font-weight: 650;
}

.content {
  min-height: 0;
  overflow-y: auto;
  overscroll-behavior: contain;
  padding: 8px 10px 10px;
}

.group {
  padding: 6px 0 8px;
}

.group-title {
  height: 28px;
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 0 8px;
  color: #667485;
  font-size: 12px;
  font-weight: 700;
}

.count {
  height: 18px;
  min-width: 22px;
  display: inline-grid;
  place-items: center;
  border-radius: 999px;
  background: #edf2f8;
  color: #64748b;
  font-size: 11px;
}

.item {
  width: 100%;
  min-height: 72px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) auto;
  gap: 12px;
  align-items: center;
  padding: 11px 12px;
  border: 1px solid transparent;
  border-radius: 8px;
  background: transparent;
  text-align: left;
}

.item:hover,
.item.active {
  border-color: #bcd0f7;
  background: #f7faff;
}

.item-main {
  min-width: 0;
}

.title-row {
  display: flex;
  align-items: center;
  gap: 8px;
  min-width: 0;
  margin-bottom: 5px;
}

.badge {
  height: 22px;
  padding: 0 7px;
  display: inline-flex;
  align-items: center;
  border-radius: 6px;
  background: #eef2f7;
  color: #64748b;
  font-size: 12px;
  font-weight: 650;
  flex: 0 0 auto;
}

.badge.task { color: #184b88; background: #eaf1ff; }
.badge.biotest { color: #147d5f; background: #e9f7f1; }
.badge.project { color: #9a5d12; background: #fff3df; }
.badge.plant { color: #2f6f34; background: #edf8e9; }
.badge.seed { color: #8a4a2d; background: #fff0e8; }

.item-title {
  min-width: 0;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  font-size: 15px;
  font-weight: 700;
}

.summary {
  margin: 0;
  overflow: hidden;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  color: #667485;
  font-size: 13px;
  line-height: 1.5;
}

:deep(mark) {
  color: #0f3f9a;
  background: #dce9ff;
  border-radius: 3px;
  padding: 0 2px;
}

.side {
  display: grid;
  justify-items: end;
  gap: 6px;
  color: #667485;
  font-size: 12px;
  white-space: nowrap;
}

.status {
  color: #147d5f;
  font-weight: 700;
}

.status.pending {
  color: #9a5d12;
}

.status.error {
  color: #b83a32;
}

.footer {
  min-height: 46px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  padding: 8px 14px;
  border-top: 1px solid #dfe5ee;
  background: #fff;
  color: #667485;
  font-size: 12px;
}

.load-more {
  height: 30px;
  padding: 0 12px;
  border: 1px solid #cfd7e3;
  border-radius: 7px;
  background: #fff;
}

.load-more:disabled {
  cursor: not-allowed;
  color: #667485;
  background: #f4f6f8;
}

.empty {
  min-height: 260px;
  display: grid;
  place-items: center;
  color: #667485;
  text-align: center;
  padding: 24px;
}

@media (max-width: 640px) {
  .palette {
    top: 14px;
    width: calc(100vw - 20px);
    max-height: calc(100vh - 28px);
  }

  .palette-search {
    grid-template-columns: 46px 1fr;
  }

  .search-action {
    display: none;
  }

  .item {
    grid-template-columns: 1fr;
  }

  .side {
    justify-items: start;
    grid-auto-flow: column;
  }
}
</style>
